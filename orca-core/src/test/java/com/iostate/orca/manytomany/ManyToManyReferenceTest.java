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
        SourceEntity preparedParent = preparePersisted();

        preparedParent.setStrValue("updated");
        preparedParent.getTargets().get(0).setIntValue(1);
        preparedParent.getTargets().get(1).setIntValue(2);

        entityManager.update(preparedParent);

        SourceEntity resultSource = entityManager.find(SourceEntity.class, preparedParent.getId());
        assertNotNull(resultSource.getId());
        assertEquals("updated", resultSource.getStrValue());
        // not updated
        List<TargetEntity> resultTargets = resultSource.getTargets();
        assertNull(resultTargets.get(0).getIntValue());
        assertNull(resultTargets.get(1).getIntValue());
    }
    
    @Test
    public void testRefreshSourceOnly() {
        SourceEntity source = preparePersisted();
        source.setStrValue("updated");
        source.getTargets().get(0).setIntValue(1);
        source.getTargets().get(1).setIntValue(2);
        entityManager.update(source);
        entityManager.update(source.getTargets().get(0));
        entityManager.update(source.getTargets().get(1));

        source.populateFieldValue("strValue", null);
        source.getTargets().get(0).populateFieldValue("intValue", null);
        source.getTargets().get(1).populateFieldValue("intValue", null);

        entityManager.refresh(source);

        assertEquals("updated", source.getStrValue());
        assertNull(source.getTargets().get(0).getIntValue());
        assertNull(source.getTargets().get(1).getIntValue());
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
        List<TargetEntity> targets = Arrays.asList(new TargetEntity(), new TargetEntity());

        SourceEntity source = new SourceEntity();
        source.setTargets(targets);

        String message = assertThrows(PersistenceException.class, () -> entityManager.persist(source)).getMessage();
        assertEquals("Failed to relate non-persisted TargetEntity to SourceEntity without cascading", message);
    }

    @Test
    public void testUpdateStandalone() {
        SourceEntity preparedParent = preparePersisted();

        preparedParent.setStrValue("updated");
        preparedParent.getTargets().get(0).setIntValue(1);
        preparedParent.getTargets().get(1).setIntValue(2);

        entityManager.update(preparedParent);
        entityManager.update(preparedParent.getTargets().get(0));
        entityManager.update(preparedParent.getTargets().get(1));

        SourceEntity resultSource = entityManager.find(SourceEntity.class, preparedParent.getId());
        assertNotNull(resultSource.getId());
        assertEquals("updated", resultSource.getStrValue());
        List<TargetEntity> resultTargets = resultSource.getTargets();
        assertEquals(1, resultTargets.get(0).getIntValue());
        assertEquals(2, resultTargets.get(1).getIntValue());
    }

    @Test
    public void testRefreshStandalone() {
        SourceEntity source = preparePersisted();
        source.setStrValue("updated");
        source.getTargets().get(0).setIntValue(1);
        source.getTargets().get(1).setIntValue(2);
        entityManager.update(source);
        entityManager.update(source.getTargets().get(0));
        entityManager.update(source.getTargets().get(1));

        source.populateFieldValue("strValue", null);
        source.getTargets().get(0).populateFieldValue("intValue", null);
        source.getTargets().get(1).populateFieldValue("intValue", null);

        entityManager.refresh(source);
        entityManager.refresh(source.getTargets().get(0));
        entityManager.refresh(source.getTargets().get(1));

        assertEquals("updated", source.getStrValue());
        assertEquals(1, source.getTargets().get(0).getIntValue());
        assertEquals(2, source.getTargets().get(1).getIntValue());
    }

    private SourceEntity preparePersisted() {
        SourceEntity source = prepare();
        source.getTargets().forEach(entityManager::persist);
        entityManager.persist(source);
        return source;
    }

    private SourceEntity prepare() {
        SourceEntity source = new SourceEntity();
        source.setTargets(Arrays.asList(new TargetEntity(), new TargetEntity()));
        return source;
    }
}
