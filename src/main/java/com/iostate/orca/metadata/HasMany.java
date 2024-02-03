package com.iostate.orca.metadata;


import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.cascade.Cascade;
import com.iostate.orca.metadata.cascade.HasManyCascade;

public class HasMany extends AssociationField {

    private final DataType dataType;

    public HasMany(
            String name,
            EntityModel sourceModel, EntityModelRef targetModelRef, String mappedByFieldName,
            FetchType fetchType, CascadeType[] cascadeTypes) {
        super(name, sourceModel, targetModelRef, mappedByFieldName, false, fetchType, cascadeTypes);
        this.dataType = new ReferenceDataType(targetModelRef, true);
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
        return new HasManyCascade(this, entity, cascadeConfig());
    }

    @Override
    public boolean isSingular() {
        return false;
    }

    @Override
    public boolean isPlural() {
        return true;
    }
}
