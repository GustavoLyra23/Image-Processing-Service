package org.gustavolyra.image_process_service.controller;

import org.gustavolyra.image_process_service.models.dto.ImageDto;
import org.gustavolyra.image_process_service.services.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }


    @PostMapping()
    public ResponseEntity<ImageDto> uploadImage(@RequestParam("file") MultipartFile file) {
        var image = imageService.uploadImage(file);
        return ResponseEntity.ok(image);
    }
}
