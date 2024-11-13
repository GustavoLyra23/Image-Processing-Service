package org.gustavolyra.image_process_service.services;

import org.gustavolyra.image_process_service.exceptions.ResourceNotFoundException;
import org.gustavolyra.image_process_service.exceptions.ReverseProxyException;
import org.gustavolyra.image_process_service.models.dto.ImageDto;
import org.gustavolyra.image_process_service.models.dto.transformations.TransformationsDto;
import org.gustavolyra.image_process_service.models.entities.Image;
import org.gustavolyra.image_process_service.repositories.ImageRepository;
import org.gustavolyra.image_process_service.utils.AuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {
    private final AwsS3Service awsS3Service;
    private final ImageRepository imageRepository;

    public ImageService(AwsS3Service awsS3Service, ImageRepository imageRepository) {
        this.awsS3Service = awsS3Service;
        this.imageRepository = imageRepository;
    }

    @Transactional
    public ImageDto uploadImage(MultipartFile file) {
        try {
            var user = AuthUtil.getCurrentUser();
            var url = awsS3Service.sendFileToS3(file);
            var image = imageRepository.save(Image.builder()
                    .url(url)
                    .user(user)
                    .build());

            return new ImageDto(image);
        } catch (IOException e) {
            throw new ReverseProxyException("Error uploading image");
        }
    }

    @Transactional(readOnly = true)
    public Page<ImageDto> getImages(Pageable pageable) {
        var user = AuthUtil.getCurrentUser();
        return imageRepository.findByUser(user, pageable).map(ImageDto::new);
    }

    @Transactional
    public void transformImage(UUID id, TransformationsDto transformations) {
        try {
            var user = AuthUtil.getCurrentUser();
            var image = imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Image not found"));
            byte[] s3File = awsS3Service.fetchFileFromS3(image.getUrl());
//            Scalr.resize(s3File);


        } catch (IOException e) {
            throw new ReverseProxyException("Error fetching image");

        }
    }
}
