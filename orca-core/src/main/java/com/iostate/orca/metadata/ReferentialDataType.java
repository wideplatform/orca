package com.iostate.orca.metadata;

public class ReferentialDataType implements DataType {
    private final EntityModelRef targetModelRef;
    private final boolean isPlural;

    public ReferentialDataType(EntityModelRef targetModelRef, boolean isPlural) {
        this.targetModelRef = targetModelRef;
        this.isPlural = isPlural;
    }

    @Override
    public String name() {
        if (isPlural) {
            return "<" + targetModelRef.getName() + ">";
        } else {
            return targetModelRef.getName();
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
}
