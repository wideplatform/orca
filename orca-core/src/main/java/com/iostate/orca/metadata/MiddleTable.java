package com.iostate.orca.metadata;

import com.iostate.orca.api.EntityObject;
import com.iostate.orca.api.exception.PersistenceException;
import com.iostate.orca.core.InternalEntityManager;

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

    public String getSourceIdColumnName() {
        return "source_id";
    }

    public String getTargetIdColumnName() {
        return "target_id";
    }

    public void put(EntityObject source, EntityObject target, InternalEntityManager entityManager) {
        Object sourceId = sourceModelRef.model().getIdValue(source);
        if (sourceId == null) {
            throw new PersistenceException("Failed to insert a relationship, sourceId is null");
        }
        Object targetId = targetModelRef.model().getIdValue(target);
        if (targetId == null) {
            throw new PersistenceException("Failed to insert a relationship, targetId is null");
        }
        Object[] args = {sourceId, targetId};

        String select = "SELECT count(1) FROM " + getTableName() + " WHERE source_id = ? AND target_id = ?";
        try {
            long count = entityManager.getSqlHelper().executeCount(select, args);
            if (count > 0) {
                return;
            }
        } catch (SQLException e) {
            throw new PersistenceException(
                    String.format("Failed to query the relationship (sourceId=%s, targetId=%s)", sourceId, targetId),
                    e);
        }

        String insert = "INSERT INTO " + getTableName() + "(source_id, target_id) VALUES(?,?)";
        try {
            entityManager.getSqlHelper().executeDML(insert, args);
        } catch (SQLException e) {
            throw new PersistenceException(
                    String.format("Failed to insert the relationship (sourceId=%s, targetId=%s)", sourceId, targetId),
                    e);
        }
    }

    public void remove(EntityObject source, EntityObject target, InternalEntityManager entityManager) {
        Object sourceId = sourceModelRef.model().getIdValue(source);
        if (sourceId == null) {
            throw new PersistenceException("Failed to delete a relationship, sourceId is null");
        }
        Object targetId = targetModelRef.model().getIdValue(target);
        if (targetId == null) {
            throw new PersistenceException("Failed to delete a relationship, targetId is null");
        }
        String sql = "DELETE FROM " + getTableName() + " WHERE source_id = ? AND target_id = ?";
        try {
            entityManager.getSqlHelper().executeDML(sql, new Object[]{sourceId, targetId});
        } catch (SQLException e) {
            throw new PersistenceException(
                    String.format("Failed to delete the relationship (sourceId=%s, targetId=%s)", sourceId, targetId),
                    e);
        }
    }
}
