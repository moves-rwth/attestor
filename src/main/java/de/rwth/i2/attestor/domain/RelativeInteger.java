package de.rwth.i2.attestor.domain;

import java.util.Set;

final public class RelativeInteger {
    public static RelativeIndex<AugmentedInteger> get(int value) {
        return new RelativeIndex<>(new AugmentedInteger(value));
    }

    public static class RelativeIntegerSet extends RelativeIndex.RelativeIndexSet<AugmentedInteger> {
        private static final Lattice<AugmentedInteger> latticeOp = new Lattice<AugmentedInteger>() {
            @Override
            public boolean isLessOrEqual(AugmentedInteger e1, AugmentedInteger e2) {
                return e1.compareTo(e2) <= 0;
            }

            @Override
            public AugmentedInteger leastElement() {
                return new AugmentedInteger(false);
            }

            @Override
            public AugmentedInteger greatestElement() {
                return new AugmentedInteger(true);
            }

            @Override
            public AugmentedInteger getLeastUpperBound(Set<AugmentedInteger> elements) {
                return elements.stream().max(AugmentedInteger::compareTo).orElseGet(this::greatestElement);
            }
        };

        private static final AddMonoid<AugmentedInteger> monoidOp = new AddMonoid<AugmentedInteger>() {
            @Override
            public AugmentedInteger identity() {
                return new AugmentedInteger(0);
            }

            @Override
            public AugmentedInteger operate(AugmentedInteger e1, AugmentedInteger e2) {
                if (e1.infinite || e2.infinite) {
                    return new AugmentedInteger((e1.infinite && e1.positive) || (e2.infinite && e2.positive));
                } else {
                    return new AugmentedInteger(e1.value + e2.value);
                }
            }
        };

        public RelativeIntegerSet() {
            super(latticeOp, monoidOp);
        }
    }
}
