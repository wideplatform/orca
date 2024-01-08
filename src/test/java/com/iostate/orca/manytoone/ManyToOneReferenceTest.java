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

        preparedSource.setString("updated");
        preparedSource.getTarget().setInteger(1);

        entityManager.update(preparedSource);

        SourceEntity resultSource = entityManager.find(SourceEntity.class, preparedSource.getId());
        assertNotNull(resultSource.getId());
        assertEquals("updated", resultSource.getString());
        // not updated
        TargetEntity target = resultSource.getTarget();
        assertEquals(0, target.getInteger().intValue());
    }

    private SourceEntity preparePersisted() {
        SourceEntity preparedParent = prepare();
        entityManager.persist(preparedParent.getTarget());
        entityManager.persist(preparedParent);
        return preparedParent;
    }

    private SourceEntity prepare() {
        SourceEntity preparedParent = new SourceEntity();
        preparedParent.setTarget(new TargetEntity());
        return preparedParent;
    }
}
