package com.iostate.orca.api.exception;

public class NonUniqueResultException extends PersistenceException {

    public NonUniqueResultException(String modelName, Object id) {
        super(String.format("model: %s, id: %s", modelName, id));
    }
}
