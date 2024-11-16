package com.scar.lms.exception;

import java.io.Serial;

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
