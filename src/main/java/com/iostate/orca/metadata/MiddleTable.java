package com.iostate.orca.metadata;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.exception.PersistenceException;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.sql.SqlHelper;

import java.sql.SQLException;

public class MiddleTable {

    private final EntityModelRef sourceModelRef;
    private final EntityModelRef targetModelRef;
    private String _tableName;

    public MiddleTable(EntityModelRef sourceModelRef, EntityModelRef targetModelRef) {
        this.sourceModelRef = sourceModelRef;
        this.targetModelRef = targetModelRef;
    }

    public String getTableName() {
        if (_tableName == null) {
            _tableName = "rel_" + sourceModelRef.model().getTableName()
                    + "_" + targetModelRef.model().getTableName();
        }
        return _tableName;
    }

    public EntityModelRef getSourceModelRef() {
        return sourceModelRef;
    }

    public EntityModelRef getTargetModelRef() {
        return targetModelRef;
    }

    public void put(PersistentObject source, PersistentObject target, EntityManager entityManager) {
        //TODO check existence
        String sql = "INSERT INTO " + getTableName() + "(source_id, target_id) VALUES(?,?)";
        Object sourceId = sourceModelRef.model().getIdField().getValue(source);
        if (sourceId == null) {
            throw new PersistenceException("Failed to insert a relationship, sourceId is null");
        }
        Object targetId = targetModelRef.model().getIdField().getValue(target);
        if (targetId == null) {
            throw new PersistenceException("Failed to insert a relationship, targetId is null");
        }
        try {
            entityManager.executeDML(sql, new Object[]{sourceId, targetId});
        } catch (SQLException e) {
            throw new PersistenceException(
                    String.format("Failed to delete the relationship (sourceId=%s, targetId=%s)", sourceId, targetId),
                    e);
        }
    }

    public void remove(PersistentObject source, PersistentObject target, SqlHelper sqlHelper) {
        String sql = "DELETE FROM " + getTableName() + " WHERE source_id = ? AND target_id = ?";
        Object sourceId = sourceModelRef.model().getIdField().getValue(source);
        if (sourceId == null) {
            throw new PersistenceException("Failed to delete a relationship, sourceId is null");
        }
        Object targetId = targetModelRef.model().getIdField().getValue(target);
        if (targetId == null) {
            throw new PersistenceException("Failed to delete a relationship, targetId is null");
        }
        try {
            sqlHelper.executeDML(sql, new Object[]{sourceId, targetId});
        } catch (SQLException e) {
            throw new PersistenceException(
                    String.format("Failed to delete the relationship (sourceId=%s, targetId=%s)", sourceId, targetId),
                    e);
        }
    }
}
