package com.iostate.orca.metadata;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.cascade.Cascade;
import com.iostate.orca.metadata.cascade.SingularAssociationCascade;

/**
 * field with XToOne association type
 */
public class SingularAssociationField extends AssociationField {

    private final String columnName;
    private final CascadeConfig cascadeConfig;
    private final DataType dataType;

    public SingularAssociationField(String name, String columnName,
                                    EntityModel targetModel,
                                    boolean isId, boolean isNullable, CascadeType[] cascadeTypes, FetchType fetchType) {
        super(name, targetModel, isId, isNullable, fetchType);
        this.columnName = columnName;
        this.cascadeConfig = new CascadeConfig(cascadeTypes);
        this.dataType = new ReferenceDataType(targetModel, false);
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public Cascade getCascade(PersistentObject entity) {
        return new SingularAssociationCascade(this, (PersistentObject) getValue(entity), cascadeConfig);
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
