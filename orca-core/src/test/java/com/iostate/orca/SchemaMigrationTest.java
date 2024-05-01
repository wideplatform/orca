package com.iostate.orca;

import com.iostate.orca.db.DbType;
import com.iostate.orca.db.TableGenerator;
import com.iostate.orca.db.TableGeneratorFactory;
import com.iostate.orca.metadata.CascadeType;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.EntityModelDiff;
import com.iostate.orca.metadata.EntityModelRef;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.ManyToMany;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.metadata.SimpleDataType;
import com.iostate.orca.metadata.SimpleField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SchemaMigrationTest {

    private MetadataManager metadataManager;

    @BeforeEach
    public void setup() {
        metadataManager = new MetadataManager();
    }

    @Test
    public void testAddColumn() {
        EntityModel simpleModel1 = modelSimpleEntity();
        EntityModel simpleModel2 = modelSimpleEntity();
        simpleModel2.addDataField(new SimpleField("boolValue", "bool_value", SimpleDataType.BOOLEAN, false, true));

        EntityModelDiff diff = EntityModelDiff.compute(simpleModel1, simpleModel2);
        {
            TableGenerator tableGenerator = TableGeneratorFactory.make(DbType.MYSQL);
            assertEquals(
                    List.of("ALTER TABLE simple_entity ADD COLUMN bool_value BOOLEAN"),
                    tableGenerator.alter(simpleModel2, diff)
            );
        }

        {
            TableGenerator tableGenerator = TableGeneratorFactory.make(DbType.POSTGRESQL);
            assertEquals(
                    List.of("ALTER TABLE simple_entity ADD COLUMN bool_value BOOLEAN"),
                    tableGenerator.alter(simpleModel2, diff)
            );
        }
    }

    @Test
    public void testAddManyToManyAssociation() {
        EntityModel sourceModel1 = modelSourceEntity();
        EntityModel targetModel1 = modelTargetEntity();

        EntityModel sourceModel2 = modelSourceEntity();
        EntityModel targetModel2 = modelTargetEntity();
        sourceModel2.addDataField(new ManyToMany(
                "targets", metadataManager,
                sourceModel2, modelRef(targetModel2), null,
                FetchType.LAZY, new CascadeType[]{}
        ));
        targetModel2.addDataField(new ManyToMany(
                "sources", metadataManager,
                targetModel2, modelRef(sourceModel2), "targets",
                FetchType.LAZY, new CascadeType[]{}
        ));

        EntityModelDiff sourceDiff = EntityModelDiff.compute(sourceModel1, sourceModel2);
        EntityModelDiff targetDiff = EntityModelDiff.compute(targetModel1, targetModel2);
        List<String> sourceAlterDDL;
        List<String> targetAlterDDL;
        {
            TableGenerator tableGenerator = TableGeneratorFactory.make(DbType.MYSQL);
            sourceAlterDDL = tableGenerator.alter(sourceModel2, sourceDiff);
            targetAlterDDL = tableGenerator.alter(targetModel2, targetDiff);
            assertEquals(
                    List.of("CREATE TABLE rel_source_entity_target_entity(source_id BIGINT, target_id BIGINT, PRIMARY KEY (source_id, target_id))"),
                    sourceAlterDDL);
            assertEquals(List.of(), targetAlterDDL);
        }

        {
            TableGenerator tableGenerator = TableGeneratorFactory.make(DbType.POSTGRESQL);
            sourceAlterDDL = tableGenerator.alter(sourceModel2, sourceDiff);
            targetAlterDDL = tableGenerator.alter(targetModel2, targetDiff);
            assertEquals(
                    List.of("CREATE TABLE rel_source_entity_target_entity(source_id BIGINT, target_id BIGINT, PRIMARY KEY (source_id, target_id))"),
                    sourceAlterDDL);
            assertEquals(List.of(), targetAlterDDL);
        }
    }

    private EntityModel modelSimpleEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);

        EntityModel entityModel = new EntityModel(
                "SimpleEntity", "simple_entity",
                "auto", idField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModel modelSourceEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field strField = new SimpleField("strValue", "str_value", SimpleDataType.STRING, false, true);

        EntityModel entityModel = new EntityModel(
                "SourceEntity", "source_entity",
                "auto", idField);
        entityModel.addDataField(strField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModel modelTargetEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field intField = new SimpleField("intValue", "int_value", SimpleDataType.INT, false, true);

        EntityModel entityModel = new EntityModel(
                "TargetEntity", "target_entity",
                "auto", idField);
        entityModel.addDataField(intField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    protected EntityModelRef modelRef(EntityModel entityModel) {
        return new EntityModelRef(entityModel.getName(), metadataManager);
    }
}
