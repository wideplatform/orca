package com.iostate.orca;

import com.iostate.orca.metadata.CascadeType;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.EntityModelRef;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.metadata.MiddleTable;
import com.iostate.orca.metadata.PluralAssociationField;
import com.iostate.orca.metadata.SimpleDataType;
import com.iostate.orca.metadata.SimpleField;
import com.iostate.orca.metadata.SingularAssociationField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class EntityModelTest {

    private static final String BASE_PACKAGE = "com.iostate.orca";

    private MetadataManager metadataManager;

    @BeforeEach
    public void setup() {
        metadataManager = new MetadataManager();
    }

    // namespace can be empty standing for the default namespace
    private void exportCode(String namespace, EntityModel... entityModels) throws IOException {
        for (EntityModel entityModel : entityModels) {
            String packageName = BASE_PACKAGE + (namespace.isEmpty() ? "" : '.' + namespace);
            String modelName = entityModel.getName();
            entityModel.setLinkedClassName(packageName + '.' + modelName);
            String yaml = metadataManager.generateYaml(entityModel);
            Path yamlDir = Paths.get(
                    "src/test/resources/models",
                    namespace);
            Files.createDirectories(yamlDir);
            Files.write(yamlDir.resolve(modelName + ".yml"), yaml.getBytes(StandardCharsets.UTF_8));

            String java = metadataManager.generateJava(entityModel, namespace, packageName);
            Path javaDir = Paths.get(
                    "src/test/java",
                    BASE_PACKAGE.replace('.', '/'),
                    namespace.replace('.', '/'));
            Files.createDirectories(javaDir);
            Files.write(javaDir.resolve(modelName + ".java"), java.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Test
    public void testSimpleEntity() throws IOException {
        EntityModel entityModel = modelSimpleEntity();
        exportCode("", entityModel);

        assertEquals("SimpleEntity", entityModel.getName());
        assertEquals("simple_entity", entityModel.getTableName());
        Collection<Field> allFields = entityModel.allFields();
        assertEquals(5, allFields.size());
        assertEquals(entityModel.getIdField(), allFields.iterator().next());
    }

    @Test
    public void testOneToOne() throws IOException {
        EntityModel parentModel = modelParentEntity();
        EntityModel childModel = modelChildEntity();
        parentModel.addDataField(
                new SingularAssociationField(
                        "child", "child", modelRef(childModel),
                        false, true,
                        FetchType.LAZY, new CascadeType[]{CascadeType.ALL})
        );

        exportCode("onetoone", parentModel, childModel);
    }

    @Test
    public void testOneToMany() throws IOException {
        EntityModel parentModel = modelParentEntity();
        EntityModel childModel = modelChildEntity();
        parentModel.addDataField(
                new PluralAssociationField(
                        "children", modelRef(childModel),
                        FetchType.LAZY, new CascadeType[]{CascadeType.ALL})
        );

        exportCode("onetomany", parentModel, childModel);
    }

    @Test
    public void testManyToOne() throws IOException {
        EntityModel sourceModel = modelSourceEntity();
        EntityModel targetModel = modelTargetEntity();
        SingularAssociationField singularAssociationField = new SingularAssociationField(
                "target", "target", modelRef(targetModel),
                false, true,
                FetchType.LAZY, new CascadeType[]{}
        );
        sourceModel.addDataField(singularAssociationField);

        exportCode("manytoone", sourceModel, targetModel);
    }

    @Test
    public void testManyToMany() throws IOException {
        EntityModel sourceModel = modelSourceEntity();
        EntityModel targetModel = modelTargetEntity();
        PluralAssociationField pluralAssociationField = new PluralAssociationField(
                "targets", modelRef(targetModel),
                FetchType.LAZY, new CascadeType[]{}
        );
        pluralAssociationField.setMiddleTable(
                new MiddleTable(modelRef(sourceModel), modelRef(targetModel)));
        sourceModel.addDataField(pluralAssociationField);

        exportCode("manytomany", sourceModel, targetModel);
    }

    private EntityModel modelSimpleEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field boolField = new SimpleField("bool", "bool", SimpleDataType.BOOLEAN, false, true);
        Field integerField = new SimpleField("integer", "integer", SimpleDataType.INT, false, true);
        Field stringField = new SimpleField("string", "string", SimpleDataType.STRING, false, true);
        Field datetimeField = new SimpleField("dt", "dt", SimpleDataType.DATETIME, false, true);

        EntityModel entityModel = new EntityModel(
                "SimpleEntity", "simple_entity",
                "auto", idField);
        entityModel.addDataField(boolField);
        entityModel.addDataField(integerField);
        entityModel.addDataField(stringField);
        entityModel.addDataField(datetimeField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModel modelParentEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field stringField = new SimpleField("string", "string", SimpleDataType.STRING, false, true);

        EntityModel entityModel = new EntityModel(
                "ParentEntity", "parent_entity",
                "auto", idField);
        entityModel.addDataField(stringField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModel modelChildEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field integerField = new SimpleField("integer", "integer", SimpleDataType.INT, false, true);

        EntityModel entityModel = new EntityModel(
                "ChildEntity", "child_entity",
                "auto", idField);
        entityModel.addDataField(integerField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModel modelSourceEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field stringField = new SimpleField("string", "string", SimpleDataType.STRING, false, true);

        EntityModel entityModel = new EntityModel(
                "SourceEntity", "source_entity",
                "auto", idField);
        entityModel.addDataField(stringField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModel modelTargetEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field integerField = new SimpleField("integer", "integer", SimpleDataType.INT, false, true);

        EntityModel entityModel = new EntityModel(
                "TargetEntity", "target_entity",
                "auto", idField);
        entityModel.addDataField(integerField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModelRef modelRef(EntityModel entityModel) {
        return new EntityModelRef(entityModel.getName(), metadataManager);
    }
}
