package com.iostate.orca;

import com.iostate.orca.api.exception.PersistenceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleCrudTest extends TestBase {

    @Override
    protected Class<?>[] entities() {
        return new Class[]{SimpleEntity.class};
    }

    @Test
    public void testCreate() {
        SimpleEntity entity = new SimpleEntity();

        entityManager.persist(entity);

        SimpleEntity result = entityManager.find(SimpleEntity.class, entity.getId());
        assertNotNull(result.getId());
        assertEquals(result.getId(), entity.getId());
    }

    @Test
    public void testCreateTwiceFails() {
        SimpleEntity first = new SimpleEntity();
        first.setId(1L);
        entityManager.persist(first);

        SimpleEntity second = new SimpleEntity();
        second.setId(1L);
        assertThrows(PersistenceException.class, () -> entityManager.persist(second));
    }

    @Test
    public void testCreateNullFails() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> entityManager.persist(null));
        assertEquals("entity is null", e.getMessage());
    }

    @Test
    public void testCreateExistentFails() {
        SimpleEntity entity = new SimpleEntity();

        entityManager.persist(entity);
        Exception e = assertThrows(IllegalStateException.class, () -> entityManager.persist(entity));
        assertEquals("entity is already persisted thus unable to persist", e.getMessage());
    }

    @Test
    public void testRetrieve() {
        SimpleEntity prepared = prepare();

        SimpleEntity result = entityManager.find(SimpleEntity.class, prepared.getId());

        assertEquals(prepared.getId(), result.getId());
    }

    @Test
    public void testRetrieveNullFails() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> entityManager.find(SimpleEntity.class, null));
        assertEquals("id is null", e.getMessage());
    }

    @Test
    public void testUpdate() {
        SimpleEntity prepared = prepare();

        prepared.setString("abc");
        prepared.setInteger(123);
        prepared.setBool(true);
        entityManager.update(prepared);

        SimpleEntity result = entityManager.find(SimpleEntity.class, prepared.getId());
        assertEquals("abc", result.getString());
        assertEquals((Integer) 123, result.getInteger());
        assertEquals(true, result.getBool());
    }

    @Test
    public void testUpdateNullFails() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> entityManager.update(null));
        assertEquals("entity is null", e.getMessage());
    }

    @Test
    public void testUpdateNonExistentFails() {
        SimpleEntity entity = new SimpleEntity();
        entity.setId(1L);

        Exception e = assertThrows(IllegalStateException.class, () -> entityManager.update(entity));
        assertEquals("entity is not persisted thus unable to update", e.getMessage());
    }

    @Test
    public void testMergeNonExistent() {
        SimpleEntity entity = new SimpleEntity();

        entityManager.merge(entity);
    }

    @Test
    public void testMergeExistent() {
        SimpleEntity entity = new SimpleEntity();
        entityManager.persist(entity);

        entity.setBool(true);
        entityManager.merge(entity);
    }

    @Test
    public void testRemove() {
        SimpleEntity prepared = prepare();

        entityManager.remove(SimpleEntity.class, prepared.getId());

        SimpleEntity result = entityManager.find(SimpleEntity.class, prepared.getId());
        assertNull(result);
    }

    @Test
    public void testRemoveTwice() {
        SimpleEntity prepared = prepare();

        entityManager.remove(SimpleEntity.class, prepared.getId());
        entityManager.remove(SimpleEntity.class, prepared.getId());

        SimpleEntity result = entityManager.find(SimpleEntity.class, prepared.getId());
        assertNull(result);
    }

    @Test
    public void testRemoveNullFails() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> entityManager.remove(SimpleEntity.class, null));
        assertEquals("id is null", e.getMessage());
    }

    private SimpleEntity prepare() {
        SimpleEntity prepared = new SimpleEntity();
        entityManager.persist(prepared);
        return prepared;
    }
}
