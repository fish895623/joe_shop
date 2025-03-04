package com.bit.joe.shoppingmall.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bit.joe.shoppingmall.dto.response.Response;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMessage = new StringBuilder("Validation failed for fields: ");

        // Extract field errors
        bindingResult
                .getFieldErrors()
                .forEach(
                        fieldError ->
                                errorMessage
                                        .append(fieldError.getField())
                                        .append(" - ")
                                        .append(fieldError.getDefaultMessage())
                                        .append("; "));

        // Construct a custom error response
        Response errorResponse =
                Response.builder()
                        .status(400) // Bad Request
                        .message(errorMessage.toString())
                        .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleNotFoundException(NotFoundException ex) {
        // NotFoundException 처리
        Response errorResponse =
                Response.builder()
                        .status(404)
                        .message(ex.getMessage())
                        .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleAllExceptions(Exception ex) {
        // Generic exception handler
        Response errorResponse =
                Response.builder()
                        .status(500) // Internal Server Error
                        .message("An unexpected error occurred: " + ex.getMessage())
                        .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
