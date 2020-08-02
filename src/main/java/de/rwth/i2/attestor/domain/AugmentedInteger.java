package de.rwth.i2.attestor.domain;

import javax.annotation.Nonnull;

public class AugmentedInteger implements Comparable<AugmentedInteger> {
    private final boolean positive;
    private final boolean infinite;
    private final int value;

    public AugmentedInteger(boolean positiveInfinity) {
        this.value = 0;
        this.infinite = true;
        this.positive = positiveInfinity;
    }

    public AugmentedInteger(int value) {
        this.value = value;
        this.infinite = false;
        this.positive = value > 0;
    }

    public boolean isPositive() {
        return positive;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public int getValue() {
        if (infinite) {
            throw new UnsupportedOperationException("Infinity is not a number");
        }

        return value;
    }


    @Override
    public int compareTo(@Nonnull AugmentedInteger other) {
        if (equals(other)) {
            return 0;
        }

        if (this.infinite) {
            return this.positive ? 1 : -1;
        } else if (other.infinite) {
            return other.positive ? -1 : 1;
        } else {
            return Integer.compare(this.value, other.value);
        }
    }

    @Override
    public boolean equals(Object otherObject) {
        if (!(otherObject instanceof AugmentedInteger)) {
            return false;
        } else {
            AugmentedInteger other = (AugmentedInteger) otherObject;
            if (this.infinite && other.infinite) {
                return this.positive == other.positive;
            } else if (this.infinite || other.infinite) {
                return false;
            } else {
                return this.value == other.value;
            }
        }
    }

    @Override
    public String toString() {
        if (infinite) {
            return (positive ? "+" : "-") + "inf";
        }

        return Integer.toString(value);
    }
}
