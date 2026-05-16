package com.example.tracking.exception;

public class CourierNotFoundException extends RuntimeException {

    public CourierNotFoundException(String courierId) {
        super("Courier not found: " + courierId);
    }
}