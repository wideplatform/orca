package com.iostate.orca.metadata;


import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.cascade.Cascade;
import com.iostate.orca.metadata.cascade.VoidCascade;

/**
 * A virtual association
 */
public class InverseAssociationField extends SingularAssociationField {

    public InverseAssociationField(String name, String columnName,
                                   EntityModel sourceModel, EntityModelRef targetModelRef,
                                   boolean isId, boolean isNullable) {
        super(name, columnName, sourceModel, targetModelRef, isId, isNullable, FetchType.EAGER, null);
    }

    @Override
    public Object getValue(Object entity) {
        PersistentObject po = (PersistentObject) entity;
        return po.getFieldValue(getName());
    }

    @Override
    public void setValue(Object entity, Object value) {
        PersistentObject po = (PersistentObject) entity;
        po.setFieldValue(getName(), value);
    }

    @Override
    public Cascade getCascade(PersistentObject entity) {
        return new VoidCascade();
    }

    @Override
    public boolean isInverse() {
        return true;
    }
}
