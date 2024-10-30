package com.iostate.orca.db;

import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.EntityModelDiff;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.ManyToMany;
import com.iostate.orca.metadata.MiddleTable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTableGenerator implements TableGenerator {

    private String columnDefinition(EntityModel entityModel, Field field) {
        String clause = field.getColumnName() + " " + SqlTypeMapping.sqlType(dbType(), field);
        if (field.isId()) {
            clause += " PRIMARY KEY";
            if (entityModel.isAutoId()) {
                clause += (" " + autoIncrement());
            }
        } else {
            if (!field.isNullable()) {
                clause += " NOT NULL";
            }
        }
        return clause;
    }

    private String middleTableDDL(ManyToMany mtm) {
        MiddleTable middleTable = mtm.getMiddleTable();
        return String.format("CREATE TABLE %s(source_id %s, target_id %s, PRIMARY KEY (source_id, target_id))",
                middleTable.getTableName(),
                SqlTypeMapping.sqlType(dbType(), middleTable.getSourceModelRef().model().getIdField()),
                SqlTypeMapping.sqlType(dbType(), middleTable.getTargetModelRef().model().getIdField())
        );
    }

    @Override
    public List<String> create(EntityModel entityModel) {
        List<String> stmts = new ArrayList<>();
        String columns = entityModel.allFields()
                .stream()
                .filter(Field::hasColumn)
                .map(field -> columnDefinition(entityModel, field))
                .collect(Collectors.joining(", "));
        stmts.add(String.format("CREATE TABLE %s(%s)", entityModel.getTableName(), columns));

        for (Field field : entityModel.allFields()) {
            if (field instanceof ManyToMany mtm && mtm.getMiddleTable() != null) {
                String ddl = middleTableDDL(mtm);
                stmts.add(ddl);
            }
        }
        return stmts;
    }

    @Override
    public List<String> drop(EntityModel entityModel) {
        List<String> stmts = new ArrayList<>();
        for (Field field : entityModel.allFields()) {
            if (field instanceof ManyToMany mtm && mtm.getMiddleTable() != null) {
                stmts.add("DROP TABLE IF EXISTS " + mtm.getMiddleTable().getTableName());
            }
        }
        stmts.add("DROP TABLE IF EXISTS " + entityModel.getTableName());
        return stmts;
    }

    @Override
    public List<String> alter(EntityModel entityModel, EntityModelDiff diff) {
        List<String> stmts = new ArrayList<>();
        if (diff.getNewTableName() != null) {
            stmts.add(String.format("RENAME TABLE %s TO %s", diff.getOldTableName(), diff.getNewTableName()));
        }

        for (Field field : diff.getAddedFields()) {
            if (field.hasColumn()) {
                stmts.add(String.format("ALTER TABLE %s ADD COLUMN %s", entityModel.getTableName(), columnDefinition(entityModel, field)));
            }
            if (field instanceof ManyToMany mtm && mtm.getMiddleTable() != null) {
                stmts.add(middleTableDDL(mtm));
            }
        }

        for (Field field : diff.getRemovedFields()) {
            if (field.hasColumn()) {
                stmts.add(String.format("ALTER TABLE %s DROP COLUMN %s", entityModel.getTableName(), field.getColumnName()));
            }
            if (field instanceof ManyToMany mtm && mtm.getMiddleTable() != null) {
                stmts.add("DROP TABLE IF EXISTS " + mtm.getMiddleTable().getTableName());
            }
        }

        return stmts;
    }

    protected abstract DbType dbType();

    protected abstract String autoIncrement();
}
