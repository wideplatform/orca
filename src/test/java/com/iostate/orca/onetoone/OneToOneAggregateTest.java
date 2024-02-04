package com.iostate.orca.onetoone;

import com.iostate.orca.TestBase;
import org.junit.jupiter.api.Test;

import java.util.function.IntSupplier;

import static org.junit.jupiter.api.Assertions.*;

public class OneToOneAggregateTest extends TestBase {

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
        ChildEntity child = resultParent.getChild();
        assertNull(child);
    }

    @Test
    public void testUpdateParentOnly() {
        ParentEntity preparedParent = new ParentEntity();
        entityManager.persist(preparedParent);

        preparedParent.setString("updated");

        entityManager.update(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        assertEquals("updated", resultParent.getString());
        ChildEntity child = resultParent.getChild();
        assertNull(child);
    }

    @Test
    public void testCreateParentShouldCascade() {
        ParentEntity preparedParent = prepare();

        entityManager.persist(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        ChildEntity child = resultParent.getChild();
        assertNotNull(child);
        assertNotNull(child.getId());
    }

    @Test
    public void testUpdateParentShouldCascade() {
        ParentEntity preparedParent = prepare();
        entityManager.persist(preparedParent);

        preparedParent.setString("updated");
        preparedParent.getChild().setInteger(1);

        entityManager.update(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        assertEquals("updated", resultParent.getString());
        ChildEntity child = resultParent.getChild();
        assertNotNull(child.getId());
        assertEquals(1, child.getInteger().intValue());
    }

    @Test
    public void testDeleteParentShouldCascade() {
        ParentEntity preparedParent = prepare();
        entityManager.persist(preparedParent);
        IntSupplier childrenCount = () -> entityManager.findBy(
                ChildEntity.class, "parent.id", preparedParent.getId()
        ).size();
        assertEquals(1, childrenCount.getAsInt());

        entityManager.remove(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNull(resultParent);
        assertEquals(0, childrenCount.getAsInt());
    }

    @Test
    public void testUpdateChildShouldNotCascade() {
        ParentEntity preparedParent = prepare();
        entityManager.persist(preparedParent);

        preparedParent.setString("updated");
        preparedParent.getChild().setInteger(1);

        entityManager.update(preparedParent.getChild());

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        // Not updated
        assertNull(resultParent.getString());
        ChildEntity child = resultParent.getChild();
        assertNotNull(child.getId());
        // Updated
        assertEquals(1, child.getInteger().intValue());
    }

    @Test
    public void testDeleteChildShouldNotCascade() {
        ParentEntity preparedParent = prepare();
        entityManager.persist(preparedParent);

        entityManager.remove(preparedParent.getChild());

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        ChildEntity child = resultParent.getChild();
        assertNull(child);
    }

    @Test
    public void testFindByChildField() {
        ParentEntity preparedParent = prepare();
        preparedParent.getChild().setInteger(123);

        entityManager.persist(preparedParent);

        ParentEntity resultParent = entityManager.findBy(ParentEntity.class, "child.integer", 123).get(0);
        assertNotNull(resultParent.getId());
        ChildEntity child = resultParent.getChild();
        assertNotNull(child);
        assertNotNull(child.getId());
    }

    private ParentEntity prepare() {
        ParentEntity preparedParent = new ParentEntity();
        preparedParent.setChild(new ChildEntity());
        return preparedParent;
    }
}
