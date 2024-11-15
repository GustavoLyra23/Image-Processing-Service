package org.gustavolyra.image_process_service.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        if (authException instanceof InsufficientAuthenticationException ||
                authException instanceof BadCredentialsException ||
                authException instanceof AuthenticationCredentialsNotFoundException) {

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

        } else {
            if (authException != null) {
                throw authException;
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
        }
    }
}
