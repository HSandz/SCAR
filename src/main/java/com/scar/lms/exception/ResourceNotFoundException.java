package com.scar.lms.exception;

import org.springframework.stereotype.Component;

import java.io.Serial;

@Component
public class ResourceNotFoundException extends LibraryException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException() {
        super("Resource not found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
