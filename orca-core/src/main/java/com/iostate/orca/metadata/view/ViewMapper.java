package com.iostate.orca.metadata.view;

import com.iostate.orca.api.TrackedObject;
import com.iostate.orca.metadata.MetadataManager;

public class ViewMapper {
    private final MetadataManager metadataManager;

    public ViewMapper(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    public <A extends TrackedObject, B extends TrackedObject> void transform(A a, B b) {
        for (String updatedField : a.get_updatedFields()) {
            Object value = a.getFieldValue(updatedField);
            b.setFieldValue(updatedField, value);
        }
    }
}
