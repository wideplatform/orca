package com.iostate.orca.sql;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.exception.EntityNotFoundException;
import com.iostate.orca.api.exception.NonUniqueResultException;
import com.iostate.orca.api.exception.PersistenceException;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.MiddleTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlHelper {

    private static final String FAIL_PERSIST = "Failed to persist";
    private static final String FAIL_UPDATE = "Failed to update";
    private static final String FAIL_REMOVE = "Failed to remove";
    private static final String FAIL_FIND = "Failed to find";

    private final ConnectionProvider connectionProvider;
    private final EntityManager entityManager;

    public SqlHelper(ConnectionProvider connectionProvider, EntityManager entityManager) {
        this.connectionProvider = connectionProvider;
        this.entityManager = entityManager;
    }

    public void insert(EntityModel entityModel, PersistentObject po) {
        final boolean isIdAssigned = getId(entityModel, po) != null;

        if (!isIdAssigned && !entityModel.isIdGenerated()) {
            throw new PersistenceException(FAIL_PERSIST + ", no id and no generator, entityName=" + entityModel.getName());
        }

        PersistableRecord record = extractDataToPersist(entityModel, po, isIdAssigned);

        record.prePersist();

        String columns = String.join(",", record.getColumnValues().keySet());

        String valuePlaceholders = record.getColumnValues().values().stream()
                .map(value -> "?")
                .collect(Collectors.joining(","));

        String sql = String.format("INSERT INTO %s(%s) VALUES (%s)",
                entityModel.getTableName(), columns, valuePlaceholders);

        Object[] args = record.getColumnValues().values().toArray();

        try {
            if (!isIdAssigned && entityModel.isIdGenerated()) {
                List<Object> keys = executeInsertWithGeneratedKeys(sql, args, new EntityKeyMapper(entityModel));
                if (keys.size() != 1) {
                    throw new PersistenceException(FAIL_PERSIST + ", INSERT returned generated keys count " + keys.size());
                }

                setId(entityModel, po, keys.get(0));
            } else {
                int count = executeDML(sql, args);
                if (count != 1) {
                    throw new PersistenceException(FAIL_PERSIST + ", INSERT returned count " + count);
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_PERSIST, e);
        }

        po.setPersisted(true);

        record.postPersist();
    }

    private Object getId(EntityModel entityModel, PersistentObject po) {
        return entityModel.getIdField().getValue(po);
    }

    private void setId(EntityModel entityModel, PersistentObject po, Object id) {
        entityModel.getIdField().setValue(po, id);
    }

    private PersistableRecord extractDataToPersist(EntityModel entityModel, PersistentObject po, boolean isIdAssigned) {
        Collection<Field> fields;
        if (isIdAssigned) {
            // Include ID
            fields = entityModel.allFields();
        } else {
            // Empty ID
            fields = entityModel.getDataFields();
        }

        return new PersistableRecord(fields, po, entityManager);
    }

    public List<Object> executeInsertWithGeneratedKeys(String sql, Object[] args, KeyMapper keyMapper) throws SQLException {
        if (sql == null) {
            throw new IllegalArgumentException("sql is null");
        }

        try (PreparedStatement ps = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }

            logSql(sql, args);
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new PersistenceException(FAIL_PERSIST + ", INSERT returned count " + count);
            }

            try (ResultSet keySet = ps.getGeneratedKeys()) {
                List<Object> keys = new ArrayList<>();
                while (keySet.next()) {
                    keys.add(keyMapper.mapKey(keySet));
                }
                return keys;
            }
        }
    }

    public void update(EntityModel entityModel, PersistentObject po) {
        PersistableRecord record = extractDataToUpdate(entityModel, po);

        record.preUpdate();

        String columnsToUpdate = record.getColumnValues().keySet().stream()
                .map(column -> column + " = ?")
                .collect(Collectors.joining(", "));

        String sql = String.format("UPDATE %s SET %s WHERE %s = ?",
                entityModel.getTableName(), columnsToUpdate, entityModel.getIdField().getColumnName());

        Object id = getId(entityModel, po);

        Object[] args = Stream.concat(record.getColumnValues().values().stream(), Stream.of(id))
                .toArray();

        try {
            int count = executeDML(sql, args);
            if (count == 0) {
                throw new EntityNotFoundException(String.format(FAIL_UPDATE + ", entityName: %s, id: %s", entityModel.getName(), id));
            } else if (count != 1) {
                throw new PersistenceException(FAIL_UPDATE + ", returned count " + count);
            }
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_UPDATE, e);
        }

        record.postUpdate();
    }

    private PersistableRecord extractDataToUpdate(EntityModel entityModel, PersistentObject po) {
        Collection<Field> fields = entityModel.getDataFields();
        return new PersistableRecord(fields, po, entityManager);
    }

    public void delete(EntityModel entityModel, Object id) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?",
                entityModel.getTableName(), entityModel.getIdField().getColumnName());

        try {
            executeDML(sql, new Object[]{id});
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_REMOVE, e);
        }

        //TODO cascade delete
    }

    public int executeDML(String sql, Object[] args) throws SQLException {
        if (sql == null) {
            throw new IllegalArgumentException("sql is null");
        }

        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }

            logSql(sql, args);
            return ps.executeUpdate();
        }
    }

    public PersistentObject find(EntityModel entityModel, Object id) {
        String selectedColumns = selectableColumns(entityModel);

        String sql = String.format("SELECT %s FROM %s WHERE %s = ?",
                selectedColumns, entityModel.getTableName(), entityModel.getIdField().getColumnName());

        PersistentObject result;
        try {
            List<PersistentObject> records = executeQuery(sql, new Object[]{id}, new EntityResultMapper(entityModel));

            if (records.isEmpty()) {
                return null;
            } else if (records.size() == 1) {
                result = records.get(0);
            } else {
                //TODO what exception type to use? Should rollback
                throw new NonUniqueResultException(String.format("entityName: %s, id: %s", entityModel.getName(), id));
            }
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_FIND, e);
        }

        return result;
    }

    public List<PersistentObject> findByField(EntityModel entityModel, Field field, Object value) {
        String selectedColumns = selectableColumns(entityModel);

        String sql = String.format("SELECT %s FROM %s WHERE %s = ?",
                selectedColumns, entityModel.getTableName(), field.getColumnName());

        try {
            return executeQuery(sql, new Object[]{value}, new EntityResultMapper(entityModel));
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_FIND, e);
        }

    }

    public List<PersistentObject> findByRelation(MiddleTable middleTable, Object sourceId) {
        EntityModel targetModel = middleTable.getTargetModel().model();

        String selectedColumns = selectableColumns(targetModel, "t.");

        String sql = "SELECT " + selectedColumns +
                " FROM " + targetModel.getTableName() + " t JOIN " + middleTable.getTableName() + " r ON t." +
                targetModel.getIdField().getColumnName() + " = r.target_id WHERE r.source_id = ?";

        try {
            return executeQuery(sql, new Object[]{sourceId}, new EntityResultMapper(targetModel));
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_FIND, e);
        }
    }

    private String selectableColumns(EntityModel entityModel) {
        return selectableColumns(entityModel, "");
    }

    private String selectableColumns(EntityModel entityModel, String prefix) {
        return entityModel.allFields()
                .stream()
                .filter(Field::hasColumn)
                .map(f -> prefix + f.getColumnName())
                .collect(Collectors.joining(","));
    }

    private List<PersistentObject> executeQuery(String sql, Object[] args, ResultMapper resultMapper) throws SQLException {
        if (sql == null) {
            throw new IllegalArgumentException("sql is null");
        }

        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }

            logSql(sql, args);
            try (ResultSet rs = ps.executeQuery()) {
                List<PersistentObject> records = new ArrayList<>();
                while (rs.next()) {
                    records.add(resultMapper.mapRow(rs));
                }
                return records;
            }
        }
    }

    public long executeCount(String sql, Object[] args) throws SQLException {
        if (sql == null) {
            throw new IllegalArgumentException("sql is null");
        }

        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    return 0L;
                }
            }
        }
    }

    private Connection connection() {
        try {
            return connectionProvider.getConnection();
        } catch (SQLException e) {
            throw new PersistenceException("Failed to getConnection()", e);
        }
    }

    private void logSql(String sql, Object[] args) {
        System.out.printf("SQL: %s, args: %s\n", sql, Arrays.toString(args));
    }

}
