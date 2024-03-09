package com.iostate.orca.circular;

import com.iostate.orca.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OneToOneDualAssociationsCircularReferenceTest extends TestBase {
    @Override
    protected Class<?>[] entities() {
        return new Class[]{DualSourceEntity.class, DualTargetEntity.class};
    }

    @Test
    public void testCreateWithForwardReference() {
        DualSourceEntity preparedSource = new DualSourceEntity();
        DualTargetEntity preparedTarget = new DualTargetEntity();
        preparedSource.setTarget1(preparedTarget);
        preparedSource.setTarget2(preparedTarget);

        entityManager.persist(preparedSource);
        entityManager.persist(preparedTarget);

        DualTargetEntity foundTarget = entityManager.find(DualTargetEntity.class, preparedTarget.getId());
        assertEquals(preparedSource.getId(), foundTarget.getSource1().getId());
        assertEquals(preparedSource.getId(), foundTarget.getSource2().getId());
    }

    @Test
    public void testCreateWithBackwardReference() {
        DualSourceEntity preparedSource = new DualSourceEntity();
        DualTargetEntity preparedTarget = new DualTargetEntity();
        preparedTarget.setSource1(preparedSource);
        preparedTarget.setSource2(preparedSource);

        entityManager.persist(preparedSource);
        entityManager.persist(preparedTarget);

        DualSourceEntity foundSource = entityManager.find(DualSourceEntity.class, preparedSource.getId());
        assertEquals(preparedTarget.getId(), foundSource.getTarget1().getId());
        assertEquals(preparedTarget.getId(), foundSource.getTarget2().getId());
    }
}
