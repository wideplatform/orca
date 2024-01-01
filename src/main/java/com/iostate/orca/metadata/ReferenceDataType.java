package com.iostate.orca.metadata;

public class ReferenceDataType implements DataType {
    private final EntityModelRef targetModel;
    private final boolean isPlural;

    public ReferenceDataType(EntityModelRef targetModel, boolean isPlural) {
        this.targetModel = targetModel;
        this.isPlural = isPlural;
    }

    @Override
    public String name() {
        if (isPlural) {
            return "<" + targetModel.getName() + ">";
        } else {
            return targetModel.getName();
        }
    }

    @Override
    public String javaTypeName() {
        if (isPlural) {
            return "java.util.List" + name();
        } else {
            return name();
        }
    }

    public EntityModelRef getTargetModel() {
        return targetModel;
    }
}
