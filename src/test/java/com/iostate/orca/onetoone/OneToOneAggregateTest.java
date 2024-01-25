package com.iostate.orca.onetoone;

import com.iostate.orca.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OneToOneAggregateTest extends TestBase {

    @Override
    protected Class<?>[] entities() {
        return new Class[]{ParentEntity.class, ChildEntity.class};
    }

    @Test
    public void testCreateAll() {
        ParentEntity preparedParent = prepare();

        entityManager.persist(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        ChildEntity child = resultParent.getChild();
        assertNotNull(child);
        assertNotNull(child.getId());
    }

    @Test
    public void testUpdateAll() {
        ParentEntity preparedParent = prepare();
        entityManager.persist(preparedParent);

        preparedParent.setString("updated");
        preparedParent.getChild().setInteger(1);

        entityManager.update(preparedParent);

        ParentEntity resultParent = entityManager.find(ParentEntity.class, preparedParent.getId());
        assertNotNull(resultParent.getId());
        ChildEntity child = resultParent.getChild();
        assertNotNull(child);
        assertNotNull(child.getId());

        assertEquals("updated", resultParent.getString());
        assertEquals(1, child.getInteger().intValue());
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
