package ch.yax.connect.quickstart.predicates;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringTestEquals {

    final String expectedValue;
    final boolean ignoreCase;

    public StringTestEquals(final String expectedValue, final boolean ignoreCase) {
        this.expectedValue = expectedValue;
        this.ignoreCase = ignoreCase;
    }

    public boolean test(final Object value) {
        if (value == null) {
            return false;
        }

        final String valueAsString = String.valueOf(value);
        log.debug("compare valueAsString={}, expected={}, ignoreCase={}", valueAsString, expectedValue, ignoreCase);

        if (ignoreCase) {
            return valueAsString.equalsIgnoreCase(expectedValue);
        } else {
            return valueAsString.equals(expectedValue);
        }
    }
}
