package com.scar.lms.exception;

import org.springframework.stereotype.Component;

import java.io.Serial;

@Component
public class InvalidDataException extends LibraryException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidDataException() {
        super("Invalid data");
    }

    public InvalidDataException(String message) {
        super(message);
    }
}
