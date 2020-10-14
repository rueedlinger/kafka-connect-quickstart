package ch.yax.connect.quickstart.common;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class ConnectMetadataUtil {
    private static final String PATH_PROPERTY_FILE = "/yax-connect.properties";
    private static final String UNKNOWN = "unknown";

    private static final String VERSION_CONFIG_KEY = "version";
    private static final String FINAL_NAME_CONFIG_KEY = "finalName";
    private static final String ARTIFACT_ID_KEY = "artifactId";
    private static final Map<String, String> metadata = new HashMap<>();
    private static String version;
    private static String artifactId;
    private static String finalName;

    static {
        try (final InputStream stream = ConnectMetadataUtil.class.getResourceAsStream(PATH_PROPERTY_FILE)) {
            final Properties props = new Properties();
            props.load(stream);
            version = props.getProperty(VERSION_CONFIG_KEY, UNKNOWN).trim();
            artifactId = props.getProperty(ARTIFACT_ID_KEY, UNKNOWN).trim();
            finalName = props.getProperty(FINAL_NAME_CONFIG_KEY, UNKNOWN).trim();

            metadata.putAll(props.entrySet().stream().collect(
                    Collectors.toMap(
                            e -> e.getKey().toString(),
                            e -> e.getValue().toString()
                    )
            ));

        } catch (final Exception e) {
            log.warn("Error while loading plugin metadata:", e);
        }
    }

    public static String getVersion() {
        return version;
    }

    public static String getFinalName() {
        return finalName;
    }

    public static String getArtifactId() {
        return artifactId;
    }

    public static Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

}
