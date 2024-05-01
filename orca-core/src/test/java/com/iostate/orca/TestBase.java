package com.iostate.orca;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.core.EntityManagerImpl;
import com.iostate.orca.db.DataSourceUtil;
import com.iostate.orca.db.DbType;
import com.iostate.orca.db.SchemaBuilderFactory;
import com.iostate.orca.db.TestConnectionProvider;
import com.iostate.orca.metadata.MetadataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TestBase {

    protected MetadataManager metadataManager;
    protected TestConnectionProvider connectionProvider;
    protected EntityManager entityManager;

    protected abstract Class<?>[] entities();

    @BeforeEach
    public void setupCore() throws Exception {
        metadataManager = new MetadataManager();
        for (Class<?> entityClass : entities()) {
            metadataManager.findEntityByClass(entityClass);
        }
        String dbTypeString = System.getenv("ORCA_DB_TYPE");
        if (dbTypeString == null || dbTypeString.isEmpty()) {
            dbTypeString = "h2";
        }
        DbType dbType = DbType.of(dbTypeString);
        connectionProvider = TestConnectionProvider.of(DataSourceUtil.create(dbType));
        SchemaBuilderFactory.make(dbType).build(connectionProvider, metadataManager);
        entityManager = new EntityManagerImpl(metadataManager, connectionProvider).asDefault();
    }

    @AfterEach
    public void teardown() throws Exception {
        connectionProvider.closeConnection();
        metadataManager = null;
        connectionProvider = null;
        entityManager = null;
    }

    // Handle DB decimal digit issue
    protected static void checkEqual(BigDecimal expected, BigDecimal actual) {
        if (expected == null) {
            assertNull(actual, "expected: null, actual: not null");
        } else {
            assertEquals(0, expected.compareTo(actual),
                    String.format("expected: %s, actual: %s", expected, actual));
        }
    }

    // Handle DB time precision issue
    protected static void checkEqual(Instant expected, Instant actual) {
        if (expected == null) {
            assertNull(actual, "expected: null, actual: not null");
        } else {
            assertEquals(expected.toEpochMilli(), actual.toEpochMilli(),
                    String.format("expected: %s, actual: %s", expected, actual));
        }
    }
}
