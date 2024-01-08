package com.iostate.orca.sql.query;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.sql.QueryResultMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QueryRootNode {
    private final EntityModel entityModel;
    private final List<Field> selectedFields = new ArrayList<>();
    private final List<QueryJoinNode> joins = new ArrayList<>();

    public QueryRootNode(EntityModel entityModel) {
        this.entityModel = entityModel;
        for (Field field : entityModel.allFields()) {
            if (field.hasColumn()) {
                selectedFields.add(field);
                if (field.isAssociation()) {
                    AssociationField af = (AssociationField) field;
                    if (af.getFetchType() == FetchType.EAGER) {
                        joins.add(new QueryJoinNode(af));
                    }
                }
            }
        }
    }

    public SqlQuery toSqlQuery() {
        SqlQuery sqlQuery = new SqlQuery();
        SqlTable drivingTable = sqlQuery.addDrivingTable(
                entityModel.getTableName(),
                selectedFields.stream().map(Field::getColumnName).collect(Collectors.toList()),
                Collections.emptyList()
        );

        for (QueryJoinNode qa : joins) {
            qa.decorateSqlQuery(sqlQuery, drivingTable);
        }
        return sqlQuery;
    }

    public PersistentObject mapRow(ResultSet rs) throws SQLException {
        PersistentObject po = new QueryResultMapper(entityModel, selectedFields).mapRow(rs);
        for (QueryJoinNode join : joins) {
            join.mapRow(rs, po);
        }
        return po;
    }
}

class QueryJoinNode {
    private final AssociationField associationField;
    private final EntityModel entityModel;
    private final List<Field> selectedFields = new ArrayList<>();
    private final List<QueryJoinNode> joins = new ArrayList<>();

    public QueryJoinNode(AssociationField associationField) {
        this.associationField = associationField;
        entityModel = associationField.getTargetModelRef().model();
        for (Field field : entityModel.allFields()) {
            if (field.hasColumn()) {
                selectedFields.add(field);
                if (field.isAssociation()) {
                    AssociationField af = (AssociationField) field;
                    if (af.getFetchType() == FetchType.EAGER) {
                        joins.add(new QueryJoinNode(af));
                    }
                }
            }
        }
    }

    void decorateSqlQuery(SqlQuery sqlQuery, SqlTable parentTable) {
        sqlQuery.addJoinTable(
                entityModel.getTableName(),
                selectedFields.stream().map(Field::getColumnName).collect(Collectors.toList()),
                Collections.emptyList(),
                parentTable.columnRef(associationField.getColumnName()),
                entityModel.getIdField().getColumnName()
        );
    }

    public void mapRow(ResultSet rs, PersistentObject parentPO) throws SQLException {
        PersistentObject po = new QueryResultMapper(entityModel, selectedFields).mapRow(rs);
        for (QueryJoinNode join : joins) {
            join.mapRow(rs, po);
        }
        associationField.setValue(parentPO, po);
    }
}
