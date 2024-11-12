package org.gustavolyra.image_process_service.models.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorDto {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String path;

}
