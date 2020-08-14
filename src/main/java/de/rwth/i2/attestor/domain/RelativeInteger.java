package de.rwth.i2.attestor.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class RelativeInteger extends RelativeIndex<AugmentedInteger> {
    public static final RelativeIntegerOp opSet = new RelativeIntegerOp();

    private RelativeInteger(AugmentedInteger concrete, Set<Integer> variables) {
        super(concrete, variables);
    }

    public static RelativeInteger get(int value) {
        return opSet.getFromConcrete(new AugmentedInteger(value));
    }

    public static final class RelativeIntegerOp extends RelativeIndexOp<AugmentedInteger, RelativeInteger> {
        private static final Map<Integer, RelativeInteger> invertedVariables = new HashMap<>();

        private static final RelativeIndexSupplier<AugmentedInteger, RelativeInteger>
                supplier = (value, variables) -> new RelativeInteger(value, filterVariables(variables));

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
                return elements.stream().max(AugmentedInteger::compareTo).orElseGet(this::leastElement);
            }
        };

        private static final AddMonoid<AugmentedInteger> monoidOp = new AddMonoid<AugmentedInteger>() {
            @Override
            public AugmentedInteger identity() {
                return new AugmentedInteger(0);
            }

            @Override
            public AugmentedInteger operate(AugmentedInteger e1, AugmentedInteger e2) {
                if (e1.isInfinite() || e2.isInfinite()) {
                    return new AugmentedInteger((e1.isInfinite() && e1.isPositive()) || (e2.isInfinite() && e2.isPositive()));
                } else {
                    return new AugmentedInteger(e1.getValue() + e2.getValue());
                }
            }
        };

        private RelativeIntegerOp() {
            super(supplier, latticeOp, monoidOp);
        }

        private static Set<Integer> filterVariables(Set<Integer> variables) {
            Set<Integer> toBeRemoved = new HashSet<>();
            variables.forEach(id -> {
                RelativeInteger inverse = invertedVariables.get(id);
                if (inverse != null) {
                    toBeRemoved.add(id);
                    toBeRemoved.add(inverse.getVariables().iterator().next());
                }
            });

            return variables.stream().filter(v -> !toBeRemoved.contains(v)).collect(Collectors.toSet());
        }

        public RelativeInteger invert(RelativeInteger toInvert) {
            AugmentedInteger newConcrete;
            if (toInvert.getConcrete() != null) {
                AugmentedInteger concrete = toInvert.getConcrete();

                if (concrete.isInfinite()) {
                    newConcrete = new AugmentedInteger(!concrete.isPositive());
                } else {
                    newConcrete = new AugmentedInteger(-1 * concrete.getValue());
                }
            } else {
                newConcrete = null;
            }

            RelativeInteger result = getFromConcrete(newConcrete);
            for (Integer id : toInvert.getVariables()) {
                invertedVariables.putIfAbsent(id, getVariable());
                result = add(result, invertedVariables.get(id));
            }

            return result;
        }

        public RelativeInteger subtract(RelativeInteger i1, RelativeInteger i2) {
            return add(i1, invert(i2));
        }
    }
}
