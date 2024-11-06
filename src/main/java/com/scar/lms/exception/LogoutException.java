package com.scar.lms.exception;

import java.io.Serial;

public class LogoutException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public LogoutException(String message) {
        super(message);
    }

}
