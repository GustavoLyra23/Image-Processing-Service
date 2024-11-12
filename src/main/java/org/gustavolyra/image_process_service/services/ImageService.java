package org.gustavolyra.image_process_service.services;

import org.gustavolyra.image_process_service.exceptions.ReverseProxyException;
import org.gustavolyra.image_process_service.models.dto.ImageDto;
import org.gustavolyra.image_process_service.models.entities.Image;
import org.gustavolyra.image_process_service.repositories.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            var url = awsS3Service.sendFileToS3(file);
            var image = imageRepository.save(Image.builder()
                    .url(url)
                    .build());
            return new ImageDto(image);
        } catch (IOException e) {
            throw new ReverseProxyException("Error uploading image");
        }
    }
}
