package org.gustavolyra.image_process_service.models.dto.image;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Data
public class ImageDataDto implements Serializable {

    private String fileName;
    private String contentType;
    private UUID userId;
    private byte[] fileData;


}
