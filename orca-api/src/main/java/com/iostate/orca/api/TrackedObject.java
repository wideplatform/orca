package com.iostate.orca.api;

import java.util.Set;

/**
 * E2E change tracking
 */
public interface TrackedObject {

    Object getFieldValue(String name);

    void setFieldValue(String name, Object value);

    /**
     * Named in a getter style for Jackson to recognize
     */
    Set<String> get_updatedFields();
}
