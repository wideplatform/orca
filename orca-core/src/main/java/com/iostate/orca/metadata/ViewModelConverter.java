package com.iostate.orca.metadata;

import com.iostate.orca.metadata.dto.ViewFieldDto;
import com.iostate.orca.metadata.dto.ViewModelDto;
import com.iostate.orca.metadata.view.ViewField;
import com.iostate.orca.metadata.view.ViewModel;

public class ViewModelConverter {
    private final MetadataManager metadataManager;

    public ViewModelConverter(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    public ViewModel viewModel(ViewModelDto modelDto) {
        ViewModel entityModel = new ViewModel(
                modelDto.getName(),
                modelDto.getEntityModelName(),
                field(modelDto.getIdField(), true)
        );
        for (ViewFieldDto dataFieldDto : modelDto.getDataFields()) {
            entityModel.addDataField(field(dataFieldDto, false));
        }
        entityModel.setLinkedClassName(modelDto.getLinkedClassName());
        return entityModel;
    }

    public ViewField field(ViewFieldDto fieldDto, boolean isId) {
        DataType dataType = SimpleDataType.valueOf(fieldDto.getDataTypeName()); // TODO ReferentialDataType
        return new ViewField(fieldDto.getName(), fieldDto.getOriginalName(), dataType, isId, fieldDto.isNullable());
    }
}
