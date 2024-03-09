package com.iostate.orca.metadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CodeUtils {

    private CodeUtils() {}

    public static void writeJavaFile(String sourceRoot, String fqcn, String code) throws IOException {
        Path path = Paths.get(sourceRoot, fqcn.replace('.', '/') + ".java");
        Files.createDirectories(path.getParent());
        Files.write(path, code.getBytes(StandardCharsets.UTF_8));
    }
}
