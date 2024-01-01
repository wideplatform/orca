package com.iostate.orca.metadata;

public class EntityModelRef {

    private final String name;
    private final transient MetadataManager metadataManager;

    public EntityModelRef(String name, MetadataManager metadataManager) {
        this.name = name;
        this.metadataManager = metadataManager;
    }

    public String getName() {
        return name;
    }

    public EntityModel model() {
        return metadataManager.findEntityByName(name);
    }
}
