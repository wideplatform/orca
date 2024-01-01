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
            EntityModelRef target = new EntityModelRef(fieldDto.getTargetModelName(), metadataManager);
            FetchType fetchType = FetchType.valueOf(fieldDto.getFetchType());
            CascadeType[] cascadeTypes = fieldDto.getCascadeTypes().stream()
                    .map(CascadeType::valueOf)
                    .toArray(CascadeType[]::new);
            if (fieldDto.getDataTypeName().startsWith("<")) {
                PluralAssociationField field = new PluralAssociationField(
                        fieldDto.getName(),
                        target,
                        fetchType,
                        cascadeTypes
                );
//                EntityModelRef source = new EntityModelRef(sourceModel.getName(), metadataManager);
//                field.setMiddleTable(new MiddleTable(source, target));
                return field;
            } else {
                return new SingularAssociationField(
                        fieldDto.getName(),
                        fieldDto.getColumnName(),
                        target,
                        isId,
                        fieldDto.isNullable(),
                        fetchType,
                        cascadeTypes
                );
            }
        }
    }
}
