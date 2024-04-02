package com.iostate.orca.sql.query;

import com.iostate.orca.api.EntityObject;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.ManyToMany;
import com.iostate.orca.metadata.MiddleTableImage;
import com.iostate.orca.metadata.EntityObjectUtils;
import com.iostate.orca.api.query.predicate.Predicate;
import com.iostate.orca.api.query.predicate.Predicates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class AdditionTree {
    private final AssociationField associationField;
    private final EntityModel targetModel;
    private final CacheContext cacheContext;

    private final List<EntityObject> sources = new ArrayList<>();

    AdditionTree(AssociationField associationField, CacheContext cacheContext) {
        this.associationField = associationField;
        this.targetModel = associationField.getTargetModelRef().model();
        this.cacheContext = cacheContext;
    }

    void addSource(EntityObject source) {
        sources.add(source);
    }

    void complete(Connection connection) throws SQLException {
        if (sources.isEmpty()) {
            return;
        }

        if (associationField instanceof ManyToMany) {
            executeForManyToManyField(connection);
        } else if (associationField.hasColumn()) {
            executeForColumnedField(connection);
        } else {
            executeForMappedField(connection);
        }
    }

    private void executeForColumnedField(Connection connection) throws SQLException {
        Field targetIdField = targetModel.getIdField();
        // Multiple sources may point to the same target
        MultiMap<Object, EntityObject> groupedSources = new MultiMap<>();
        for (EntityObject source : sources) {
            Object fkValue = source.getForeignKeyValue(associationField.getColumnName());
            if (fkValue == null) {
                continue;
            }
            EntityObject cachedTarget = cacheContext.get(targetModel, fkValue);
            if (cachedTarget == null) {
                groupedSources.put(fkValue, source);
            } else {
                associationField.populateValue(source, cachedTarget);
            }
        }

        if (groupedSources.isEmpty()) {
            return;
        }

        Predicate filter = Predicates.in(targetIdField.getName(), groupedSources.keySet());
        List<EntityObject> allTargets = new QueryTree(targetModel, cacheContext)
                .addFilter(filter)
                .execute(connection);
        for (EntityObject target : allTargets) {
            Object fkValue = targetIdField.getValue(target);
            for (EntityObject source : groupedSources.get(fkValue)) {
                associationField.populateValue(source, target);
            }
        }
    }

    private void executeForMappedField(Connection connection) throws SQLException {
        Field sourceIdField = associationField.getSourceModel().getIdField();

        Map<Object, EntityObject> idsToSources = new HashMap<>();
        for (EntityObject source : sources) {
            Object id = sourceIdField.getValue(source);
            idsToSources.put(id, source);
        }

        Field fkField = associationField.getMappedByField();
        Predicate filter = Predicates.in(fkField.getName(), idsToSources.keySet());
        List<EntityObject> allTargets = new QueryTree(targetModel, cacheContext)
                .addFilter(filter)
                .execute(connection);
        // Multiple targets may point to the same source
        Map<Object, List<EntityObject>> groupedTargets = allTargets.stream()
                .collect(Collectors.groupingBy(t -> t.getForeignKeyValue(fkField.getColumnName())));
        groupedTargets.forEach((key, group) -> {
            EntityObject source = idsToSources.get(key);
            if (associationField.isPlural()) {
                associationField.populateValue(source, group);
            } else {
                if (group.size() > 0) {
                    associationField.populateValue(source, group.get(0));
                }
            }
        });
    }

    private void executeForManyToManyField(Connection connection) throws SQLException {
        EntityModel sourceModel = associationField.getSourceModel();
        Map<Object, EntityObject> idsToSources = EntityObjectUtils.indexById(sourceModel, sources);

        MultiMap<Object, Object> sourceIdsToTargetIds = new MultiMap<>();
        MiddleTableImage middle = ((ManyToMany) associationField).middleTableImage();
        String middleTableQuery = "SELECT " + middle.getSourceIdColumn() + ", " + middle.getTargetIdColumn()
                + " FROM " + middle.getTableName()
                + " WHERE " + middle.getSourceIdColumn() + " IN "
                + idsToSources.keySet().stream().map(x -> "?").collect(Collectors.joining(",", "(", ")"));
        try (PreparedStatement ps = connection.prepareStatement(middleTableQuery)) {
            int i = 1;
            for (Object key : idsToSources.keySet()) {
                ps.setObject(i, key);
                i++;
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sourceIdsToTargetIds.put(rs.getObject(1), rs.getObject(2));
                }
            }
        }

        List<EntityObject> allTargets = new QueryTree(targetModel, cacheContext)
                .addFilter(Predicates.in(targetModel.getIdField().getName(), sourceIdsToTargetIds.valueSet()))
                .execute(connection);
        Map<Object, EntityObject> idsToTargets = EntityObjectUtils.indexById(targetModel, allTargets);
        idsToSources.forEach((sourceId, source) -> {
            List<Object> targetIds = sourceIdsToTargetIds.get(sourceId);
            if (targetIds != null) {
                List<EntityObject> targets = targetIds.stream().map(idsToTargets::get).collect(Collectors.toList());
                associationField.populateValue(source, targets);
            }
        });
    }
}
