package com.iostate.orca.metadata;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.cascade.Cascade;
import com.iostate.orca.metadata.dto.FieldDto;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AssociationField extends AbstractField {

    private final EntityModelRef targetModel;
    private final FetchType fetchType;
    private final CascadeType[] cascadeTypes;

    private final transient CascadeConfig cascadeConfig;

    private Field targetInverseField;

    public AssociationField(String name, EntityModelRef targetModel,
                            boolean isId, boolean isNullable, FetchType fetchType, CascadeType[] cascadeTypes) {
        super(name, isId, isNullable);
        this.targetModel = targetModel;
        this.fetchType = fetchType;
        this.cascadeTypes = cascadeTypes;
        this.cascadeConfig = new CascadeConfig(cascadeTypes);
    }

    public EntityModelRef getTargetModel() {
        return targetModel;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public CascadeType[] getCascadeTypes() {
        return cascadeTypes;
    }

    public CascadeConfig cascadeConfig() {
        return cascadeConfig;
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

    @Override
    public final FieldDto toDto() {
        FieldDto dto = new FieldDto();
        dto.setName(getName());
        dto.setColumnName(getColumnName());
        dto.setDataTypeName(getDataType().name());
        dto.setTargetModelName(getTargetModel().getName());
        dto.setNullable(isNullable());
        dto.setFetchType(getFetchType().name());
        dto.setCascadeTypes(Arrays.stream(getCascadeTypes()).map(Enum::name).collect(Collectors.toList()));
        return dto;
    }
}
