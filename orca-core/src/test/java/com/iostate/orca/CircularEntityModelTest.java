package com.iostate.orca;

import com.iostate.orca.metadata.BelongsTo;
import com.iostate.orca.metadata.CascadeType;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.ManyToMany;
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
    public void testOneToOneDualAssociations() throws IOException {
        EntityModel sourceModel = modelDualSourceEntity();
        EntityModel targetModel = modelDualTargetEntity();
        sourceModel.addDataField(new HasOne(
                "target1",
                sourceModel, modelRef(targetModel), "source1",
                true, FetchType.EAGER, null
        ));
        sourceModel.addDataField(new HasOne(
                "target2",
                sourceModel, modelRef(targetModel), "source2",
                true, FetchType.EAGER, null
        ));
        targetModel.addDataField(new BelongsTo(
                "source1", "source1",
                targetModel, modelRef(sourceModel),
                true, FetchType.EAGER, null
        ));
        targetModel.addDataField(new BelongsTo(
                "source2", "source2",
                targetModel, modelRef(sourceModel),
                true, FetchType.EAGER, null
        ));

        exportCode("circular", sourceModel, targetModel);
    }

    @Test
    public void testOneToManyAggregate() throws IOException {
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
    public void testManyToManyReference() throws IOException {
        EntityModel sourceModel = modelManySourceEntity();
        EntityModel targetModel = modelManyTargetEntity();
        sourceModel.addDataField(new ManyToMany(
                "targets", metadataManager,
                sourceModel, modelRef(targetModel), null,
                FetchType.EAGER, null
        ));
        targetModel.addDataField(new ManyToMany(
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

    private EntityModel modelDualSourceEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field strField = new SimpleField("strValue", "str_value", SimpleDataType.STRING, false, true);

        EntityModel entityModel = new EntityModel(
                "DualSourceEntity", "dual_source_entity",
                "auto", idField);
        entityModel.addDataField(strField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModel modelDualTargetEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field strField = new SimpleField("strValue", "str_value", SimpleDataType.STRING, false, true);

        EntityModel entityModel = new EntityModel(
                "DualTargetEntity", "dual_target_entity",
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

    private EntityModel modelManySourceEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field strField = new SimpleField("strValue", "str_value", SimpleDataType.STRING, false, true);

        EntityModel entityModel = new EntityModel(
                "ManySourceEntity", "many_source_entity",
                "auto", idField);
        entityModel.addDataField(strField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

    private EntityModel modelManyTargetEntity() {
        Field idField = new SimpleField("id", "id", SimpleDataType.LONG, true, true);
        Field intField = new SimpleField("intValue", "int_value", SimpleDataType.INT, false, true);

        EntityModel entityModel = new EntityModel(
                "ManyTargetEntity", "many_target_entity",
                "auto", idField);
        entityModel.addDataField(intField);
        metadataManager.addEntityModel(entityModel);
        return entityModel;
    }

}
