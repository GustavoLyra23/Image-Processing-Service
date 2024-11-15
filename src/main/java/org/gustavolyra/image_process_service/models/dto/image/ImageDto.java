package org.gustavolyra.image_process_service.models.dto.image;

import lombok.Data;
import org.gustavolyra.image_process_service.models.entities.Image;

import java.util.UUID;

@Data
public class ImageDto {

    UUID id;
    String url;

    public ImageDto(Image image) {
        this.id = image.getUuid();
        this.url = image.getUrl();
    }


}
