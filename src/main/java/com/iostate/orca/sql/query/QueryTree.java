package com.iostate.orca.sql.query;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.api.exception.InvalidObjectPathException;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.query.ConcreteSqlBuilder;
import com.iostate.orca.query.SqlBuilder;
import com.iostate.orca.query.predicate.Predicate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Entrance of object-oriented query model */
public class QueryTree {
    private final QueryRootNode root;
    private final List<Predicate> filters = new ArrayList<>();

    public QueryTree(EntityModel entityModel) {
        root = new QueryRootNode(entityModel);
    }

    public void addFilter(Predicate filter) {
        filters.add(filter);
    }

    public SqlObject toSqlObject() {
        ConcreteSqlBuilder sqlBuilder = new ConcreteSqlBuilder(this);
        sqlBuilder.addString("SELECT ");
        root.buildSelectColumns(sqlBuilder);
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

    public List<PersistentObject> mapRows(ResultSet rs) throws SQLException {
        return root.mapRows(rs);
    }

    public String resolveObjectPath(String objectPath) {
        List<String> levels = Arrays.asList(objectPath.split("\\."));
        return root.resolveObjectPath(levels, 0);
    }
}

class QueryContext {
    final TableAliasGenerator tableAliasGenerator = new TableAliasGenerator();
    final ColumnIndexGenerator columnIndexGenerator = new ColumnIndexGenerator();
}

abstract class QueryNode {
    private final QueryNode parentNode;
    protected final EntityModel entityModel;
    protected final String tableAlias;
    protected final List<SelectedField> selectedFields = new ArrayList<>();
    protected final List<QueryJoinNode> joins = new ArrayList<>();
    // TODO fire additional queries to load other association fields which are not joinable
    protected final List<QueryAddition> additions = new ArrayList<>();

    protected QueryNode(QueryNode parentNode, EntityModel entityModel, QueryContext queryContext) {
        this.parentNode = parentNode;
        this.entityModel = entityModel;
        this.tableAlias = queryContext.tableAliasGenerator.generate();
        buildSubtree(queryContext);
    }

    private void buildSubtree(QueryContext queryContext) {
        for (Field field : entityModel.allFields()) {
             if (field.isAssociation()) {
                AssociationField af = (AssociationField) field;
                if (af.getFetchType() == FetchType.EAGER) {
                    if (isCircular(af)) {
                        additions.add(new QueryAddition(this, af));
                    } else {
                        joins.add(new QueryJoinNode(this, af, queryContext));
                    }
                }
            } else if (field.hasColumn()) {
                 selectedFields.add(new SelectedField(field, queryContext.columnIndexGenerator));
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

    void buildSelectColumns(SqlBuilder sqlBuilder) {
        for (SelectedField sf : selectedFields) {
            sqlBuilder.addSelectColumn(tableAlias, sf.getField().getColumnName());
        }
        for (QueryJoinNode join : joins) {
            join.buildSelectColumns(sqlBuilder);
        }
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
            if (join.isEmpty()) {
                throw InvalidObjectPathException.fieldNotFound(levels, name);
            }
            return join.get().resolveObjectPath(levels, offset + 1);
        }
    }
}

class QueryRootNode extends QueryNode {
    // Handle duplicate data in cartesian product
    private final Map<Object, PersistentObject> idsToPos = new LinkedHashMap<>();

    QueryRootNode(EntityModel entityModel) {
        super(null, entityModel, new QueryContext());
    }

    void buildTableClauses(SqlBuilder sqlBuilder) {
        sqlBuilder.addTableClause(entityModel.getTableName() + " " + tableAlias);
        for (QueryJoinNode join : joins) {
            join.buildTableClauses(sqlBuilder, tableAlias);
        }
    }

    List<PersistentObject> mapRows(ResultSet rs) throws SQLException {
        QueryResultMapper mapper = new QueryResultMapper(entityModel, selectedFields);
        while (rs.next()) {
            PersistentObject po = mapper.mapRow(rs);
            {
                PersistentObject prev = idsToPos.putIfAbsent(entityModel.getIdField().getValue(po), po);
                if (prev != null) {
                    po = prev;
                }
            }
            for (QueryJoinNode join : joins) {
                join.mapRow(rs, po);
            }
        }
        return new ArrayList<>(idsToPos.values());
    }
}

class QueryJoinNode extends QueryNode {
    private final AssociationField associationField;
    // Handle duplicate data in cartesian product
    private final Map<Object, PersistentObject> idsToPos = new LinkedHashMap<>();

    QueryJoinNode(QueryNode parentNode, AssociationField associationField, QueryContext queryContext) {
        super(parentNode, associationField.getTargetModelRef().model(), queryContext);
        this.associationField = associationField;
    }

    public AssociationField getAssociationField() {
        return associationField;
    }

    void mapRow(ResultSet rs, PersistentObject parent) throws SQLException {
        QueryResultMapper mapper = new QueryResultMapper(entityModel, selectedFields);
        PersistentObject po = mapper.mapRow(rs);
        {
            PersistentObject prev = idsToPos.putIfAbsent(entityModel.getIdField().getValue(po), po);
            if (prev != null) {
                po = prev;
            } else {
                if (associationField.isSingular()) {
                    associationField.setValue(parent, po);
                } else {
                    List<PersistentObject> list = (List<PersistentObject>) associationField.getValue(parent);
                    if (list == null) {
                        list = new ArrayList<>();
                        associationField.setValue(parent, list);
                    }
                    list.add(po);
                }
            }
        }
        for (QueryJoinNode join : joins) {
            join.mapRow(rs, po);
        }
    }

    void buildTableClauses(SqlBuilder sqlBuilder, String parentTableAlias) {
        String joinCondition = associationField.hasColumn() ?
                parentTableAlias + "." + associationField.getColumnName() + " = "
                        + tableAlias + "." + entityModel.getIdField().getColumnName() :
                parentTableAlias + "." + associationField.getSourceModel().getIdField().getColumnName() + " = "
                + tableAlias + "." + entityModel.findFieldByName(associationField.getMappedByFieldName()).getColumnName();
        sqlBuilder.addTableClause(
                " LEFT JOIN " + entityModel.getTableName() + " " + tableAlias +
                        " ON " + joinCondition);
        for (QueryJoinNode join : joins) {
            join.buildTableClauses(sqlBuilder, tableAlias);
        }
    }
}

// Not a node
class QueryAddition {
    private final QueryNode parentNode;
    private final AssociationField associationField;

    QueryAddition(QueryNode parentNode, AssociationField associationField) {
        this.parentNode = parentNode;
        this.associationField = associationField;
    }
}

class SelectedField {
    private final Field field;
    private final ColumnIndexGenerator columnIndexGenerator;
    private int index;

    SelectedField(Field field, ColumnIndexGenerator columnIndexGenerator) {
        this.field = field;
        this.columnIndexGenerator = columnIndexGenerator;
    }

    public Field getField() {
        return field;
    }

    int getIndex() {
        if (index == 0) {
            index = columnIndexGenerator.generate();
        }
        return index;
    }
}
