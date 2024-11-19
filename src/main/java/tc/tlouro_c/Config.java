package tc.tlouro_c;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static Properties properties;

    public static Properties getConfig() {

        if (properties == null) {
            properties = loadConfig();
        }
        return properties;
    }

    private static Properties loadConfig() {
        Properties properties = new Properties();
        try (var input = Config.class.getResourceAsStream("/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
            System.exit(1);
        }
        return properties;
    }
}
