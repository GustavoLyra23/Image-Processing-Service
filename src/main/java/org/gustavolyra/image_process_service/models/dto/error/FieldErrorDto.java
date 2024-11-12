package org.gustavolyra.image_process_service.models.dto.error;

import lombok.Data;

@Data
public class FieldErrorDto {

    private String field;
    private String message;

}
