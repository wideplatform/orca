package com.iostate.example.persistence;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.db.DbType;
import com.iostate.orca.db.SchemaBuilderFactory;
import com.iostate.orca.utils.CodeUtils;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.MetadataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class OrcaInitializer implements ApplicationRunner {
    @Autowired
    private MetadataManager metadataManager;

    @Autowired
    private ConnectionProvider connectionProvider;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        EntityModel entityModel = metadataManager.findEntityByName("Item");

        String packageName = "com.iostate.example.persistence.entity";
        String javaCode = metadataManager.generateJava(entityModel, "", packageName);
        CodeUtils.writeJavaFile("orca-example/src/main/java", packageName + ".Item", javaCode);
        String dbType = System.getProperty("mdp.db.type", "h2");
        SchemaBuilderFactory.make(DbType.of(dbType)).build(connectionProvider, metadataManager);
    }
}
