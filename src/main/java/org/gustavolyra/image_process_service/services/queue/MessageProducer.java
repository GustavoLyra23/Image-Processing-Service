package org.gustavolyra.image_process_service.services.queue;

import org.gustavolyra.image_process_service.config.RabbitMQConfig;
import org.gustavolyra.image_process_service.models.dto.ImageDataDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(ImageDataDto message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, message);
    }
}
