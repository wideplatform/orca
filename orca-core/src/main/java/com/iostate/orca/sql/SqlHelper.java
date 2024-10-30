package com.iostate.orca.sql;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.EntityObject;
import com.iostate.orca.api.exception.EntityNotFoundException;
import com.iostate.orca.api.exception.NonUniqueResultException;
import com.iostate.orca.api.exception.PersistenceException;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.api.query.predicate.Predicates;
import com.iostate.orca.metadata.ManyToMany;
import com.iostate.orca.metadata.MiddleTableImage;
import com.iostate.orca.sql.query.QueryTree;

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

    public void insert(EntityModel entityModel, EntityObject entity) {
        final boolean isIdAssigned = entityModel.getIdValue(entity) != null;

        if (!isIdAssigned && !entityModel.isIdGenerated()) {
            throw new PersistenceException(FAIL_PERSIST + ", no id and no generator, entityName=" + entityModel.getName());
        }

        PersistableRecord record = extractDataToPersist(entityModel, entity, isIdAssigned);

        record.prePersist();

        String columns = String.join(",", record.getColumnValues().keySet());

        String valuePlaceholders = record.getColumnValues().values().stream()
                .map(value -> "?")
                .collect(Collectors.joining(","));

        String sql = String.format("INSERT INTO %s(%s) VALUES (%s)",
                entityModel.getTableName(), columns, valuePlaceholders);
        Object[] args = record.getColumnValues().values().toArray();

        try {
            if (!isIdAssigned && entityModel.isAutoId()) {
                List<Object> keys = executeInsertWithGeneratedKeys(sql, args, new EntityKeyMapper(entityModel));
                if (keys.size() != 1) {
                    throw new PersistenceException(FAIL_PERSIST + ", INSERT returned generated keys count " + keys.size());
                }

                populateId(entityModel, entity, keys.get(0));
            } else {
                int count = executeDML(sql, args);
                if (count != 1) {
                    throw new PersistenceException(FAIL_PERSIST + ", INSERT returned count " + count);
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_PERSIST, e);
        }

        record.postPersist();
    }

    private void populateId(EntityModel entityModel, EntityObject entity, Object id) {
        entityModel.getIdField().populateValue(entity, id);
    }

    private PersistableRecord extractDataToPersist(EntityModel entityModel, EntityObject entity, boolean isIdAssigned) {
        Collection<Field> fields;
        if (isIdAssigned) {
            // Include ID
            fields = entityModel.allFields();
        } else {
            // Empty ID
            fields = entityModel.getDataFields();
        }

        return new PersistableRecord(fields, entity, entityManager);
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

    public void update(EntityModel entityModel, EntityObject entity) {
        PersistableRecord record = extractDataToUpdate(entityModel, entity);

        record.preUpdate();

        String columnsToUpdate = record.getColumnValues().keySet().stream()
                .map(column -> column + " = ?")
                .collect(Collectors.joining(", "));

        String sql = String.format("UPDATE %s SET %s WHERE %s = ?",
                entityModel.getTableName(), columnsToUpdate, entityModel.getIdField().getColumnName());

        Object id = entityModel.getIdValue(entity);

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

    private PersistableRecord extractDataToUpdate(EntityModel entityModel, EntityObject entity) {
        Collection<Field> fields = entityModel.getDataFields();
        return new PersistableRecord(fields, entity, entityManager);
    }

    public void deleteEntity(EntityModel entityModel, EntityObject entity) {
        Object id = entityModel.getIdField().getValue(entity);
        if (shouldCascadeOnEntityDelete(entityModel)) {
            PersistableRecord record = extractDataToUpdate(entityModel, entity);
            record.preDelete();
            sqlDelete(entityModel, id);
            record.postDelete();
        } else {
            sqlDelete(entityModel, id);
        }
    }

    public void deleteById(EntityModel entityModel, Object id) {
        if (shouldCascadeOnEntityDelete(entityModel)) {
            EntityObject entity = findById(entityModel, id);
            PersistableRecord record = extractDataToUpdate(entityModel, entity);
            record.preDelete();
            sqlDelete(entityModel, id);
            record.postDelete();
        } else {
            sqlDelete(entityModel, id);
        }
    }

    private void sqlDelete(EntityModel entityModel, Object id) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?",
                entityModel.getTableName(), entityModel.getIdField().getColumnName());
        try {
            executeDML(sql, new Object[]{id});
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_REMOVE, e);
        }
    }

    private static boolean shouldCascadeOnEntityDelete(EntityModel entityModel) {
        return entityModel.getDataFields().stream()
                .anyMatch(f -> f instanceof AssociationField && ((AssociationField) f).cascadeConfig().isRemove());
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

    public void executeDDL(String sql) throws SQLException {
        if (sql == null) {
            throw new IllegalArgumentException("sql is null");
        }
        try(Statement statement = connection().createStatement()) {
            statement.execute(sql);
            logSql(sql, null);
        }
    }

    public EntityObject findById(EntityModel entityModel, Object id) {
        QueryTree queryTree = new QueryTree(entityModel);
        queryTree.addFilter(Predicates.equal("id", id));

        try{
            List<EntityObject> records = queryTree.execute(connection());
            if (records.isEmpty()) {
                return null;
            } else if (records.size() == 1) {
                return records.get(0);
            } else {
                throw new NonUniqueResultException(entityModel.getName(), id);
            }
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_FIND, e);
        }
    }

    public List<EntityObject> findBy(EntityModel entityModel, String objectPath, Object value) {
        QueryTree queryTree = new QueryTree(entityModel);
        queryTree.addFilter(Predicates.equal(objectPath, value));

        try {
            return queryTree.execute(connection());
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_FIND, e);
        }
    }

    public List<EntityObject> findManyToManyTargets(ManyToMany mtm, Object sourceId) {
        EntityModel targetModel = mtm.getTargetModelRef().model();
        MiddleTableImage middle = mtm.middleTableImage();

        String selectedColumns = selectableColumns(targetModel, "t.");

        String sql = "SELECT " + selectedColumns +
                " FROM " + targetModel.getTableName() + " t JOIN " + middle.getTableName() + " r ON t." +
                targetModel.getIdField().getColumnName() + " = r." + middle.getTargetIdColumn() +
                " WHERE r." + middle.getSourceIdColumn() + " = ?";

        try {
            return executeQuery(sql, new Object[]{sourceId}, new EntityResultMapper(targetModel));
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_FIND, e);
        }
    }

    public List<EntityObject> findAll(EntityModel entityModel) {
        try {
            return new QueryTree(entityModel).execute(connection());
        } catch (SQLException e) {
            throw new PersistenceException(FAIL_FIND, e);
        }
    }

    private String selectableColumns(EntityModel entityModel, String prefix) {
        return entityModel.allFields()
                .stream()
                .filter(Field::hasColumn)
                .map(f -> prefix + f.getColumnName())
                .collect(Collectors.joining(","));
    }

    private List<EntityObject> executeQuery(String sql, Object[] args, ResultMapper resultMapper) throws SQLException {
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
                List<EntityObject> records = new ArrayList<>();
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

    // Connection lifecycle management is delegated to the application framework like Spring
    private Connection connection() {
        return connectionProvider.getConnection();
    }

    private void logSql(String sql, Object[] args) {
        if (args == null) {
            System.out.println("SQL: " + sql);
        } else {
            System.out.printf("SQL: %s, args: %s\n", sql, Arrays.toString(args));
        }
    }
}
