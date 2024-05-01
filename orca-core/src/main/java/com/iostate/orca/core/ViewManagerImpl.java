package com.iostate.orca.core;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.EntityObject;
import com.iostate.orca.api.ViewManager;
import com.iostate.orca.api.ViewObject;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.metadata.view.ViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class ViewManagerImpl implements ViewManager {
    private final MetadataManager metadataManager;
    private final EntityManager entityManager;

    public ViewManagerImpl(MetadataManager metadataManager, EntityManager entityManager) {
        this.metadataManager = metadataManager;
        this.entityManager = entityManager;
    }

    @Override
    public <T extends ViewObject> T find(Class<T> viewClass, Object id) {
        ViewModel viewModel = metadataManager.findViewByClass(viewClass);
        EntityModel entityModel = metadataManager.findEntityByName(viewModel.getEntityModelName());
        EntityObject entity = entityManager.find(entityModel.getName(), id);
        ViewMapper viewMapper = new ViewMapper();
        //noinspection unchecked
        return (T) viewMapper.entityToView(entity, entityModel, viewModel);
    }

    @Override
    public <T extends ViewObject> List<T> findAll(Class<T> viewClass) {
        ViewModel viewModel = metadataManager.findViewByClass(viewClass);
        EntityModel entityModel = metadataManager.findEntityByName(viewModel.getEntityModelName());
        List<EntityObject> entities = entityManager.findAll(entityModel.getName());
        ViewMapper viewMapper = new ViewMapper();
        //noinspection unchecked
        return entities.stream()
                .map(entity -> (T) viewMapper.entityToView(entity, entityModel, viewModel))
                .collect(Collectors.toList());
    }
}
