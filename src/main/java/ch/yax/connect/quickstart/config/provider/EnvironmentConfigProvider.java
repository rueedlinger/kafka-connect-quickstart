package ch.yax.connect.quickstart.config.provider;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigData;
import org.apache.kafka.common.config.provider.ConfigProvider;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class EnvironmentConfigProvider implements ConfigProvider {

    private static final String BLACKLIST = "blacklist";
    private static final String REGEX_SPLIT = ",";
    private final Map<String, String> envs;
    private Set<String> blackliste = new TreeSet<>();

    // for testing
    EnvironmentConfigProvider(final Map<String, String> envs) {
        this.envs = envs;
    }

    public EnvironmentConfigProvider() {
        this(System.getenv());
    }


    @Override
    public ConfigData get(final String path) {
        log.debug("get config for path {}", path);
        return new ConfigData(getFilteredEnvs());
    }


    @Override
    public ConfigData get(final String path, final Set<String> keys) {
        log.debug("get config for path {} and keys {}", path, keys);
        return new ConfigData(
                getFilteredEnvs()
                        .entrySet()
                        .stream().filter(map -> keys.contains(map.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Override
    public void close() {
        log.debug("close");
    }

    @Override
    public void configure(final Map<String, ?> configs) {
        log.debug("configure with configs {}", configs);
        blackliste = getBlacklistFromConfig(configs);
    }

    private Map<String, String> getFilteredEnvs() {
        return envs.entrySet()
                .stream()
                .filter(map -> !blackliste.contains(map.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Set<String> getBlacklistFromConfig(final Map<String, ?> configs) {
        final Set<String> blacklist = new TreeSet<>();

        final Object cfg = configs.get(BLACKLIST);

        if (cfg != null) {
            final List<String> values = Arrays.asList(cfg.toString().split(REGEX_SPLIT));
            blacklist.addAll(values.stream()
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }

        return blacklist;
    }
}
