package com.iostate.orca.sql.query;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.api.exception.InvalidObjectPathException;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.HasAndBelongsToMany;
import com.iostate.orca.metadata.MiddleTableImage;
import com.iostate.orca.query.ConcreteSqlBuilder;
import com.iostate.orca.query.SqlBuilder;
import com.iostate.orca.query.predicate.Predicate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Entrance of object-oriented query model
 */
public class QueryTree {
    private final QueryContext queryContext;
    private final QueryRootNode root;
    private final List<Predicate> filters = new ArrayList<>();

    public QueryTree(EntityModel entityModel) {
        this(entityModel, new CacheContext());
    }

    public QueryTree(EntityModel entityModel, CacheContext cacheContext) {
        queryContext = new QueryContext(cacheContext);
        root = new QueryRootNode(entityModel, queryContext);
    }

    public QueryTree addFilter(Predicate filter) {
        filters.add(filter);
        return this;
    }

    public SqlObject toSqlObject() {
        ConcreteSqlBuilder sqlBuilder = new ConcreteSqlBuilder(this);
        sqlBuilder.addString("SELECT ");
        for (SelectedField sf : queryContext.columnIndexGenerator.getSelectedFields()) {
            sqlBuilder.addSelectColumn(sf.getTableAlias(), sf.getField().getColumnName());
        }

        sqlBuilder.addString(" FROM ");
        root.buildTableClauses(sqlBuilder);
        if (filters.isEmpty()) {
            return new SqlObject(sqlBuilder.toSql(), new Object[]{});
        }

        sqlBuilder.addString(" WHERE ");
        int filterCount = 0;
        for (Predicate filter : filters) {
            if (filterCount > 0) {
                sqlBuilder.addString(" AND ");
            }
            filter.accept(sqlBuilder);
            filterCount++;
        }
        return new SqlObject(sqlBuilder.toSql(), sqlBuilder.getArguments().toArray());
    }

    public List<PersistentObject> execute(Connection connection) throws SQLException {
        SqlObject sqlObject = toSqlObject();
        String sql = sqlObject.getSql();
        Object[] args = sqlObject.getArguments();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }

            logSql(sql, args);
            try (ResultSet rs = ps.executeQuery()) {
                root.mapRows(rs);
            }
        }

        Collection<PersistentObject> results = root.complete(connection);
        return new ArrayList<>(results);
    }

    private void logSql(String sql, Object[] args) {
        System.out.printf("QueryTree SQL: %s, args: %s\n", sql, Arrays.toString(args));
    }

    public String resolveObjectPath(String objectPath) {
        List<String> levels = Arrays.asList(objectPath.split("\\."));
        return root.resolveObjectPath(levels, 0);
    }
}

class QueryContext {
    final CacheContext cacheContext;
    final CacheContext preCacheContext = new CacheContext();
    final TableAliasGenerator tableAliasGenerator = new TableAliasGenerator();
    final ColumnIndexGenerator columnIndexGenerator = new ColumnIndexGenerator();

    QueryContext(CacheContext cacheContext) {
        this.cacheContext = cacheContext;
    }

    void commitCache() {
        cacheContext.merge(preCacheContext);
    }
}

abstract class QueryNode {
    private final QueryNode parentNode;
    protected final EntityModel entityModel;
    protected final QueryContext queryContext;
    protected final String tableAlias;
    protected final FieldSelection fieldSelection = new FieldSelection();
    protected final List<QueryJoinNode> joins = new ArrayList<>();
    protected final List<AdditionTree> additions = new ArrayList<>();
    protected final QueryResultMapper mapper;

    protected QueryNode(QueryNode parentNode, EntityModel entityModel, QueryContext queryContext) {
        this.parentNode = parentNode;
        this.entityModel = entityModel;
        this.queryContext = queryContext;
        this.tableAlias = queryContext.tableAliasGenerator.generate();
        buildSubtree(queryContext);
        this.mapper = new QueryResultMapper(entityModel, fieldSelection);
    }

    private void buildSubtree(QueryContext queryContext) {
        for (Field field : entityModel.allFields()) {
            // SimpleField and BelongsTo
            if (field.hasColumn()) {
                fieldSelection.add(queryContext.columnIndexGenerator.newSelectedField(field, tableAlias));
            }
            // all eager associations
            if (field.isAssociation()) {
                AssociationField af = (AssociationField) field;
                if (af.getFetchType() == FetchType.EAGER) {
                    if (isCircular(af)) {
                        additions.add(new AdditionTree(af, queryContext.cacheContext));
                    } else {
                        joins.add(new QueryJoinNode(this, af, queryContext));
                    }
                }
            }
        }
    }

    private boolean isCircular(AssociationField associationField) {
        String targetModelName = associationField.getTargetModelRef().getName();
        QueryNode ancestor = this;
        while (ancestor != null) {
            if (ancestor.entityModel.getName().equals(targetModelName)) {
                return true;
            }
            ancestor = ancestor.parentNode;
        }
        return false;
    }

    String resolveObjectPath(List<String> levels, int offset) {
        if (offset >= levels.size()) {
            throw InvalidObjectPathException.outOfBounds(levels, offset);
        }
        String name = levels.get(offset);
        Field field = entityModel.findFieldByName(name);
        if (field == null) {
            throw InvalidObjectPathException.fieldNotFound(levels, name);
        }
        if (field.hasColumn()) {
            return tableAlias + '.' + field.getColumnName();
        } else {
            Optional<QueryJoinNode> join = joins.stream()
                    .filter(j -> j.getAssociationField().getName().equals(name))
                    .findFirst();
            if (!join.isPresent()) {
                throw InvalidObjectPathException.fieldNotFound(levels, name);
            }
            return join.get().resolveObjectPath(levels, offset + 1);
        }
    }
}

