package com.scar.lms.exception.handler;

import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.InvalidDataException;
import com.scar.lms.exception.OperationNotAllowedException;
import com.scar.lms.exception.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        ex.printStackTrace();
        String errorMessage = "An unexpected error occurred: " + ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(
            ResourceNotFoundException resourceNotFoundException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(resourceNotFoundException.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateResourceException(
            DuplicateResourceException duplicateResourceException) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(duplicateResourceException.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<?> handleInvalidDataException(
            InvalidDataException invalidDataException) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(invalidDataException.getMessage());
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<?>  handleOperationNotAllowedException(
            OperationNotAllowedException operationNotAllowedException) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(operationNotAllowedException.getMessage());
    }
}
