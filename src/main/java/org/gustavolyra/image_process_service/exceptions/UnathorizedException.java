package org.gustavolyra.image_process_service.exceptions;

public class UnathorizedException extends RuntimeException {
    public UnathorizedException(String message) {
        super(message);
    }
}
