package org.gustavolyra.image_process_service.models.dto.transformations;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class TransformationsDto implements Serializable {
    private int rotate;
    private String format;
    private Crop crop;
    private Resize resize;
    private Filters filters;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransformationsDto that = (TransformationsDto) o;
        if (rotate != that.rotate) return false;
        if (!Objects.equals(format, that.format)) return false;
        if (!Objects.equals(crop, that.crop)) return false;
        if (!Objects.equals(resize, that.resize)) return false;
        return Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        int result = rotate;
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (crop != null ? crop.hashCode() : 0);
        result = 31 * result + (resize != null ? resize.hashCode() : 0);
        result = 31 * result + (filters != null ? filters.hashCode() : 0);
        return result;
    }
}
