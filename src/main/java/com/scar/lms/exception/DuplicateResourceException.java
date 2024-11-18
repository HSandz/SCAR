package com.scar.lms.exception;

import org.springframework.stereotype.Component;

import java.io.Serial;

@Component
public class DuplicateResourceException extends LibraryException {

    @Serial
    private static final long serialVersionUID = 2L;

    public DuplicateResourceException() {
        super("Duplicate resource");
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
