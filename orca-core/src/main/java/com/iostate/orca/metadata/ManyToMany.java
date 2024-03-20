package com.iostate.orca.metadata;


import com.iostate.orca.api.EntityObject;
import com.iostate.orca.metadata.cascade.Cascade;
import com.iostate.orca.metadata.cascade.ManyToManyCascade;

public class ManyToMany extends AssociationField {

    private final DataType dataType;
    private final MiddleTable middleTable;

    public ManyToMany(
            String name, MetadataManager metadataManager,
            EntityModel sourceModel, EntityModelRef targetModelRef, String mappedByFieldName,
            FetchType fetchType, CascadeType[] cascadeTypes) {
        super(name, sourceModel, targetModelRef, mappedByFieldName, false, fetchType, cascadeTypes);
        this.dataType = new ReferentialDataType(targetModelRef, true);
        if (mappedByFieldName == null) {
            this.middleTable = new MiddleTable(new EntityModelRef(sourceModel.getName(), metadataManager), targetModelRef);
        } else {
            this.middleTable = null;
        }
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
    public Cascade getCascade(EntityObject entity) {
        return new ManyToManyCascade(this, entity, cascadeConfig());
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

    public MiddleTableImage middleTableImage() {
        return new MiddleTableImage(this);
    }
}
