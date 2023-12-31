package com.iostate.orca.api.exception;

public class NonUniqueResultException extends PersistenceException {
    public NonUniqueResultException(String message) {
        super(message);
    }
}
