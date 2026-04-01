package com.logitrack.exception;

/**
 * Exceção lançada quando um recurso solicitado não é encontrado no banco de dados.
 * Resulta em HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("Recurso '%s' não encontrado com ID: %d", resourceName, id));
    }
}
