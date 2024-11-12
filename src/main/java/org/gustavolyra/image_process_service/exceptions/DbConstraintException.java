package org.gustavolyra.image_process_service.exceptions;

public class DbConstraintException extends RuntimeException {
    public DbConstraintException(String message) {
        super(message);
    }
}
