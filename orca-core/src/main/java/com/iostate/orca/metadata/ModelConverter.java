package com.iostate.orca.metadata;

import com.iostate.orca.metadata.dto.EntityModelDto;
import com.iostate.orca.metadata.dto.FieldDto;

public class ModelConverter {
    private final MetadataManager metadataManager;

    public ModelConverter(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    public EntityModel entityModel(EntityModelDto modelDto) {
        EntityModel entityModel = new EntityModel(
                modelDto.getName(),
                modelDto.getTableName(),
                modelDto.getIdGenerator(),
                field(null, modelDto.getIdField(), true)
        );
        for (FieldDto dataFieldDto : modelDto.getDataFields()) {
            entityModel.addDataField(field(entityModel, dataFieldDto, false));
        }
        entityModel.setLinkedClassName(modelDto.getLinkedClassName());
        return entityModel;
    }

    public Field field(EntityModel sourceModel, FieldDto fieldDto, boolean isId) {
        if (fieldDto.getTargetModelName() == null) {
            return new SimpleField(
                    fieldDto.getName(),
                    fieldDto.getColumnName(),
                    SimpleDataType.valueOf(fieldDto.getDataTypeName()),
                    isId,
                    fieldDto.isNullable()
            );
        } else {
            EntityModelRef targetModelRef = new EntityModelRef(fieldDto.getTargetModelName(), metadataManager);
            FetchType fetchType = FetchType.valueOf(fieldDto.getFetchType());
            CascadeType[] cascadeTypes = fieldDto.getCascadeTypes() == null ?
                    null :
                    fieldDto.getCascadeTypes().stream()
                            .map(CascadeType::valueOf)
                            .toArray(CascadeType[]::new);

            String associationType = fieldDto.getAssociationType();
            if ("HasMany".equals(associationType)) {
                checkPrecondition(fieldDto.getMappedByFieldName() != null,
                        String.format("HasMany association %s of model %s must have a mappedByField", fieldDto.getName(), sourceModel.getName()));
                return new HasMany(
                        fieldDto.getName(), sourceModel,
                        targetModelRef, fieldDto.getMappedByFieldName(),
                        fetchType, cascadeTypes
                );
            } else if ("ManyToMany".equals(associationType)) {
                return new ManyToMany(
                        fieldDto.getName(), metadataManager, sourceModel,
                        targetModelRef, fieldDto.getMappedByFieldName(),
                        fetchType, cascadeTypes
                );
            } else if ("HasOne".equals(associationType)) {
                checkPrecondition(fieldDto.getColumnName() == null,
                        String.format("HasOne association %s of model %s must not have a column", fieldDto.getName(), sourceModel.getName()));
                return new HasOne(
                        fieldDto.getName(),
                        sourceModel,
                        targetModelRef,
                        fieldDto.getMappedByFieldName(),
                        fieldDto.isNullable(),
                        fetchType,
                        cascadeTypes
                );
            } else if ("BelongsTo".equals(associationType)) {
                checkPrecondition(fieldDto.getColumnName() != null,
                        String.format("BelongsTo association %s of model %s must have a column", fieldDto.getName(), sourceModel.getName()));
                return new BelongsTo(
                        fieldDto.getName(),
                        fieldDto.getColumnName(),
                        sourceModel,
                        targetModelRef,
                        fieldDto.isNullable(),
                        fetchType,
                        cascadeTypes
                );
            } else {
                throw new IllegalArgumentException(String.format(
                        "Field %s of model %s has unknown type", fieldDto.getName(), sourceModel.getName()
                ));
            }
        }
    }

    private void checkPrecondition(boolean expectation, String message) {
        if (!expectation) {
            throw new IllegalArgumentException(message);
        }
    }
}
