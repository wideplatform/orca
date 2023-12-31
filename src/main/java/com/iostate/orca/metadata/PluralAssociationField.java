package com.iostate.orca.metadata;


import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.cascade.Cascade;
import com.iostate.orca.metadata.cascade.PluralAssociationCascade;

import java.util.Collection;

/**
 * field with XToMany association type
 */
public class PluralAssociationField extends AssociationField {

    private final CascadeConfig cascadeConfig;
    private final DataType dataType;

    private MiddleTable middleTable;

    public PluralAssociationField(String name,
                                  EntityModel targetModel,
                                  CascadeType[] cascadeTypes, FetchType fetchType) {
        super(name, targetModel, false, false, fetchType);
        this.cascadeConfig = new CascadeConfig(cascadeTypes);
        this.dataType = new ReferenceDataType(targetModel, true);
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
        return new PluralAssociationCascade(this, (Collection<PersistentObject>) getValue(entity), cascadeConfig);
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

    public void setMiddleTable(MiddleTable middleTable) {
        this.middleTable = middleTable;
    }
}
