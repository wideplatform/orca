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
        assertEquals(7, allFields.size());
        assertEquals(entityModel.getIdField(), allFields.iterator().next());
    }

    private EntityModel modelSimpleEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field boolField = new SimpleField("boolValue", "bool_value", SimpleDataType.BOOLEAN, false, true);
        Field intField = new SimpleField("intValue", "int_value", SimpleDataType.INT, false, true);
        Field longField = new SimpleField("longValue", "long_value", SimpleDataType.LONG, false, true);
        Field decField = new SimpleField("decValue", "dec_value", SimpleDataType.DECIMAL, false, true);
        Field strField = new SimpleField("strValue", "str_value", SimpleDataType.STRING, false, true);
        Field datetimeField = new SimpleField("dtValue", "dt_value", SimpleDataType.DATETIME, false, true);

        EntityModel entityModel = new EntityModel(
                "SimpleEntity", "simple_entity",
                "auto", idField);
        entityModel.addDataField(boolField);
        entityModel.addDataField(intField);
        entityModel.addDataField(longField);
        entityModel.addDataField(decField);
        entityModel.addDataField(strField);
        entityModel.addDataField(datetimeField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }
}
