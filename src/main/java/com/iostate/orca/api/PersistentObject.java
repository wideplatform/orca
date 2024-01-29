package com.iostate.orca.api;


public interface PersistentObject extends TrackedObject {
    boolean isPersisted();

    void setPersisted(boolean persisted);

    Object getForeignKeyValue(String key);

    void setForeignKeyValue(String key, Object value);
}
