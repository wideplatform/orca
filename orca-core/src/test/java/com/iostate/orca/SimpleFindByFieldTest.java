package com.iostate.orca;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleFindByFieldTest extends TestBase {

    @Override
    protected Class<?>[] entities() {
        return new Class[]{SimpleEntity.class};
    }

    @Test
    public void testFindByString() {
        SimpleEntity blank = new SimpleEntity();
        entityManager.persist(blank);
        SimpleEntity entity = new SimpleEntity();
        entity.setStrValue("v");
        entityManager.persist(entity);

        List<SimpleEntity> result = entityManager.findBy(SimpleEntity.class, "strValue", "v");
        assertEquals(1, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
    }

    @Test
    public void testFindByInteger() {
        SimpleEntity blank = new SimpleEntity();
        entityManager.persist(blank);
        SimpleEntity entity = new SimpleEntity();
        entity.setIntValue(123);
        entityManager.persist(entity);

        List<SimpleEntity> result = entityManager.findBy(SimpleEntity.class, "intValue", 123);
        assertEquals(1, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
    }

    @Test
    public void testFindByBoolean() {
        SimpleEntity blank = new SimpleEntity();
        entityManager.persist(blank);
        SimpleEntity entity = new SimpleEntity();
        entity.setBoolValue(true);
        entityManager.persist(entity);

        List<SimpleEntity> result = entityManager.findBy(SimpleEntity.class, "boolValue", true);
        assertEquals(1, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
    }
}
