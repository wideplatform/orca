package com.iostate.orca.api.exception;

import java.util.List;

public class InvalidObjectPathException extends PersistenceException {

    InvalidObjectPathException(String message) {
        super(message);
    }

    public static InvalidObjectPathException fieldNotFound(List<String> levels, String fieldName) {
        return new InvalidObjectPathException("In path " + levels + ", field " + fieldName + " is not found");
    }

    public static InvalidObjectPathException outOfBounds(List<String> levels, int offset) {
        return new InvalidObjectPathException(
                "In path " + levels + ", offset " + offset + " is out of bounds, please query a column-based field");
    }
}
