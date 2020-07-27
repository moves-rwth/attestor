package de.rwth.i2.attestor.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RelativeIndexOp<T, I extends RelativeIndex<T>> implements Lattice<I>, AddMonoid<I> {
    private final Lattice<T> latticeOp;
    private final AddMonoid<T> monoidOp;
    private final RelativeIndexSupplier<T, I> supplier;

    public RelativeIndexOp(RelativeIndexSupplier<T, I> supplier, Lattice<T> latticeOp, AddMonoid<T> monoidOp) {
        this.supplier = supplier;
        this.latticeOp = latticeOp;
        this.monoidOp = monoidOp;
    }

    public I getConcrete(T concrete) {
        return supplier.get(concrete);
    }

    public I getVariable() {
        return supplier.get();
    }

    // Lattice operations
    @Override
    public I leastElement() {
        return supplier.get(latticeOp.leastElement());
    }

    @Override
    public I greatestElement() {
        return supplier.get(latticeOp.greatestElement());
    }

    @Override
    public I getLeastUpperBound(Set<I> elements) {
        if (elements.isEmpty()) {
            return greatestElement();
        }

        return elements.stream().reduce(leastElement(), (i1, i2) -> {

            T concrete = latticeOp.getLeastUpperBound(Stream.of(
                    i1.getConcrete() != null ? i1.getConcrete() : latticeOp.leastElement(),
                    i2.getConcrete() != null ? i2.getConcrete() : latticeOp.leastElement()
            ).collect(Collectors.toSet()));

            Set<Integer> variables = new HashSet<>(i1.getVariables());
            variables.addAll(i2.getVariables());

            return supplier.get(concrete, variables);
        });
    }

    @Override
    public boolean isLessOrEqual(I e1, I e2) {
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

        if (e2.getVariables().containsAll(e1.getVariables())) {
            if (e1.isConcrete() && e2.isConcrete()) {
                return latticeOp.isLessOrEqual(e1.getConcrete(), e2.getConcrete());
            }

            return !e1.isConcrete() && !e2.isConcrete();
        }

        return false;
    }

    // Monoid operations
    @Override
    public I identity() {
        return supplier.get(monoidOp.identity());
    }

    @Override
    public I operate(I e1, I e2) {
        if (e1.equals(latticeOp.greatestElement()) || e2.equals(latticeOp.greatestElement())) {
            return greatestElement();
        } else if (e1.equals(latticeOp.leastElement()) || e2.equals(latticeOp.leastElement())) {
            return leastElement();
        }

        T concrete = monoidOp.add(
                e1.getConcrete() != null ? e1.getConcrete() : monoidOp.identity(),
                e2.getConcrete() != null ? e2.getConcrete() : monoidOp.identity()
        );

        Set<Integer> variables = new HashSet<>(e1.getVariables());
        variables.addAll(e2.getVariables());

        return supplier.get(concrete, variables);
    }
}
