package com.iostate.orca.circular;

import com.iostate.orca.TestBase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CircularReferenceTest extends TestBase {
    @Override
    protected Class<?>[] entities() {
        return new Class[] {SourceEntity.class, TargetEntity.class};
    }

    @Test
    public void testCreateWithForwardReference() {
        SourceEntity preparedSource1 = new SourceEntity();
        SourceEntity preparedSource2 = new SourceEntity();
        TargetEntity preparedTarget1 = new TargetEntity();
        TargetEntity preparedTarget2 = new TargetEntity();
        // Save with source->target forward reference
        preparedSource1.getTargets().add(preparedTarget1);
        preparedSource2.getTargets().add(preparedTarget1);
        preparedSource2.getTargets().add(preparedTarget2);

        entityManager.persist(preparedTarget1);
        entityManager.persist(preparedTarget2);
        entityManager.persist(preparedSource1);
        entityManager.persist(preparedSource2);

        // Expected: s1->[t1], s2->[t1, t2], t1->[s1, s2], t2->[s2]
        SourceEntity foundSource1 = entityManager.find(SourceEntity.class, preparedSource1.getId());
        SourceEntity foundSource2 = entityManager.find(SourceEntity.class, preparedSource2.getId());
        assertEquals(1, foundSource1.getTargets().size());
        assertEquals(2, foundSource2.getTargets().size());
        assertEquals(2, foundSource1.getTargets().get(0).getSources().size());
        assertEquals(Arrays.asList(1, 2), foundSource2.getTargets().stream().map(t -> t.getSources().size()).sorted().collect(Collectors.toList()));
    }
}
