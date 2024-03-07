package com.iostate.orca;

import com.iostate.orca.metadata.BelongsTo;
import com.iostate.orca.metadata.CascadeType;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.HasAndBelongsToMany;
import com.iostate.orca.metadata.HasMany;
import com.iostate.orca.metadata.HasOne;
import com.iostate.orca.metadata.SimpleDataType;
import com.iostate.orca.metadata.SimpleField;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CircularEntityModelTest extends EntityModelGenerationTestBase {

    @Test
    public void testSelfReference() throws IOException {
        EntityModel selfModel = modelSelfEntity();
        selfModel.addDataField(new HasOne(
                "target",
                selfModel, modelRef(selfModel), "source",
                true, FetchType.EAGER, null
        ));
        selfModel.addDataField(new BelongsTo(
                "source", "source_id",
                selfModel, modelRef(selfModel),
                true, FetchType.EAGER, null
        ));

        exportCode("circular", selfModel);
    }

    @Test
    public void testOneToManyToOne() throws IOException {
        EntityModel parentModel = modelParentEntity();
        EntityModel childModel = modelChildEntity();
        parentModel.addDataField(new HasMany(
                "children",
                parentModel, modelRef(childModel), "parent",
                FetchType.EAGER, new CascadeType[]{CascadeType.ALL}
        ));
        childModel.addDataField(new BelongsTo(
                "parent", "parent_id",
                childModel, modelRef(parentModel),
                false, FetchType.EAGER, new CascadeType[]{CascadeType.ALL}
        ));

        exportCode("circular", parentModel, childModel);
    }

    @Test
    public void testManyToManyToMany() throws IOException {
        EntityModel sourceModel = modelSourceEntity();
        EntityModel targetModel = modelTargetEntity();
        sourceModel.addDataField(new HasAndBelongsToMany(
                "targets", metadataManager,
                sourceModel, modelRef(targetModel), null,
                FetchType.EAGER, null
        ));
        targetModel.addDataField(new HasAndBelongsToMany(
                "sources", metadataManager,
                targetModel, modelRef(sourceModel), "targets",
                FetchType.EAGER, null
        ));

        exportCode("circular", sourceModel, targetModel);
    }

    private EntityModel modelSelfEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field strField = new SimpleField("strValue", "str_value", SimpleDataType.STRING, false, true);

        EntityModel entityModel = new EntityModel(
                "SelfEntity", "self_entity",
                "auto", idField);
        entityModel.addDataField(strField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModel modelParentEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field strField = new SimpleField("strValue", "str_value", SimpleDataType.STRING, false, true);

        EntityModel entityModel = new EntityModel(
                "ParentEntity", "parent_entity",
                "auto", idField);
        entityModel.addDataField(strField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModel modelChildEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field intField = new SimpleField("intValue", "int_value", SimpleDataType.INT, false, true);

        EntityModel entityModel = new EntityModel(
                "ChildEntity", "child_entity",
                "auto", idField);
        entityModel.addDataField(intField);
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

}
