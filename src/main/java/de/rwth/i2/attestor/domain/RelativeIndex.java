package de.rwth.i2.attestor.domain;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RelativeIndex<T> {
    private static final TIntList reservedVariables = new TIntLinkedList();

    final T concrete;
    final TIntSet variables = new TIntHashSet();

    public RelativeIndex(T concrete) {
        this.concrete = concrete;
    }

    public RelativeIndex() {
        this.concrete = null;
        int id = reservedVariables.max() + 1;
        reservedVariables.add(id);
        variables.add(id);
    }

    RelativeIndex(T concrete, TIntSet variables) {
        this.concrete = concrete;
        if (reservedVariables.containsAll(variables)) {
            variables.addAll(variables);
        } else {
            throw new IllegalArgumentException("Constructing a relative index using unreserved variables is not allowed");
        }
    }

    public boolean isConcrete() {
        return variables.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RelativeIndex)) {
            return false;
        }

        RelativeIndex<?> other = (RelativeIndex<?>) obj;

        return this.concrete.equals(other.concrete) && this.variables.equals(other.variables);
    }

    public static class RelativeIndexSet<T> implements Lattice<RelativeIndex<T>>, AddMonoid<RelativeIndex<T>> {
        private final Lattice<T> latticeOp;
        private final AddMonoid<T> monoidOp;

        public RelativeIndexSet(Lattice<T> latticeOp, AddMonoid<T> monoidOp) {
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

            return elements.stream().reduce(identity(), (i1, i2) -> {

                T concrete = latticeOp.getLeastUpperBound(Stream.of(
                        i1.concrete != null ? i1.concrete : monoidOp.identity(),
                        i2.concrete != null ? i2.concrete : monoidOp.identity()
                ).collect(Collectors.toSet()));

                TIntSet variables = new TIntHashSet(i1.variables);
                variables.addAll(i2.variables);

                return new RelativeIndex<>(concrete, variables);
            });
        }

        @Override
        public boolean isLessOrEqual(RelativeIndex<T> e1, RelativeIndex<T> e2) {
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
                    e1.concrete != null ? e1.concrete : monoidOp.identity()
            );

            TIntSet variables = new TIntHashSet(e1.variables);
            variables.addAll(e2.variables);

            return new RelativeIndex<>(concrete, variables);
        }
    }
}
