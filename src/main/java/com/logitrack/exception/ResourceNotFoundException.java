package com.logitrack.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("Recurso '%s' não encontrado com ID: %d", resourceName, id));
    }
}
