package ch.yax.connect.quickstart.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.rest.ConnectRestExtension;
import org.apache.kafka.connect.rest.ConnectRestExtensionContext;

import java.util.Map;

import static ch.yax.connect.quickstart.common.ConnectMetadataUtil.getVersion;

@Slf4j
public class HealthExtension implements ConnectRestExtension {

    private Map<String, ?> configs;

    @Override
    public void register(final ConnectRestExtensionContext connectRestExtensionContext) {
        log.info("register context {}", connectRestExtensionContext);
        connectRestExtensionContext.configurable()
                .register(new HealthCheckResource(configs, connectRestExtensionContext.clusterState()));
    }

    @Override
    public void close() {
        log.info("closing");
    }

    @Override
    public void configure(final Map<String, ?> configs) {
        log.info("configure extension with configs {}", configs);
        this.configs = configs;
    }

    @Override
    public String version() {
        return getVersion();
    }
}
