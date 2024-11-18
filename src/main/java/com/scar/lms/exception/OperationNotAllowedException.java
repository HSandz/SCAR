package com.scar.lms.exception;

import org.springframework.stereotype.Component;

import java.io.Serial;

@Component
public class OperationNotAllowedException extends LibraryException {

    @Serial
    private static final long serialVersionUID = 1L;

    public OperationNotAllowedException() {
        super("Operation not allowed");
    }

    public OperationNotAllowedException(String message) {
        super(message);
    }
}