class QueryRootNode extends QueryNode {
    private final Map<Object, PersistentObject> results = new LinkedHashMap<>();

    QueryRootNode(EntityModel entityModel, QueryContext queryContext) {
        super(null, entityModel, queryContext);
    }

    void buildTableClauses(SqlBuilder sqlBuilder) {
        sqlBuilder.addTableClause(entityModel.getTableName() + " " + tableAlias);
        for (QueryJoinNode join : joins) {
            join.buildTableClauses(sqlBuilder, tableAlias);
        }
    }

    void mapRows(ResultSet rs) throws SQLException {
        while (rs.next()) {
            Object id = mapper.getId(rs);
            PersistentObject cached = queryContext.cacheContext.get(entityModel, id);
            if (cached != null) {
                results.put(id, cached);
                continue;
            }

            PersistentObject prev = results.get(id);
            PersistentObject current = prev == null ? mapper.mapRow(rs) : prev;
            for (QueryJoinNode join : joins) {
                join.mapRow(rs, current);
            }

            if (prev == null) {
                results.put(id, current);
                queryContext.preCacheContext.put(entityModel, id, current);
                for (AdditionTree addition : additions) {
                    addition.addSource(current);
                }
            }
        }
    }

    Collection<PersistentObject> complete(Connection connection) throws SQLException {
        queryContext.commitCache();
        for (AdditionTree addition : additions) {
            addition.complete(connection);
        }
        for (QueryJoinNode join : joins) {
            join.complete(connection);
        }
        return results.values();
    }
}

class QueryJoinNode extends QueryNode {
    private final AssociationField associationField;

    QueryJoinNode(QueryNode parentNode, AssociationField associationField, QueryContext queryContext) {
        super(parentNode, associationField.getTargetModelRef().model(), queryContext);
        this.associationField = associationField;
    }

    public AssociationField getAssociationField() {
        return associationField;
    }

    void mapRow(ResultSet rs, PersistentObject parent) throws SQLException {
        Object id = mapper.getId(rs);
        if (!mapper.isValidId(id)) {
            // Skip empty sub-record in left join
            return;
        }

        PersistentObject prev = queryContext.cacheContext.get(entityModel, id);
        PersistentObject current;
        if (prev != null) {
            current = prev;
        } else {
            current = mapper.mapRow(rs);
            queryContext.cacheContext.put(entityModel, id, current);
            for (AdditionTree addition : additions) {
                addition.addSource(current);
            }
        }

        if (associationField.isSingular()) {
            associationField.setValue(parent, current);
        } else {
            //noinspection unchecked
            List<PersistentObject> list = (List<PersistentObject>) associationField.getValue(parent);
            if (list == null) {
                list = new ArrayList<>();
                associationField.setValue(parent, list);
            }
            list.add(current);
        }

        for (QueryJoinNode join : joins) {
            join.mapRow(rs, current);
        }
    }

    void buildTableClauses(SqlBuilder sqlBuilder, String parentTableAlias) {
        EntityModel parentModel = associationField.getSourceModel();
        if (associationField instanceof HasAndBelongsToMany) {
            MiddleTableImage middle = ((HasAndBelongsToMany) associationField).middleTableImage();
            String middleTableAlias = queryContext.tableAliasGenerator.generate();
            sqlBuilder.addTableClause(leftJoinClause(
                    new JoinableTable(parentModel.getTableName(), parentTableAlias, parentModel.getIdField().getColumnName()),
                    new JoinableTable(middle.getTableName(), middleTableAlias, middle.getSourceIdColumn())
            ));
            sqlBuilder.addTableClause(leftJoinClause(
                    new JoinableTable(middle.getTableName(), middleTableAlias, middle.getTargetIdColumn()),
                    new JoinableTable(entityModel.getTableName(), tableAlias, entityModel.getIdField().getColumnName())
            ));
        } else if (associationField.hasColumn()) {
            sqlBuilder.addTableClause(leftJoinClause(
                    new JoinableTable(parentModel.getTableName(), parentTableAlias, associationField.getColumnName()),
                    new JoinableTable(entityModel.getTableName(), tableAlias, entityModel.getIdField().getColumnName())
            ));
        } else {
            Field mappedByField = entityModel.findFieldByName(associationField.getMappedByFieldName());
            sqlBuilder.addTableClause(leftJoinClause(
                    new JoinableTable(parentModel.getTableName(), parentTableAlias, parentModel.getIdField().getColumnName()),
                    new JoinableTable(entityModel.getTableName(), tableAlias, mappedByField.getColumnName())
            ));
        }

        for (QueryJoinNode join : joins) {
            join.buildTableClauses(sqlBuilder, tableAlias);
        }
    }

    private String leftJoinClause(JoinableTable src, JoinableTable tgt) {
        return " LEFT JOIN " + tgt.name + " " + tgt.alias + " ON " +
                src.alias + "." + src.column + "=" + tgt.alias + "." + tgt.column;
    }

    public void complete(Connection connection) throws SQLException {
        for (AdditionTree addition : additions) {
            addition.complete(connection);
        }
        for (QueryJoinNode join : joins) {
            join.complete(connection);
        }
    }
}

class JoinableTable {
    final String name;
    final String alias;
    final String column;

    JoinableTable(String name, String alias, String column) {
        this.name = name;
        this.alias = alias;
        this.column = column;
    }
}
