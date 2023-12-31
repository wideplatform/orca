package com.iostate.orca.api.exception;

public class EntityNotFoundException extends PersistenceException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
