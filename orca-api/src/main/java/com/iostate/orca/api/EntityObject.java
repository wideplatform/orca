package com.iostate.orca.api;


public interface EntityObject extends TrackedObject {
    /**
     * Internal API.
     */
    boolean persisted();

    /**
     * Internal API.
     */
    void persisted(boolean persisted);

    /**
     * Internal API.
     */
    void populateFieldValue(String name, Object value);

    /**
     * Internal API.
     */
    Object getForeignKeyValue(String name);

    /**
     * Internal API.
     */
    void setForeignKeyValue(String name, Object value);
}
