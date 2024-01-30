package com.iostate.orca.metadata;


import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.cascade.Cascade;
import com.iostate.orca.metadata.cascade.HasAndBelongsToManyCascade;

import java.util.Collection;

public class HasAndBelongsToMany extends AssociationField {

    private final DataType dataType;
    private final MiddleTable middleTable;

    public HasAndBelongsToMany(
            String name, MetadataManager metadataManager,
            EntityModel sourceModel, EntityModelRef targetModelRef,
            FetchType fetchType, CascadeType[] cascadeTypes) {
        super(name, sourceModel, targetModelRef, null, false, fetchType, cascadeTypes);
        this.dataType = new ReferenceDataType(targetModelRef, true);
        this.middleTable = new MiddleTable(new EntityModelRef(sourceModel.getName(), metadataManager), targetModelRef);
    }

    @Override
    public String getColumnName() {
        return null;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Cascade getCascade(PersistentObject entity) {
        return new HasAndBelongsToManyCascade(this, (Collection<PersistentObject>) getValue(entity), cascadeConfig());
    }

    @Override
    public boolean isSingular() {
        return false;
    }

    @Override
    public boolean isPlural() {
        return true;
    }

    public MiddleTable getMiddleTable() {
        return middleTable;
    }
}
