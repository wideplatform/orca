package com.iostate.orca.manytomany;

import com.iostate.orca.TestBase;
import com.iostate.orca.api.exception.PersistenceException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManyToManyReferenceTest extends TestBase {
    @Override
    protected Class<?>[] entities() {
        return new Class[]{SourceEntity.class, TargetEntity.class};
    }

    @Test
    public void testCreateSourceOnly() {
        SourceEntity source = new SourceEntity();

        entityManager.persist(source);

        SourceEntity resultSource = entityManager.find(SourceEntity.class, source.getId());
        assertNotNull(resultSource.getId());
        assertEquals(0, resultSource.getTargets().size());
    }

    @Test
    public void testUpdateSourceOnly() {
        SourceEntity source = new SourceEntity();
        entityManager.persist(source);

        source.setString("updated");
        entityManager.update(source);

        SourceEntity resultSource = entityManager.find(SourceEntity.class, source.getId());
        assertNotNull(resultSource.getId());
        assertEquals(0, resultSource.getTargets().size());
        assertEquals("updated", resultSource.getString());
    }
    
    @Test
    public void testCreateStandalone() {
        List<TargetEntity> targets = Arrays.asList(new TargetEntity(), new TargetEntity());
        targets.forEach(entityManager::persist);

        SourceEntity source = new SourceEntity();
        source.setTargets(targets);
        entityManager.persist(source);

        SourceEntity resultParent = entityManager.find(SourceEntity.class, source.getId());
        assertNotNull(resultParent.getId());
        List<TargetEntity> resultTargets = resultParent.getTargets();
        assertEquals(2, resultTargets.size());
        assertNotNull(resultTargets.get(0).getId());
        assertNotNull(resultTargets.get(1).getId());
    }

    @Test
    public void testCreateStandaloneWithInvalidReferenceFails() {
        List<TargetEntity> targets = Arrays.asList(
                new TargetEntity(), new TargetEntity());

        SourceEntity source = new SourceEntity();
        source.setTargets(targets);

        assertThrows(PersistenceException.class, () -> entityManager.persist(source));
    }

    @Test
    public void testUpdateStandalone() {
        SourceEntity preparedParent = preparePersisted();

        preparedParent.setString("updated");
        preparedParent.getTargets().get(0).setInteger(1);
        preparedParent.getTargets().get(1).setInteger(2);

        entityManager.update(preparedParent);

        SourceEntity resultSource = entityManager.find(SourceEntity.class, preparedParent.getId());
        assertNotNull(resultSource.getId());
        assertEquals("updated", resultSource.getString());
        // not updated
        List<TargetEntity> resultTargets = resultSource.getTargets();
        assertEquals(0, resultTargets.get(0).getInteger());
        assertEquals(0, resultTargets.get(1).getInteger());
    }

    private SourceEntity preparePersisted() {
        SourceEntity preparedParent = prepare();
        preparedParent.getTargets().forEach(entityManager::persist);
        entityManager.persist(preparedParent);
        return preparedParent;
    }

    private SourceEntity prepare() {
        SourceEntity preparedParent = new SourceEntity();
        preparedParent.setTargets(Arrays.asList(new TargetEntity(), new TargetEntity()));
        return preparedParent;
    }
}
