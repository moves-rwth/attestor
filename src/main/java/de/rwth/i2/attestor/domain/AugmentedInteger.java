package de.rwth.i2.attestor.domain;

import javax.annotation.Nonnull;

public class AugmentedInteger implements Comparable<AugmentedInteger> {
    public static AugmentedInteger POSITIVE_INFINITY = new AugmentedInteger(1);
    public static AugmentedInteger NEGATIVE_INFINITY = new AugmentedInteger(-1);

    private final int value;

    public AugmentedInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        if (this.equals(POSITIVE_INFINITY) || this.equals(NEGATIVE_INFINITY)) {
            throw new UnsupportedOperationException("Infinity is not a number");
        }

        return value;
    }

    @Override
    public int compareTo(@Nonnull AugmentedInteger other) {
        if (this.equals(other)) {
            return 0;
        }

        if (this.equals(POSITIVE_INFINITY)) {
            return 1;
        }

        if (this.equals(NEGATIVE_INFINITY)) {
            return other.equals(POSITIVE_INFINITY) ? 1 : -1;
        }

        return Integer.compare(this.value, other.value);
    }

    @Override
    public boolean equals(Object other) {
        if (!other.getClass().equals(AugmentedInteger.class)) {
            return false;
        }

        if (this == POSITIVE_INFINITY || other == POSITIVE_INFINITY ||
                this == NEGATIVE_INFINITY || other == NEGATIVE_INFINITY) {
            return this == other;
        }

        return this.value == ((AugmentedInteger) other).value;
    }

    @Override
    public String toString() {
        if (this.equals(POSITIVE_INFINITY)) {
            return "+inf";
        }

        if (this.equals(NEGATIVE_INFINITY)) {
            return "-inf";
        }

        return Integer.toString(value);
    }
}
