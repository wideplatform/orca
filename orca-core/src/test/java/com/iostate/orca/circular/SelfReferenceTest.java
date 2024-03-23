package com.iostate.orca.circular;

import com.iostate.orca.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SelfReferenceTest extends TestBase {
    @Override
    protected Class<?>[] entities() {
        return new Class[]{SelfEntity.class};
    }

    @Test
    public void testCreateWithForwardReference() {
        SelfEntity preparedSource = new SelfEntity();
        SelfEntity preparedTarget = new SelfEntity();
        // Save with source->target forward reference
        preparedSource.setTarget(preparedTarget);

        entityManager.persist(preparedSource);
        entityManager.persist(preparedTarget);

        SelfEntity foundSource = entityManager.find(SelfEntity.class, preparedSource.getId());
        assertEquals(preparedSource, foundSource);

        SelfEntity foundTarget = foundSource.getTarget();
        assertEquals(preparedTarget, foundTarget);
        assertSame(foundSource, foundTarget.getSource());
    }

    @Test
    public void testCreateWithBackwardReference() {
        SelfEntity preparedSource = new SelfEntity();
        SelfEntity preparedTarget = new SelfEntity();
        // Save with target->source backward reference
        preparedTarget.setSource(preparedSource);

        entityManager.persist(preparedSource);
        entityManager.persist(preparedTarget);

        SelfEntity foundSource = entityManager.find(SelfEntity.class, preparedSource.getId());
        assertEquals(preparedSource, foundSource);

        SelfEntity foundTarget = foundSource.getTarget();
        assertEquals(preparedTarget, foundTarget);
        assertSame(foundSource, foundTarget.getSource());
    }
}
