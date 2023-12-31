package com.iostate.orca.query;

import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.query.predicate.Predicate;

public class Query<T> {

    private final String tableName;
    private String[] elements;
    private Predicate where;

    Query(String tableName) {
        this.tableName = tableName;
    }

    public static <T> Query<T> from(MetadataManager metadataManager, Class<T> entityClass) {
        return new Query<>(metadataManager.findEntityByClass(entityClass).getTableName());
    }

    public static <T extends PersistentObject> Query<T> from(EntityModel entityModel) {
        return new Query<>(entityModel.getTableName());
    }

    public Query<T> select(String... elements) {
        this.elements = elements;
        return this;
    }

    public Query<T> where(Predicate... predicates) {
        if (where == null) {
            where = Predicate.and(predicates);
        } else {
            where = Predicate.and(where, predicates);
        }
        return this;
    }

    public Query<T> andWhere(Predicate predicate) {
        if (where == null) {
            where = predicate;
        } else {
            where = where.and(predicate);
        }
        return this;
    }

    public Query<T> orWhere(Predicate predicate) {
        if (where == null) {
            where = predicate;
        } else {
            where = where.or(predicate);
        }
        return this;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder("SELECT ");
        builder
                .append(elements != null ? String.join(", ", elements) : "*")
                .append(" FROM ")
                .append(tableName);

        if (where != null) {
            builder
                    .append(" WHERE ")
                    .append(where);
        }

        return builder.toString();
    }
}
