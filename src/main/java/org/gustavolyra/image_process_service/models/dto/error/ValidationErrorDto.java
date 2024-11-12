package org.gustavolyra.image_process_service.models.dto.error;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ValidationErrorDto {

    private final Set<FieldErrorDto> fieldErrors = new HashSet<>();

    public void addFieldError(FieldErrorDto fieldErrorDto) {
        fieldErrors.add(fieldErrorDto);
    }

}
