package com.iostate.orca.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CodeUtils {

    private CodeUtils() {}

    public static void writeJavaFile(Path sourceRoot, String fqcn, String code) throws IOException {
        Path path = sourceRoot.resolve(fqcn.replace('.', '/') + ".java");
        Files.createDirectories(path.getParent());
        Files.write(path, code.getBytes(StandardCharsets.UTF_8));
    }
}
