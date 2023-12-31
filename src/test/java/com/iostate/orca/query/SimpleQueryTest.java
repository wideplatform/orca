package com.iostate.orca.query;

import com.iostate.orca.SimpleEntity;
import com.iostate.orca.TestBase;
import org.junit.jupiter.api.Test;

import static com.iostate.orca.query.predicate.Predicates.equal;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleQueryTest extends TestBase {

    @Override
    protected Class<?>[] entities() {
        return new Class[]{
                SimpleEntity.class
        };
    }

    @Test
    public void testFindAll() {
        Query<SimpleEntity> query = Query.from(metadataManager, SimpleEntity.class);

        assertEquals("SELECT * FROM simple_entity", query.toString());
    }

    @Test
    public void testFindByField() {
        Query<SimpleEntity> query = Query.from(metadataManager, SimpleEntity.class)
                .where(equal("string", "a"));

        assertEquals("SELECT * FROM simple_entity WHERE string = 'a'", query.toString());
    }

    @Test
    public void testFindByFieldsAnd() {
        Query<SimpleEntity> query = Query.from(metadataManager, SimpleEntity.class)
                .where(equal("string", "a"))
                .andWhere(equal("bool", true));


        assertEquals("SELECT * FROM simple_entity WHERE string = 'a' AND bool = true", query.toString());
    }

    @Test
    public void testFindByFieldsOr() {
        Query<SimpleEntity> query = Query.from(metadataManager, SimpleEntity.class)
                .where(equal("string", "a"))
                .orWhere(equal("bool", true));

        assertEquals("SELECT * FROM simple_entity WHERE string = 'a' OR bool = true", query.toString());
    }

    @Test
    public void testSelectFields() {
        Query<SimpleEntity> query = Query.from(metadataManager, SimpleEntity.class)
                .select("id", "string");

        assertEquals("SELECT id, string FROM simple_entity", query.toString());
    }
}
