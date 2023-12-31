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
        SimpleEntity po = new SimpleEntity();
        po.setString("v");
        entityManager.persist(po);

        List<SimpleEntity> result = entityManager.findByField(SimpleEntity.class, "string", "v");
        assertEquals(1, result.size());
        assertEquals(po.getId(), result.get(0).getId());
    }

    @Test
    public void testFindByInteger() {
        SimpleEntity blank = new SimpleEntity();
        entityManager.persist(blank);
        SimpleEntity po = new SimpleEntity();
        po.setInteger(123);
        entityManager.persist(po);

        List<SimpleEntity> result = entityManager.findByField(SimpleEntity.class, "integer", 123);
        assertEquals(1, result.size());
        assertEquals(po.getId(), result.get(0).getId());
    }

    @Test
    public void testFindByBoolean() {
        SimpleEntity blank = new SimpleEntity();
        entityManager.persist(blank);
        SimpleEntity po = new SimpleEntity();
        po.setBool(true);
        entityManager.persist(po);

        List<SimpleEntity> result = entityManager.findByField(SimpleEntity.class, "bool", true);
        assertEquals(1, result.size());
        assertEquals(po.getId(), result.get(0).getId());
    }
}
