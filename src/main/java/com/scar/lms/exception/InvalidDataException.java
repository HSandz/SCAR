package com.scar.lms.exception;

import java.io.Serial;

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
