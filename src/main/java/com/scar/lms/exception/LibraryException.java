package com.scar.lms.exception;

import org.springframework.stereotype.Component;

import java.io.Serial;

@Component
public class LibraryException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public LibraryException() {
        super();
    }

    public LibraryException(String message) {
        super(message);
    }
}
