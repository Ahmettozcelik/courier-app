package com.example.tracking.exception;

import com.example.tracking.util.TraceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CourierNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourierNotFound(CourierNotFoundException  ex ) {

        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                TraceUtil.getTraceId(),
                Instant.now().toString()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }
}