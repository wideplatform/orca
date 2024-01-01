package com.iostate.orca.query;

import com.iostate.orca.TestBase;
import com.iostate.orca.onetoone.ChildEntity;
import com.iostate.orca.onetoone.ParentEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.iostate.orca.query.predicate.Predicates.in;
import static com.iostate.orca.query.predicate.Predicates.isNotNull;
import static com.iostate.orca.query.predicate.Predicates.isNull;
import static com.iostate.orca.query.predicate.Predicates.notEqual;
import static org.junit.jupiter.api.Assertions.*;

public class OneToOneQueryTest extends TestBase {

    @Override
    protected Class<?>[] entities() {
        return new Class[]{
                ParentEntity.class,
                ChildEntity.class
        };
    }

    @Test
    public void testQueryParentWithChild() {
        Query<ParentEntity> query = Query.from(metadataManager, ParentEntity.class)
                .where(in("child.integer", Arrays.asList(1, 2)).or(isNull("child")))
                .andWhere(notEqual("string", "").and(isNotNull("string")));

        assertEquals(
                "SELECT * FROM parent_entity WHERE (child.integer IN (1, 2) OR child IS NULL) AND string <> '' AND string IS NOT NULL",
                query.toString());
    }
}
