package org.gustavolyra.image_process_service.models.dto.transformations;

import lombok.Data;

@Data
public class Crop {

    private int width;
    private int height;
    private int x;
    private int y;

}
