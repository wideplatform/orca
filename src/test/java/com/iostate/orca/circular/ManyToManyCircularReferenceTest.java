package com.iostate.orca.circular;

import com.iostate.orca.TestBase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ManyToManyCircularReferenceTest extends TestBase {
    @Override
    protected Class<?>[] entities() {
        return new Class[] {ManySourceEntity.class, ManyTargetEntity.class};
    }

    @Test
    public void testCreateWithForwardReference() {
        ManySourceEntity preparedSource1 = new ManySourceEntity();
        ManySourceEntity preparedSource2 = new ManySourceEntity();
        ManyTargetEntity preparedTarget1 = new ManyTargetEntity();
        ManyTargetEntity preparedTarget2 = new ManyTargetEntity();
        // Save with source->target forward reference
        preparedSource1.getTargets().add(preparedTarget1);
        preparedSource2.getTargets().add(preparedTarget1);
        preparedSource2.getTargets().add(preparedTarget2);

        entityManager.persist(preparedTarget1);
        entityManager.persist(preparedTarget2);
        entityManager.persist(preparedSource1);
        entityManager.persist(preparedSource2);

        // Expected: s1->[t1], s2->[t1, t2], t1->[s1, s2], t2->[s2]
        ManySourceEntity foundSource1 = entityManager.find(ManySourceEntity.class, preparedSource1.getId());
        ManySourceEntity foundSource2 = entityManager.find(ManySourceEntity.class, preparedSource2.getId());
        assertEquals(1, foundSource1.getTargets().size());
        assertEquals(2, foundSource2.getTargets().size());
        assertEquals(2, foundSource1.getTargets().get(0).getSources().size());
        assertEquals(Arrays.asList(1, 2), foundSource2.getTargets().stream().map(t -> t.getSources().size()).sorted().collect(Collectors.toList()));
    }

    @Test
    public void testCreateWithBackwardReference() {
        ManySourceEntity preparedSource1 = new ManySourceEntity();
        ManySourceEntity preparedSource2 = new ManySourceEntity();
        ManyTargetEntity preparedTarget1 = new ManyTargetEntity();
        ManyTargetEntity preparedTarget2 = new ManyTargetEntity();
        // Save with target->source backward reference
        preparedTarget1.getSources().add(preparedSource1);
        preparedTarget2.getSources().add(preparedSource1);
        preparedTarget2.getSources().add(preparedSource2);

        entityManager.persist(preparedSource1);
        entityManager.persist(preparedSource2);
        entityManager.persist(preparedTarget1);
        entityManager.persist(preparedTarget2);

        // Expected: t1->[s1], t2->[s1, s2], s1->[t1, t2], s2->[t2]
        ManyTargetEntity foundTarget1 = entityManager.find(ManyTargetEntity.class, preparedTarget1.getId());
        ManyTargetEntity foundTarget2 = entityManager.find(ManyTargetEntity.class, preparedTarget2.getId());
        assertEquals(1, foundTarget1.getSources().size());
        assertEquals(2, foundTarget2.getSources().size());
        assertEquals(2, foundTarget1.getSources().get(0).getTargets().size());
        assertEquals(Arrays.asList(1, 2), foundTarget2.getSources().stream().map(s -> s.getTargets().size()).sorted().collect(Collectors.toList()));
    }
}
