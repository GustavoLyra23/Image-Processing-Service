package org.gustavolyra.image_process_service.models.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {

    private String accessToken;


}
