package com.iostate.orca.query;

import com.iostate.orca.TestBase;
import com.iostate.orca.onetomany.ChildEntity;
import com.iostate.orca.onetomany.ParentEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.iostate.orca.query.predicate.Predicates.in;
import static com.iostate.orca.query.predicate.Predicates.isNotNull;
import static com.iostate.orca.query.predicate.Predicates.isNull;
import static com.iostate.orca.query.predicate.Predicates.notEqual;
import static org.junit.jupiter.api.Assertions.*;

public class OneToManyQueryTest extends TestBase {

    @Override
    protected Class<?>[] entities() {
        return new Class[]{
                ParentEntity.class,
                ChildEntity.class
        };
    }

    @Test
    public void testQueryParentWithChildren() {
        Query<ParentEntity> query = Query.from(metadataManager, ParentEntity.class)
                .where(in("children.integer", Arrays.asList(1, 2)).or(isNull("children")))
                .andWhere(notEqual("string", "").and(isNotNull("string")));
        assertEquals(
                "SELECT * FROM parent_entity WHERE (children.integer IN (1, 2) OR children IS NULL) AND string <> '' AND string IS NOT NULL",
                query.toString());
    }
}
