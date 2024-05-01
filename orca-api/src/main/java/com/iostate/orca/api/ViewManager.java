package com.iostate.orca.api;

import java.util.List;

public interface ViewManager {
    <T extends ViewObject> T find(Class<T> viewClass, Object id);

    <T extends ViewObject> List<T> findAll(Class<T> viewClass);
}
