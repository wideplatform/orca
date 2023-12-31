package com.iostate.orca;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.Namespace;
import com.iostate.orca.db.DbInitializer;
import com.iostate.orca.metadata.MetadataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public abstract class TestBase {

    protected MetadataManager metadataManager;
    protected DbInitializer dbInitializer;
    protected EntityManager entityManager;

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    protected abstract Class<?>[] entities();

    @BeforeEach
    public void setup() throws Exception {
        metadataManager = new MetadataManager();
        for (Class<?> entityClass : entities()) {
            String namespace = entityClass.getAnnotation(Namespace.class).value();
            String directory = namespace.isEmpty() ? "" : namespace + '/';
            String path = "models/" + directory + entityClass.getSimpleName() + ".yml";
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
                byte[] bytes = in.readAllBytes();
                metadataManager.loadEntityModel(new String(bytes, StandardCharsets.UTF_8));
            }
        }

        dbInitializer = new DbInitializer(metadataManager);
        dbInitializer.execute();

        entityManager = new EntityManagerImpl(metadataManager, dbInitializer.getConnectionProvider());
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
    public void teardown() {
        metadataManager = null;
        dbInitializer = null;
        entityManager = null;
    }
}
