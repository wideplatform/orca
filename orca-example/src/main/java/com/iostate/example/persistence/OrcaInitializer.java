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
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

@Component
public class OrcaInitializer implements ApplicationRunner {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MetadataManager metadataManager;

    @Autowired
    private ConnectionProvider connectionProvider;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File workingDir = new File("").getAbsoluteFile();
        Path sourceRoot;
        if (workingDir.getName().equals("orca-example")) {
            sourceRoot = workingDir.toPath().resolve("src/main/java");
        } else {
            sourceRoot = workingDir.toPath().resolve("orca-example/src/main/java");
        }

        String packageName = "com.iostate.example.persistence.entity";
        Resource[] resources = applicationContext.getResources("classpath:models/**");
        for (Resource resource : resources) {
            String modelName = resource.getFilename().replace(".yml", "");
            EntityModel entityModel = metadataManager.findEntityByName(modelName);
            if (!entityModel.getLinkedClassName().startsWith(packageName)) {
                throw new RuntimeException("Inconsistent package name for entity " + modelName);
            }
            String javaCode = metadataManager.generateJava(entityModel, "", packageName);
            CodeUtils.writeJavaFile(sourceRoot, entityModel.getLinkedClassName(), javaCode);
        }

        SchemaBuilderFactory.make(DbType.MYSQL).build(connectionProvider, metadataManager);
    }
}
