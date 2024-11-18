package com.scar.lms.exception;

import org.springframework.stereotype.Component;

import java.io.Serial;

@Component
public class ForbiddenException extends LibraryException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ForbiddenException() {
        super("Access forbidden");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}