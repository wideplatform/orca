package com.iostate.orca.api;


public interface EntityObject extends TrackedObject {
    boolean isPersisted();

    void setPersisted(boolean persisted);

    Object getForeignKeyValue(String name);

    void setForeignKeyValue(String name, Object value);
}
