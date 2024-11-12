package org.gustavolyra.image_process_service.controller;

import org.gustavolyra.image_process_service.models.dto.auth.AuthResponseDto;
import org.gustavolyra.image_process_service.models.dto.auth.UserDataDto;
import org.gustavolyra.image_process_service.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody UserDataDto userDataDto) {
        var authResponse = authService.login(userDataDto);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserDataDto userDataDto) {
        authService.register(userDataDto);
        return ResponseEntity.noContent().build();
    }


}
