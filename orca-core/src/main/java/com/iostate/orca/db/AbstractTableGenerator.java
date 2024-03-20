package com.iostate.orca.db;

import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.ManyToMany;
import com.iostate.orca.metadata.MiddleTable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractTableGenerator implements TableGenerator {
    @Override
    public Map<String, String> create(EntityModel entityModel) {
        String columns = entityModel.allFields()
                .stream()
                .filter(Field::hasColumn)
                .map(field -> {
                    String clause = field.getColumnName() + " " + SqlTypeMapping.sqlType(dbType(), field);
                    if (field.isId()) {
                        clause += " PRIMARY KEY";
                        if (entityModel.isIdGenerated()) {
                            clause += (" " + autoIncrement());
                        }
                    } else {
                        if (!field.isNullable()) {
                            clause += " NOT NULL";
                        }
                    }
                    return clause;
                })
                .collect(Collectors.joining(", "));

        Map<String, String> tablesVsDdls = new HashMap<>();
        tablesVsDdls.put(entityModel.getTableName(), String.format("CREATE TABLE %s(%s);\n", entityModel.getTableName(), columns));

        entityModel.allFields().forEach(f -> {
            if (f instanceof ManyToMany) {
                MiddleTable rel = ((ManyToMany) f).getMiddleTable();
                if (rel != null) {
                    String ddl = String.format("  CREATE TABLE %s(source_id %s, target_id %s, PRIMARY KEY (source_id, target_id));\n",
                            rel.getTableName(),
                            SqlTypeMapping.sqlType(dbType(), rel.getSourceModelRef().model().getIdField()),
                            SqlTypeMapping.sqlType(dbType(), rel.getTargetModelRef().model().getIdField())
                    );
                    tablesVsDdls.put(rel.getTableName(), ddl);
                }
            }
        });

        return tablesVsDdls;
    }

    protected abstract DbType dbType();

    protected abstract String autoIncrement();
}
