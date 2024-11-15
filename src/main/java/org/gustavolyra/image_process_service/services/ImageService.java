package org.gustavolyra.image_process_service.services;

import lombok.extern.slf4j.Slf4j;
import org.gustavolyra.image_process_service.exceptions.ImageTransformationException;
import org.gustavolyra.image_process_service.exceptions.ResourceNotFoundException;
import org.gustavolyra.image_process_service.exceptions.ReverseProxyException;
import org.gustavolyra.image_process_service.models.dto.ImageDataDto;
import org.gustavolyra.image_process_service.models.dto.ImageDto;
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
            throw new ReverseProxyException("Error fetching image");
        }
    }

    public byte[] applyTransformations(byte[] image, TransformationsDto transformations) {
        try {
            log.info("applying transformations");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
            BufferedImage buffImage = ImageIO.read(inputStream);
            buffImage = applyResize(buffImage, transformations.getResize());
            //converting BufferedImage to byte array...
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(buffImage, "png", outputStream);
            log.info("transformations applied");
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error applying transformations", e);
            throw new ImageTransformationException("Error applying transformations");
        }
    }

    public BufferedImage applyResize(BufferedImage image, Resize resize) {
        log.info("applying resize");
        if (resize != null) {
            return Scalr.resize(image, Scalr.Method.QUALITY, resize.getWidth(), resize.getHeight());
        } else {
            return image;
        }
    }
}
