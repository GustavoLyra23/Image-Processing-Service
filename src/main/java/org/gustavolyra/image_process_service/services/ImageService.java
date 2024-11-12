package org.gustavolyra.image_process_service.services;

import org.gustavolyra.image_process_service.models.dto.ImageDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {


    @Transactional
    public ImageDto uploadImage(MultipartFile file) {
        //TODO: Implement this method
        return null;
    }
}
