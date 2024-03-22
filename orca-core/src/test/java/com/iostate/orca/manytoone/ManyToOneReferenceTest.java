package com.iostate.orca.manytoone;

import com.iostate.orca.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManyToOneReferenceTest extends TestBase {

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
        assertNull(resultSource.getTarget());
    }

    @Test
    public void testUpdateSourceOnly() {
        SourceEntity preparedSource = preparePersisted();

        preparedSource.setStrValue("updated");
        preparedSource.getTarget().setIntValue(1);

        entityManager.update(preparedSource);

        SourceEntity resultSource = entityManager.find(SourceEntity.class, preparedSource.getId());
        assertNotNull(resultSource.getId());
        assertEquals("updated", resultSource.getStrValue());
        // not updated
        TargetEntity target = resultSource.getTarget();
        assertNull(target.getIntValue());
    }

    @Test
    public void testRefreshSourceOnly() {
        SourceEntity source = preparePersisted();
        source.setStrValue("updated");
        source.getTarget().setIntValue(123);
        entityManager.update(source);
        entityManager.update(source.getTarget());

        source.populateFieldValue("strValue", null);
        source.getTarget().populateFieldValue("intValue", null);

        entityManager.refresh(source);

        assertEquals("updated", source.getStrValue());
        assertNull(source.getTarget().getIntValue());
    }

    @Test
    public void testCreateStandalone() {
        TargetEntity target = new TargetEntity();
        entityManager.persist(target);
        SourceEntity source = new SourceEntity();
        source.setTarget(target);

        entityManager.persist(source);

        SourceEntity resultSource = entityManager.find(SourceEntity.class, source.getId());
        assertNotNull(resultSource.getId());
        TargetEntity resultTarget = resultSource.getTarget();
        assertNotNull(resultTarget);
        assertNotNull(resultTarget.getId());
    }

    @Test
    public void testUpdateStandalone() {
        SourceEntity preparedSource = preparePersisted();

        preparedSource.setStrValue("updated");
        preparedSource.getTarget().setIntValue(1);

        entityManager.update(preparedSource);
        entityManager.update(preparedSource.getTarget());

        SourceEntity resultSource = entityManager.find(SourceEntity.class, preparedSource.getId());
        assertNotNull(resultSource.getId());
        assertEquals("updated", resultSource.getStrValue());
        TargetEntity target = resultSource.getTarget();
        assertEquals(1, target.getIntValue());
    }

    @Test
    public void testRefreshStandalone() {
        SourceEntity source = preparePersisted();
        source.setStrValue("updated");
        source.getTarget().setIntValue(123);
        entityManager.update(source);
        entityManager.update(source.getTarget());

        source.populateFieldValue("strValue", null);
        source.getTarget().populateFieldValue("intValue", null);

        entityManager.refresh(source);
        entityManager.refresh(source.getTarget());

        assertEquals("updated", source.getStrValue());
        assertEquals(123, source.getTarget().getIntValue());
    }

    private SourceEntity preparePersisted() {
        SourceEntity source = prepare();
        entityManager.persist(source.getTarget());
        entityManager.persist(source);
        return source;
    }

    private SourceEntity prepare() {
        SourceEntity source = new SourceEntity();
        source.setTarget(new TargetEntity());
        return source;
    }
}
