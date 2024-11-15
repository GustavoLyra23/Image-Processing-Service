package org.gustavolyra.image_process_service.services;

import lombok.extern.slf4j.Slf4j;
import org.gustavolyra.image_process_service.exceptions.ImageTransformationException;
import org.gustavolyra.image_process_service.exceptions.ResourceNotFoundException;
import org.gustavolyra.image_process_service.exceptions.ReverseProxyException;
import org.gustavolyra.image_process_service.models.dto.ImageDataDto;
import org.gustavolyra.image_process_service.models.dto.ImageDto;
import org.gustavolyra.image_process_service.models.dto.transformations.Crop;
import org.gustavolyra.image_process_service.models.dto.transformations.Filters;
import org.gustavolyra.image_process_service.models.dto.transformations.Resize;
import org.gustavolyra.image_process_service.models.dto.transformations.TransformationsDto;
import org.gustavolyra.image_process_service.repositories.ImageRepository;
import org.gustavolyra.image_process_service.services.queue.MessageProducer;
import org.gustavolyra.image_process_service.utils.AuthUtil;
import org.imgscalr.Scalr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    private final AwsS3Service awsS3Service;
    private final ImageRepository imageRepository;
    private final MessageProducer messageProducer;

    public ImageService(AwsS3Service awsS3Service, ImageRepository imageRepository, MessageProducer messageProducer) {
        this.awsS3Service = awsS3Service;
        this.imageRepository = imageRepository;
        this.messageProducer = messageProducer;
    }


    public String uploadImage(MultipartFile file) {
        try {
            log.info("sending file to queue");
            var user = AuthUtil.getCurrentUser();
            messageProducer.sendMessage(ImageDataDto.builder()
                    .contentType(file.getContentType())
                    .fileName(file.getOriginalFilename())
                    .userId(user.getId())
                    .fileData(file.getBytes())
                    .build());
            return "Sent file to AWS... please wait for processing";
        } catch (IOException e) {
            throw new InternalError("Error uploading image");
        }

    }

    @Transactional(readOnly = true)
    public Page<ImageDto> getImages(Pageable pageable) {
        var user = AuthUtil.getCurrentUser();
        return imageRepository.findByUser(user, pageable).map(ImageDto::new);
    }

    @Transactional
    public byte[] transformImage(UUID id, TransformationsDto transformations) {
        try {
            log.info("transforming image");
            var user = AuthUtil.getCurrentUser();
            var image = imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Image not found"));
            URI uri = URI.create(image.getUrl());
            String key = uri.getPath().substring(1);
            byte[] s3File = awsS3Service.fetchFileFromS3(key);
            return applyTransformations(s3File, transformations);
        } catch (IOException e) {
            log.error("Error fetching image", e);
            throw new ReverseProxyException("Error fetching image from S3");
        }
    }

    public byte[] applyTransformations(byte[] image, TransformationsDto transformations) {
        try {
            log.info("Applying transformations...");
            BufferedImage buffImage = ImageIO.read(new ByteArrayInputStream(image));
            if (transformations.getResize() != null) {
                buffImage = applyResize(buffImage, transformations.getResize());
            }
            if (transformations.getCrop() != null) {
                buffImage = applyCrop(buffImage, transformations.getCrop());
            }
            if (transformations.getRotate() != 0) {
                buffImage = applyRotate(buffImage, transformations.getRotate());
            }
            if (transformations.getFilters() != null) {
                buffImage = applyFilters(buffImage, transformations.getFilters());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(buffImage, "png", outputStream);

            log.info("Transformations applied successfully.");
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error applying transformations", e);
            throw new ImageTransformationException("Error applying transformations");
        }
    }

    public BufferedImage applyResize(BufferedImage image, Resize resize) {
        log.info("Applying resize...");
        return Scalr.resize(image, Scalr.Method.QUALITY, resize.getWidth(), resize.getHeight());
    }

    public BufferedImage applyCrop(BufferedImage image, Crop crop) {
        log.info("Applying crop...");
        return Scalr.crop(image, crop.getX(), crop.getY(), crop.getWidth(), crop.getHeight());
    }

    public BufferedImage applyRotate(BufferedImage image, int rotate) {
        log.info("applying rotate");
        return Scalr.rotate(image, Scalr.Rotation.CW_90);
    }

    public BufferedImage applyFilters(BufferedImage image, Filters filters) {
        log.info("Applying filters...");
        if (Boolean.TRUE.equals(filters.isGrayscale())) {
            image = Scalr.apply(image, Scalr.OP_GRAYSCALE);
        }
        if (Boolean.TRUE.equals(filters.isSepia())) {
            image = applySepia(image);
        }
        return image;
    }

    public BufferedImage applySepia(BufferedImage image) {
        log.info("Applying sepia...");
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                newImage.setRGB(x, y, calculateSepiaColor(p));
            }
        }
        return newImage;
    }

    private int calculateSepiaColor(int p) {
        int a = (p >> 24) & 0xff;
        int r = (p >> 16) & 0xff;
        int g = (p >> 8) & 0xff;
        int b = p & 0xff;

        int tr = Math.min((int) (0.393 * r + 0.769 * g + 0.189 * b), 255);
        int tg = Math.min((int) (0.349 * r + 0.686 * g + 0.168 * b), 255);
        int tb = Math.min((int) (0.272 * r + 0.534 * g + 0.131 * b), 255);

        return (a << 24) | (tr << 16) | (tg << 8) | tb;
    }
}
