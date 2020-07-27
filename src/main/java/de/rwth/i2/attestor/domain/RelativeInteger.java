package de.rwth.i2.attestor.domain;

import java.util.Set;

final public class RelativeInteger extends RelativeIndex<AugmentedInteger> {
    private RelativeInteger() {
    }

    private RelativeInteger(AugmentedInteger concrete) {
        super(concrete);
    }

    private RelativeInteger(AugmentedInteger concrete, Set<Integer> variables) {
        super(concrete, variables);
    }

    private static final RelativeIndexSupplier<AugmentedInteger, RelativeInteger>
            supplier = new RelativeIndexSupplier<AugmentedInteger, RelativeInteger>() {

        @Override
        public RelativeInteger get() {
            return new RelativeInteger();
        }

        @Override
        public RelativeInteger get(AugmentedInteger value) {
            return new RelativeInteger(value);
        }

        @Override
        public RelativeInteger get(AugmentedInteger value, Set<Integer> variables) {
            return new RelativeInteger(value, variables);
        }
    };

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

    public static final RelativeIndexOp<AugmentedInteger, RelativeInteger> opSet = new RelativeIndexOp<>(supplier, latticeOp, monoidOp);

    public static RelativeInteger get(int value) {
        return opSet.getConcrete(new AugmentedInteger(value));
    }
}
