package de.rwth.i2.attestor.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RelativeIndexOp<T> implements Lattice<RelativeIndex<T>>, AddMonoid<RelativeIndex<T>> {
    private final Lattice<T> latticeOp;
    private final AddMonoid<T> monoidOp;

    public RelativeIndexOp(Lattice<T> latticeOp, AddMonoid<T> monoidOp) {
        this.latticeOp = latticeOp;
        this.monoidOp = monoidOp;
    }

    // Lattice operations
    @Override
    public RelativeIndex<T> leastElement() {
        return new RelativeIndex<>(latticeOp.leastElement());
    }

    @Override
    public RelativeIndex<T> greatestElement() {
        return new RelativeIndex<>(latticeOp.greatestElement());
    }

    @Override
    public RelativeIndex<T> getLeastUpperBound(Set<RelativeIndex<T>> elements) {
        if (elements.isEmpty()) {
            return greatestElement();
        }

        return elements.stream().reduce(leastElement(), (i1, i2) -> {

            T concrete = latticeOp.getLeastUpperBound(Stream.of(
                    i1.concrete != null ? i1.concrete : latticeOp.leastElement(),
                    i2.concrete != null ? i2.concrete : latticeOp.leastElement()
            ).collect(Collectors.toSet()));

            Set<Integer> variables = new HashSet<>(i1.variables);
            variables.addAll(i2.variables);

            return new RelativeIndex<>(concrete, variables);
        });
    }

    @Override
    public boolean isLessOrEqual(RelativeIndex<T> e1, RelativeIndex<T> e2) {
        if (e1.equals(leastElement())) {
            return true;
        }

        if (e2.equals(leastElement())) {
            return e1.equals(leastElement());
        }

        if (e1.equals(greatestElement())) {
            return e2.equals(greatestElement());
        }

        if (e2.equals(greatestElement())) {
            return true;
        }

        if (e2.variables.containsAll(e1.variables)) {
            if (e1.isConcrete() && e2.isConcrete()) {
                return latticeOp.isLessOrEqual(e1.concrete, e2.concrete);
            }

            return !e1.isConcrete() && !e2.isConcrete();
        }

        return false;
    }

    // Monoid operations
    @Override
    public RelativeIndex<T> identity() {
        return new RelativeIndex<>(monoidOp.identity());
    }

    @Override
    public RelativeIndex<T> operate(RelativeIndex<T> e1, RelativeIndex<T> e2) {
        if (e1.equals(latticeOp.greatestElement()) || e2.equals(latticeOp.greatestElement())) {
            return greatestElement();
        } else if (e1.equals(latticeOp.leastElement()) || e2.equals(latticeOp.leastElement())) {
            return leastElement();
        }

        T concrete = monoidOp.add(
                e1.concrete != null ? e1.concrete : monoidOp.identity(),
                e2.concrete != null ? e2.concrete : monoidOp.identity()
        );

        Set<Integer> variables = new HashSet<>(e1.variables);
        variables.addAll(e2.variables);

        return new RelativeIndex<>(concrete, variables);
    }
}
