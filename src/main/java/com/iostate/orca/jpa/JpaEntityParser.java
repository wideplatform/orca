package com.iostate.orca.jpa;

import com.iostate.orca.api.BasePO;
import com.iostate.orca.metadata.CascadeType;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.InverseAssociationField;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.metadata.EntityModelRef;
import com.iostate.orca.metadata.PluralAssociationField;
import com.iostate.orca.metadata.MiddleTable;
import com.iostate.orca.metadata.SimpleDataType;
import com.iostate.orca.metadata.SimpleField;
import com.iostate.orca.metadata.SingularAssociationField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class JpaEntityParser {

    private final BiConsumer<Class<?>, EntityModel> ingest;
    private final MetadataManager metadataManager;

    public JpaEntityParser(BiConsumer<Class<?>, EntityModel> ingest, MetadataManager metadataManager) {
        this.ingest = ingest;
        this.metadataManager = metadataManager;
    }

    public EntityModel execute(Class<?> entityClass) {
        String entityName = resolveName(entityClass);

        String tableName = resolveTableName(entityName, entityClass);

        PersistentFields persistentFields = resolvePersistentFields(entityClass);

        String idGenerator = resolveGeneratedValue(persistentFields.idField);

        Field idField = toField(null, persistentFields.idField, true);

        EntityModel entityModel = new EntityModel(entityName, tableName, idGenerator, idField);

        persistentFields.dataFields.forEach(field -> entityModel.addDataField(toField(entityModel, field, false)));

        ingest.accept(entityClass, entityModel);
        return entityModel;
    }

    private String resolveName(Class<?> entityClass) {
        Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
        if (entityAnnotation != null) {
            if (entityAnnotation.name().isEmpty()) {
                return entityClass.getSimpleName();
            } else {
                return entityAnnotation.name();
            }
        } else if (BasePO.class.isAssignableFrom(entityClass)) {
            return entityClass.getSimpleName();
        } else {
            throw new PersistenceException(entityClass + " does not have @Entity annotation or extend BasePO");
        }
    }

    private String resolveTableName(String entityName, Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null && !table.name().isEmpty()) {
            return table.name();
        } else {
            return entityName.toUpperCase();
        }
    }

    private PersistentFields resolvePersistentFields(Class<?> entityClass) {
        //TODO entity inheritance

        //TODO annotated getters

        List<java.lang.reflect.Field> idFields = new ArrayList<>();
        List<java.lang.reflect.Field> dataFields = new ArrayList<>();
        for (java.lang.reflect.Field jField : entityClass.getDeclaredFields()) {
            if (Modifier.isStatic(jField.getModifiers())) {
                continue;
            }
            if (isId(jField)) {
                idFields.add(jField);
                if (isTransient(jField)) {
                    throw new PersistenceException("Transient field can't be id field, field=" + jField);
                }
            } else if (!isTransient(jField)) {
                dataFields.add(jField);
            }
        }

        if (idFields.size() != 1) {
            throw new PersistenceException(String.format(
                    "Require exactly 1 field annotated by @Id, but found %d: %s", idFields.size(), idFields));
        }

        return new PersistentFields(idFields.get(0), dataFields);
    }

    private boolean isId(java.lang.reflect.Field field) {
        return field.getAnnotation(Id.class) != null || field.getName().equals("id");
    }

    private boolean isTransient(java.lang.reflect.Field field) {
        return field.getAnnotation(Transient.class) != null ||
                (field.getName().startsWith("_") && field.getName().endsWith("_"));
    }

    private String resolveGeneratedValue(java.lang.reflect.Field idField) {
        if (idField.getAnnotation(GeneratedValue.class) != null
                || idField.getName().equals("id")) {
            return "auto";
        } else {
            return null;
        }
    }

    private Field toField(EntityModel enclosingEntityModel, java.lang.reflect.Field jField, boolean isId) {
        String name = jField.getName();

        Column column = jField.getAnnotation(Column.class);
        boolean isNullable = column == null || column.nullable();

        OneToOne oneToOne = jField.getAnnotation(OneToOne.class);
        ManyToOne manyToOne = jField.getAnnotation(ManyToOne.class);
        OneToMany oneToMany = jField.getAnnotation(OneToMany.class);
        ManyToMany manyToMany = jField.getAnnotation(ManyToMany.class);
        if (Stream.of(oneToOne, manyToOne, oneToMany, manyToMany).filter(Objects::nonNull).count() > 1) {
            throw new IllegalArgumentException("field " + jField + " can't have multiple association types");
        }

        if (oneToOne != null) {
            //TODO optional(), targetEntity()
            Class<?> targetEntity = jField.getType();
            EntityModel targetEntityModel = new JpaEntityParser(ingest, metadataManager).execute(targetEntity);
            String columnName = resolveColumnName(column, jField.getName().toUpperCase() + "_ID");

            return new SingularAssociationField(name, columnName,
                    modelRef(targetEntityModel), isId, isNullable,
                    fetchType(oneToOne.fetch()), cascadeTypes(oneToOne.cascade()));
        }

        if (manyToOne != null) {
            //TODO optional(), targetEntity()
            Class<?> targetEntity = jField.getType();
            EntityModel targetEntityModel = new JpaEntityParser(ingest, metadataManager).execute(targetEntity);
            String columnName = resolveColumnName(column, jField.getName().toUpperCase() + "_ID");

            return new SingularAssociationField(name, columnName,
                    modelRef(targetEntityModel), isId, isNullable,
                    fetchType(manyToOne.fetch()), cascadeTypes(manyToOne.cascade()));
        }

        if (oneToMany != null ||
                manyToMany != null) {
            if (isCollectionTyped(jField)) {

                //TODO targetEntity()
                ParameterizedType collectionType = (ParameterizedType) jField.getGenericType();
                Type targetType = collectionType.getActualTypeArguments()[0];
                if (targetType instanceof Class) {
                    Class<?> targetEntity = (Class<?>) targetType;
                    EntityModel targetEntityModel = new JpaEntityParser(ingest, metadataManager).execute(targetEntity);

                    if (oneToMany != null) {
                        Field inverseField = createInverseField(enclosingEntityModel);
                        targetEntityModel.addDataField(inverseField);

                        PluralAssociationField field = new PluralAssociationField(
                                name, modelRef(targetEntityModel),
                                fetchType(oneToMany.fetch()), cascadeTypes(oneToMany.cascade()));
                        field.setTargetInverseField(inverseField);
                        return field;
                    } else {
                        PluralAssociationField field = new PluralAssociationField(
                                name, modelRef(targetEntityModel),
                                fetchType(manyToMany.fetch()), cascadeTypes(manyToMany.cascade()));
                        //TODO table auto-naming
                        field.setMiddleTable(
                                new MiddleTable(modelRef(enclosingEntityModel), modelRef(targetEntityModel)));
                        return field;
                    }
                }
            }

            throw new IllegalArgumentException("field " + jField + " should be a parameterized collection type");
        }

        String columnName = resolveColumnName(column, jField.getName());
        return new SimpleField(name, columnName, SimpleDataType.valueOf(jField.getType()), isId, isNullable);
    }

    private boolean isCollectionTyped(java.lang.reflect.Field field) {
        return Collection.class.isAssignableFrom(field.getType()) &&
                field.getGenericType() instanceof ParameterizedType;
    }

    // Inverse association from target to source
    private Field createInverseField(EntityModel enclosingEntityModel) {
        //TODO virtual field auto-naming
        String inverseFieldName = "inverse_" + enclosingEntityModel.getName();
        String inverseColumnName = inverseFieldName.toUpperCase() + "_ID";
        return new InverseAssociationField(
                inverseFieldName, inverseColumnName,
                modelRef(enclosingEntityModel),
                false, true);
    }

    private String resolveColumnName(Column column, String defaultName) {
        return (column == null || column.name().isEmpty()) ? defaultName : column.name();
    }

    private static class PersistentFields {
        final java.lang.reflect.Field idField;
        final List<java.lang.reflect.Field> dataFields;

        PersistentFields(java.lang.reflect.Field idField, List<java.lang.reflect.Field> dataFields) {
            this.idField = idField;
            this.dataFields = dataFields;
        }
    }

    private CascadeType[] cascadeTypes(javax.persistence.CascadeType[] cascade) {
        CascadeType[] result = new CascadeType[cascade.length];
        for (int i = 0; i < cascade.length; i++) {
            javax.persistence.CascadeType each = cascade[i];
            result[i] = CascadeType.valueOf(each.name());
        }
        return result;
    }

    private FetchType fetchType(javax.persistence.FetchType fetch) {
        return FetchType.valueOf(fetch.name());
    }

    private EntityModelRef modelRef(EntityModel entityModel) {
        return new EntityModelRef(entityModel.getName(), metadataManager);
    }
}
