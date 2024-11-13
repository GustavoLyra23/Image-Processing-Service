package org.gustavolyra.image_process_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class ImageProcessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageProcessServiceApplication.class, args);
    }
}
