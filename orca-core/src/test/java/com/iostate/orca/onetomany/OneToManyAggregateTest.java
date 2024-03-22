package com.iostate.orca.onetomany;

import com.iostate.orca.TestBase;
import org.junit.jupiter.api.Test;

import java.util.function.IntSupplier;

import static org.junit.jupiter.api.Assertions.*;

public class OneToManyAggregateTest extends TestBase {

    @Override
    protected Class<?>[] entities() {
        return new Class[]{ParentEntity.class, ChildEntity.class};
    }

    @Test
    public void testCreateParentOnly() {
        ParentEntity preparedParent = new ParentEntity();

        entityManager.persist(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        assertEquals(0, resultParent.getChildren().size());
    }

    @Test
    public void testUpdateParentOnly() {
        ParentEntity preparedParent = new ParentEntity();
        entityManager.persist(preparedParent);

        preparedParent.setStrValue("updated");

        entityManager.update(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        assertEquals(0, resultParent.getChildren().size());
        assertEquals("updated", resultParent.getStrValue());
    }

    @Test
    public void testCreateParentShouldCascade() {
        ParentEntity preparedParent = prepare();

        entityManager.persist(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        assertEquals(2, resultParent.getChildren().size());
        ChildEntity child1 = resultParent.getChildren().get(0);
        ChildEntity child2 = resultParent.getChildren().get(1);
        assertNotNull(child1.getId());
        assertNotNull(child2.getId());
        assertNotEquals(child1.getId(), child2.getId());
    }

    @Test
    public void testUpdateParentShouldCascade() {
        ParentEntity preparedParent = prepare();
        entityManager.persist(preparedParent);

        preparedParent.setStrValue("updated");
        preparedParent.getChildren().get(0).setIntValue(1);
        preparedParent.getChildren().get(1).setIntValue(2);

        entityManager.update(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        assertEquals(2, resultParent.getChildren().size());
        ChildEntity child1 = resultParent.getChildren().get(0);
        ChildEntity child2 = resultParent.getChildren().get(1);
        assertNotNull(child1.getId());
        assertNotNull(child2.getId());
        assertNotEquals(child1.getId(), child2.getId());

        assertEquals("updated", resultParent.getStrValue());
        assertEquals(1, child1.getIntValue());
        assertEquals(2, child2.getIntValue());
    }

    @Test
    public void testDeleteParentShouldCascade() {
        ParentEntity preparedParent = prepare();
        entityManager.persist(preparedParent);
        IntSupplier childrenCount = () -> entityManager.findBy(
                ChildEntity.class, "parent.id", preparedParent.getId()
        ).size();
        assertEquals(2, childrenCount.getAsInt());

        entityManager.remove(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNull(resultParent);
        assertEquals(0, childrenCount.getAsInt());
    }

    @Test
    public void testRefreshParentShouldCascade() {
        ParentEntity parent = prepare();
        parent.setStrValue("abc");
        parent.getChildren().get(0).setIntValue(123);
        entityManager.persist(parent);

        parent.populateFieldValue("strValue", null);
        parent.getChildren().get(0).populateFieldValue("intValue", null);
        parent.getChildren().get(1).populateFieldValue("intValue", 123);

        entityManager.refresh(parent);
        assertEquals("abc", parent.getStrValue());
        assertEquals(123, parent.getChildren().get(0).getIntValue());
        assertNull(parent.getChildren().get(1).getIntValue());
    }

    private ParentEntity prepare() {
        ParentEntity preparedParent = new ParentEntity();
        preparedParent.getChildren().add(new ChildEntity());
        preparedParent.getChildren().add(new ChildEntity());
        return preparedParent;
    }
}
