package com.scar.lms.exception;

import java.io.Serial;

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
