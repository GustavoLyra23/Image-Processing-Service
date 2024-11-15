package org.gustavolyra.image_process_service.controller;

import org.gustavolyra.image_process_service.models.dto.ImageDto;
import org.gustavolyra.image_process_service.models.dto.transformations.TransformationsDto;
import org.gustavolyra.image_process_service.services.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/v1/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping()
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        var msg = imageService.uploadImage(file);
        return ResponseEntity.ok(msg);
    }

    @GetMapping()
    public ResponseEntity<Page<ImageDto>> getImages(Pageable pageable) {
        var images = imageService.getImages(pageable);
        return ResponseEntity.ok(images);
    }

    @PostMapping("/{id}/transform")
    public ResponseEntity<byte[]> transformImage(@PathVariable String id, @RequestBody TransformationsDto transformations) {
        byte[] image = imageService.transformImage(UUID.fromString(id), transformations);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png");
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
