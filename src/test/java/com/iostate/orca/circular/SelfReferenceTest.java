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
    public void testCreateWithBackwardReference() {
        SelfEntity preparedSource = new SelfEntity();
        SelfEntity preparedTarget = new SelfEntity();
        // Save with target->source backward reference
        preparedTarget.setSource(preparedSource);

        entityManager.persist(preparedSource);
        entityManager.persist(preparedTarget);

        SelfEntity foundSource = entityManager.find(SelfEntity.class, preparedSource.getId());
        assertEquals(preparedSource.getId(), foundSource.getId());
        assertNotNull(foundSource.getTarget());
        assertEquals(preparedTarget.getId(), foundSource.getTarget().getId());
        assertNotNull(foundSource.getTarget().getSource());
        assertEquals(preparedSource.getId(), foundSource.getTarget().getSource().getId());
    }
}
