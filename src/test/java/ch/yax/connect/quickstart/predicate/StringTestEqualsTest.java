package ch.yax.connect.quickstart.predicate;

import ch.yax.connect.quickstart.predicates.StringTestEquals;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StringTestEqualsTest {

    @Test
    void testNullAndEmpty() {
        final StringTestEquals compareValue = new StringTestEquals("expectedValue", false);
        assertThat(compareValue.test(null)).isFalse();
        assertThat(compareValue.test("")).isFalse();
    }

    @Test
    void testEquals() {
        final String expectedValue = "foo";
        final boolean ignoreCase = false;
        final StringTestEquals compareValue = new StringTestEquals(expectedValue, ignoreCase);

        assertThat(compareValue.test(expectedValue)).isTrue();
        assertThat(compareValue.test(expectedValue.toUpperCase())).isFalse();

    }

    @Test
    void testEqualsIgnoreCase() {
        final String expectedValue = "foo";
        final boolean ignoreCase = true;
        final StringTestEquals compareValue = new StringTestEquals(expectedValue, ignoreCase);

        assertThat(compareValue.test(expectedValue)).isTrue();
        assertThat(compareValue.test(expectedValue.toUpperCase())).isTrue();

    }

    @Test
    void testEqualsObject() {
        final boolean ignoreCase = false;

        StringTestEquals compareValue = new StringTestEquals("123", ignoreCase);
        assertThat(compareValue.test(123)).isTrue();

        compareValue = new StringTestEquals("123.4", ignoreCase);
        assertThat(compareValue.test(123.4)).isTrue();

        compareValue = new StringTestEquals("[1, 2, 3]", ignoreCase);
        assertThat(compareValue.test(List.of(1, 2, 3))).isTrue();

    }

}
