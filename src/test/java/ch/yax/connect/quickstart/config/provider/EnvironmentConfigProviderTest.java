package ch.yax.connect.quickstart.config.provider;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvironmentConfigProviderTest {

    private static final String CFG_BLACKLIST = "blacklist";

    private final Map<String, ?> CONFIG = Map.of(CFG_BLACKLIST, "foo, bar,baz");
    private final Map<String, String> ENV = Map.of(
            "foo", "1",
            "bar", "2",
            "baz", "3",
            "key1", "4",
            "key2", "5");

    @Test
    void testGet() {
        final EnvironmentConfigProvider configProvider = new EnvironmentConfigProvider(ENV);
        assertThat(configProvider.get(null).data()).hasSize(ENV.size());
        configProvider.configure(CONFIG);
        assertThat(configProvider.get(null).data()).hasSize(2);
    }

    @Test
    void testGetWithKeys() {
        final EnvironmentConfigProvider configProvider = new EnvironmentConfigProvider(ENV);
        assertThat(configProvider.get(null, ENV.keySet()).data()).hasSize(ENV.size());

        configProvider.configure(CONFIG);
        assertThat(configProvider.get(null, ENV.keySet()).data()).hasSize(2);
        assertThat(configProvider.get(null, Set.of("key2")).data()).hasSize(1);
    }

    @Test
    void testBlacklist() {
        final EnvironmentConfigProvider configProvider = new EnvironmentConfigProvider(ENV);

        configProvider.configure(Map.of(CFG_BLACKLIST, " baz, foo ,bar "));
        assertThat(configProvider.get(null, ENV.keySet()).data()).hasSize(2);

        configProvider.configure(Map.of(CFG_BLACKLIST, ",baz,"));
        assertThat(configProvider.get(null, ENV.keySet()).data()).hasSize(4);

        configProvider.configure(Map.of(CFG_BLACKLIST, "baz"));
        assertThat(configProvider.get(null, ENV.keySet()).data()).hasSize(4);
    }

}
