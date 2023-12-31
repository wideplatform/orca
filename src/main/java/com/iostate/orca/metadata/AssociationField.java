package com.iostate.orca.metadata;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.cascade.Cascade;

public abstract class AssociationField extends AbstractField {

    private final EntityModel targetModel;
    private final FetchType fetchType;

    private Field targetInverseField;

    public AssociationField(String name, EntityModel targetModel,
                            boolean isId, boolean isNullable, FetchType fetchType) {
        super(name, isId, isNullable);
        this.targetModel = targetModel;
        this.fetchType = fetchType;
    }

    public EntityModel getTargetModel() {
        return targetModel;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    @Override
    public boolean isAssociation() {
        return true;
    }

    public abstract Cascade getCascade(PersistentObject entity);

    public abstract boolean isSingular();

    public abstract boolean isPlural();

    public boolean isInverse() {
        return false;
    }

    public Field getTargetInverseField() {
        return targetInverseField;
    }

    public void setTargetInverseField(Field targetInverseField) {
        this.targetInverseField = targetInverseField;
    }
}
