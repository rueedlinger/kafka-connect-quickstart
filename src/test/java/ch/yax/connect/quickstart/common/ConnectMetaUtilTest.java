package ch.yax.connect.quickstart.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectMetaUtilTest {

    @Test
    void test() {
        assertThat(ConnectMetadataUtil.getArtifactId()).isNotEmpty();
        assertThat(ConnectMetadataUtil.getVersion()).isNotEmpty();
        assertThat(ConnectMetadataUtil.getFinalName()).isNotEmpty();
        assertThat(ConnectMetadataUtil.getMetadata()).hasSizeGreaterThan(0);
    }

}
