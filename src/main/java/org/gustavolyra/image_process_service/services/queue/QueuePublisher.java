package org.gustavolyra.image_process_service.services.queue;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueuePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;

    public QueuePublisher(RabbitTemplate rabbitTemplate, Queue queue) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    public void send(String order) {
        rabbitTemplate.convertAndSend(this.queue.getName(), order);
    }

}
