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

public class AssociatedEntityModelTest extends EntityModelGenerationTestBase {

    @Test
    public void testOneToOne() throws IOException {
        EntityModel parentModel = modelParentEntity();
        EntityModel childModel = modelChildEntity();
        parentModel.addDataField(
                new HasOne(
                        "child",
                        parentModel, modelRef(childModel), "parent",
                        true,
                        FetchType.EAGER, new CascadeType[]{CascadeType.ALL})
        );
        childModel.addDataField(
                new BelongsTo(
                        "parent", "parent_id",
                        childModel, modelRef(parentModel),
                        false,
                        FetchType.EAGER, null)
        );

        exportCode("onetoone", parentModel, childModel);
    }

    @Test
    public void testOneToMany() throws IOException {
        EntityModel parentModel = modelParentEntity();
        EntityModel childModel = modelChildEntity();
        parentModel.addDataField(
                new HasMany(
                        "children",
                        parentModel, modelRef(childModel), "parent",
                        FetchType.EAGER, new CascadeType[]{CascadeType.ALL})
        );
        childModel.addDataField(
                new BelongsTo(
                        "parent", "parent_id",
                        childModel, modelRef(parentModel),
                        false, FetchType.EAGER, null
                )
        );

        exportCode("onetomany", parentModel, childModel);
    }

    @Test
    public void testManyToOne() throws IOException {
        EntityModel sourceModel = modelSourceEntity();
        EntityModel targetModel = modelTargetEntity();
        sourceModel.addDataField(new BelongsTo(
                "target", "target_id",
                sourceModel, modelRef(targetModel),
                true, FetchType.LAZY, new CascadeType[]{}
        ));

        exportCode("manytoone", sourceModel, targetModel);
    }

    @Test
    public void testManyToMany() throws IOException {
        EntityModel sourceModel = modelSourceEntity();
        EntityModel targetModel = modelTargetEntity();
        sourceModel.addDataField(new HasAndBelongsToMany(
                "targets", metadataManager,
                sourceModel, modelRef(targetModel), null,
                FetchType.EAGER, new CascadeType[]{}
        ));
        targetModel.addDataField(new HasAndBelongsToMany(
                "sources", metadataManager,
                targetModel, modelRef(sourceModel), "targets",
                FetchType.EAGER, new CascadeType[]{}
        ));

        exportCode("manytomany", sourceModel, targetModel);
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
