package ch.yax.connect.quickstart.rest;

import org.apache.kafka.connect.rest.ConnectRestExtensionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Configurable;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HealthExtensionTest {

    @Mock
    private ConnectRestExtensionContext context;

    @Mock
    private Configurable configurable;

    @Test
    void testRegister() {

        when(context.configurable()).thenReturn(configurable);

        final HealthExtension extension = new HealthExtension();
        final Map config = Map.of("KEY", "VALUE");
        extension.configure(config);
        extension.register(context);

        verify(configurable).register(any(HealthCheckResource.class));

        extension.close();

    }

}
