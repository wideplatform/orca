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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public abstract class TestBase {

    protected MetadataManager metadataManager;
    protected TestConnectionProvider connectionProvider;
    protected EntityManager entityManager;

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    protected abstract Class<?>[] entities();

    @BeforeEach
    public void setup() throws Exception {
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
        entityManager = new EntityManagerImpl(metadataManager, connectionProvider);
    }

    protected LocalDate date(String dateText) {
        return LocalDate.parse(dateText);
    }

    protected Date datetime(String dateTimeText) {
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT, Locale.US);
        try {
            return format.parse(dateTimeText);
        } catch (ParseException e) {
            return null;
        }
    }

    @AfterEach
    public void teardown() throws Exception {
        connectionProvider.closeConnection();
        metadataManager = null;
        connectionProvider = null;
        entityManager = null;
    }
}
