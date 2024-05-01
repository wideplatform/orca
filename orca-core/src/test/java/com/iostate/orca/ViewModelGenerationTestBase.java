package com.iostate.orca;

import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.metadata.view.ViewModel;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ViewModelGenerationTestBase {

    private static final String BASE_PACKAGE = "com.iostate.orca";

    protected MetadataManager metadataManager;

    @BeforeEach
    public void setup() {
        metadataManager = new MetadataManager();
    }

    // namespace can be empty standing for the default namespace
    protected void exportCode(String namespace, ViewModel... viewModels) throws IOException {
        for (ViewModel viewModel : viewModels) {
            String packageName = BASE_PACKAGE + (namespace.isEmpty() ? "" : '.' + namespace);
            String modelName = viewModel.getName();
            viewModel.setLinkedClassName(packageName + '.' + modelName);
            String yaml = metadataManager.generateYaml(viewModel);
            Path yamlDir = Paths.get(
                    "src/test/resources/models",
                    namespace);
            Files.createDirectories(yamlDir);
            Files.write(yamlDir.resolve(modelName + ".yml"), yaml.getBytes(StandardCharsets.UTF_8));

            String java = metadataManager.generateJava(viewModel, namespace, packageName);
            Path javaDir = Paths.get(
                    "src/test/java",
                    BASE_PACKAGE.replace('.', '/'),
                    namespace.replace('.', '/'));
            Files.createDirectories(javaDir);
            Files.write(javaDir.resolve(modelName + ".java"), java.getBytes(StandardCharsets.UTF_8));
        }
    }
}
