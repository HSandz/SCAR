package com.scar.lms.exception;

import java.io.Serial;

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
