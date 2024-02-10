package com.iostate.orca;

import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.EntityModelRef;
import com.iostate.orca.metadata.MetadataManager;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EntityModelGenerationTestBase {

    private static final String BASE_PACKAGE = "com.iostate.orca";

    protected MetadataManager metadataManager;

    @BeforeEach
    public void setup() {
        metadataManager = new MetadataManager();
    }

    // namespace can be empty standing for the default namespace
    protected void exportCode(String namespace, EntityModel... entityModels) throws IOException {
        for (EntityModel entityModel : entityModels) {
            String packageName = BASE_PACKAGE + (namespace.isEmpty() ? "" : '.' + namespace);
            String modelName = entityModel.getName();
            entityModel.setLinkedClassName(packageName + '.' + modelName);
            String yaml = metadataManager.generateYaml(entityModel);
            Path yamlDir = Paths.get(
                    "src/test/resources/models",
                    namespace);
            Files.createDirectories(yamlDir);
            Files.write(yamlDir.resolve(modelName + ".yml"), yaml.getBytes(StandardCharsets.UTF_8));

            String java = metadataManager.generateJava(entityModel, namespace, packageName);
            Path javaDir = Paths.get(
                    "src/test/java",
                    BASE_PACKAGE.replace('.', '/'),
                    namespace.replace('.', '/'));
            Files.createDirectories(javaDir);
            Files.write(javaDir.resolve(modelName + ".java"), java.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected EntityModelRef modelRef(EntityModel entityModel) {
        return new EntityModelRef(entityModel.getName(), metadataManager);
    }
}
