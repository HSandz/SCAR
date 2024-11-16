package com.scar.lms.exception;

import java.io.Serial;

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