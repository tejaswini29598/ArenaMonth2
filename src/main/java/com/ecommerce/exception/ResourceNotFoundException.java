package com.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ResourceNotFoundException withId(String resource, Long id) {
        return new ResourceNotFoundException(resource + " not found with id: " + id);
    }
}
