package org.gustavolyra.image_process_service.models.dto.transformations;

import lombok.Data;

@Data
public class TransformationsDto {

    private Double rotate;
    private String format;
    private Crop crop;
    private Resize resize;
    private Filters filters;

}
