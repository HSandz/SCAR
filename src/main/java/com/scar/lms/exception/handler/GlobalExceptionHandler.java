package com.scar.lms.exception.handler;

import com.scar.lms.exception.LogoutException;
import com.scar.lms.exception.NotFoundException;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException notFoundException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(notFoundException.getMessage());
    }

    @ExceptionHandler(LogoutException.class)
    public ResponseEntity<String> handleLogoutException(LogoutException logoutException) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(logoutException.getMessage());
    }

}
