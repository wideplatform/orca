package com.iostate.orca.metadata;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.cascade.Cascade;
import com.iostate.orca.metadata.cascade.HasOneCascade;

public class HasOne extends AssociationField {

    private final DataType dataType;

    public HasOne(
            String name,
            EntityModel sourceModel, EntityModelRef targetModelRef, String mappedByFieldName,
            boolean isNullable, FetchType fetchType, CascadeType[] cascadeTypes) {
        super(name, sourceModel, targetModelRef, mappedByFieldName, isNullable, fetchType, cascadeTypes);
        this.dataType = new ReferenceDataType(targetModelRef, false);
    }

    @Override
    public String getColumnName() {
        return null;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public Cascade getCascade(PersistentObject entity) {
        return new HasOneCascade(this, (PersistentObject) getValue(entity), cascadeConfig());
    }

    @Override
    public boolean isSingular() {
        return true;
    }

    @Override
    public boolean isPlural() {
        return false;
    }

}
