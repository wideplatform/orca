package com.iostate.orca.sql.query;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Entrance of object-oriented query model */
public class QueryTree {
    private final QueryRootNode root;

    public QueryTree(EntityModel entityModel) {
        root = new QueryRootNode(entityModel);
    }

    public SqlQuery toSqlQuery() {
        return root.toSqlQuery();
    }

    public List<PersistentObject> mapRows(ResultSet rs) throws SQLException {
        return root.mapRows(rs);
    }
}

abstract class QueryNode {
    protected final EntityModel entityModel;
    protected final List<SelectedField> selectedFields = new ArrayList<>();
    protected final List<QueryJoinNode> joins = new ArrayList<>();

    protected QueryNode(EntityModel entityModel, ColumnIndexGenerator columnIndexGenerator) {
        this.entityModel = entityModel;
        buildSubtree(columnIndexGenerator);
    }

    protected final void buildSubtree(ColumnIndexGenerator columnIndexGenerator) {
        for (Field field : entityModel.allFields()) {
            if (field.hasColumn()) {
                if (field.isAssociation()) {
                    AssociationField af = (AssociationField) field;
                    if (af.getFetchType() == FetchType.EAGER) {
                        joins.add(new QueryJoinNode(af, columnIndexGenerator));
                    }
                } else {
                    selectedFields.add(new SelectedField(field, columnIndexGenerator));
                }
            }
        }
    }
}

class QueryRootNode extends QueryNode {
    // Handle duplicate data in cartesian product
    private final Map<Object, PersistentObject> idsToPos = new LinkedHashMap<>();

    QueryRootNode(EntityModel entityModel) {
        super(entityModel, new ColumnIndexGenerator());
    }

    SqlQuery toSqlQuery() {
        SqlQuery sqlQuery = new SqlQuery();
        SqlTable drivingTable = sqlQuery.addDrivingTable(
                entityModel.getTableName(),
                selectedFields.stream()
                        .map(sf -> sf.getField().getColumnName())
                        .collect(Collectors.toList()),
                Collections.emptyList()
        );

        for (QueryJoinNode qa : joins) {
            qa.decorateSqlQuery(sqlQuery, drivingTable);
        }
        return sqlQuery;
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

    QueryJoinNode(AssociationField associationField, ColumnIndexGenerator columnIndexGenerator) {
        super(associationField.getTargetModelRef().model(), columnIndexGenerator);
        this.associationField = associationField;
    }

    void decorateSqlQuery(SqlQuery sqlQuery, SqlTable parentTable) {
        sqlQuery.addJoinTable(
                entityModel.getTableName(),
                selectedFields.stream()
                        .map(sf -> sf.getField().getColumnName())
                        .collect(Collectors.toList()),
                Collections.emptyList(),
                parentTable.columnRef(associationField.getColumnName()),
                entityModel.getIdField().getColumnName()
        );
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
