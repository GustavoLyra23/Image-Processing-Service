package org.gustavolyra.image_process_service.controller.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.gustavolyra.image_process_service.exceptions.DbConstraintException;
import org.gustavolyra.image_process_service.exceptions.ResourceNotFoundException;
import org.gustavolyra.image_process_service.exceptions.ReverseProxyException;
import org.gustavolyra.image_process_service.exceptions.UnathorizedException;
import org.gustavolyra.image_process_service.models.dto.error.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UnathorizedException.class)
    public ResponseEntity<ErrorDto> handleUnathorizedException(UnathorizedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        var err = new ErrorDto(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DbConstraintException.class)
    public ResponseEntity<ErrorDto> handleDbConstraintException(DbConstraintException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        var err = new ErrorDto(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUsernameNotFoundException(UsernameNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        var err = new ErrorDto(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ReverseProxyException.class)
    public ResponseEntity<ErrorDto> handleReverseProxyException(ReverseProxyException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        var err = new ErrorDto(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        var err = new ErrorDto(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

}
