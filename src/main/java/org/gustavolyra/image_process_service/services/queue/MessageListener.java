package org.gustavolyra.image_process_service.services.queue;

import lombok.extern.slf4j.Slf4j;
import org.gustavolyra.image_process_service.config.RabbitMQConfig;
import org.gustavolyra.image_process_service.exceptions.ResourceNotFoundException;
import org.gustavolyra.image_process_service.exceptions.ReverseProxyException;
import org.gustavolyra.image_process_service.models.dto.ImageDataDto;
import org.gustavolyra.image_process_service.models.entities.Image;
import org.gustavolyra.image_process_service.repositories.ImageRepository;
import org.gustavolyra.image_process_service.repositories.UserRepository;
import org.gustavolyra.image_process_service.services.AwsS3Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
public class MessageListener {

    private final AwsS3Service awsS3Service;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    public MessageListener(AwsS3Service awsS3Service, ImageRepository imageRepository, UserRepository userRepository) {
        this.awsS3Service = awsS3Service;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveMessage(ImageDataDto message) {
        try {
            var url = awsS3Service.sendFileToS3(message);
            var user = userRepository.findById(message.getUserId()).orElseThrow(() -> {
                log.error("User not found");
                return new ResourceNotFoundException("User not found");
            });
            var image = imageRepository.save(Image.builder().url(url).user(user).build());
            System.out.println("Received rabbitmq message");
        } catch (IOException e) {
            throw new ReverseProxyException("Error uploading image");
        }
    }
}
