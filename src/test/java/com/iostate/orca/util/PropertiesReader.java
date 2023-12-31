package com.iostate.orca.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    public Properties read(String path) throws IOException {
        Properties prop = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            prop.load(in);
            return prop;
        }
    }
}
