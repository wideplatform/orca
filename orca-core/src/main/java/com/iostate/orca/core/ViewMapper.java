package com.iostate.orca.core;

import com.iostate.orca.api.EntityObject;
import com.iostate.orca.api.ViewObject;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.view.ViewField;
import com.iostate.orca.metadata.view.ViewModel;

public class ViewMapper {

    public ViewObject entityToView(EntityObject entity, EntityModel entityModel, ViewModel viewModel) {
        ViewObject view = viewModel.newInstance();
        for (ViewField vf : viewModel.allFields()) {
            String efName = vf.getOriginalName() != null ? vf.getOriginalName() : vf.getName();
            if (entity.get_updatedFields().contains(efName)) {
                view.get_updatedFields().add(vf.getName());
            }
            Field ef = entityModel.findFieldByName(efName);
            // TODO type adapter
            view.setFieldValue(vf.getName(), ef.getValue(entity));
        }
        return view;
    }
}
