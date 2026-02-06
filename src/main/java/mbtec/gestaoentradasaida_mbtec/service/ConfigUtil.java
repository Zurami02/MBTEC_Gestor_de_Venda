package mbtec.gestaoentradasaida_mbtec.service;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class ConfigUtil {

    private static final Properties props = new Properties();
    private static final Path path = Paths.get(
            System.getProperty("user.home"), ".mbtec", "config.properties"
    );

    static {
        // Carrega ao iniciar
        try {
            if (Files.exists(path)) {
                try (InputStream in = Files.newInputStream(path)) {
                    props.load(in);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void set(String key, String value) {
        props.setProperty(key, value);
        try {
            Files.createDirectories(path.getParent());
            try (OutputStream out = Files.newOutputStream(path)) {
                props.store(out, "Configurações Mbtec");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
