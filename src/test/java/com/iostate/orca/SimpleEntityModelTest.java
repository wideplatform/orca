package com.iostate.orca;

import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.SimpleDataType;
import com.iostate.orca.metadata.SimpleField;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleEntityModelTest extends EntityModelGenerationTestBase {

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
}
