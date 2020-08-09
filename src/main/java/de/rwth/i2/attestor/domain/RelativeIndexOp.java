package de.rwth.i2.attestor.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RelativeIndexOp<T, I extends RelativeIndex<T>> implements Lattice<I>, AddMonoid<I> {
    private static int variableCounter = 0;

    private final Lattice<T> latticeOp;
    private final AddMonoid<T> monoidOp;
    private final RelativeIndexSupplier<T, I> supplier;

    public RelativeIndexOp(RelativeIndexSupplier<T, I> supplier, Lattice<T> latticeOp, AddMonoid<T> monoidOp) {
        this.supplier = supplier;
        this.latticeOp = latticeOp;
        this.monoidOp = monoidOp;
    }

    public I getFromConcrete(T concrete) {
        return supplier.get(concrete, Collections.emptySet());
    }

    public I getVariable() {
        int id = variableCounter;
        variableCounter++;
        return supplier.get(monoidOp.identity(), Collections.singleton(id));
    }

    // Lattice operations
    @Override
    public I leastElement() {
        return getFromConcrete(latticeOp.leastElement());
    }

    @Override
    public I greatestElement() {
        return getFromConcrete(latticeOp.greatestElement());
    }

    @Override
    public I getLeastUpperBound(Set<I> elements) {
        if (elements.isEmpty()) {
            return greatestElement();
        }

        return elements.stream().reduce(leastElement(), (i1, i2) -> {
            Set<T> s = new HashSet<>();
            s.add(i1.getConcrete());
            s.add(i2.getConcrete());
            T concrete = latticeOp.getLeastUpperBound(s);

            Set<Integer> variables = new HashSet<>(i1.getVariables());
            variables.addAll(i2.getVariables());

            return supplier.get(concrete, variables);
        });
    }

    @Override
    public boolean isLessOrEqual(I e1, I e2) {
        if (e2.getVariables().containsAll(e1.getVariables())) {
            return latticeOp.isLessOrEqual(e1.getConcrete(), e2.getConcrete());
        }

        return e2.equals(greatestElement());
    }

    // Monoid operations
    @Override
    public I identity() {
        return getFromConcrete(monoidOp.identity());
    }

    @Override
    public I operate(I e1, I e2) {
        if (e1.equals(latticeOp.greatestElement()) || e2.equals(latticeOp.greatestElement())) {
            return greatestElement();
        } else if (e1.equals(latticeOp.leastElement()) || e2.equals(latticeOp.leastElement())) {
            return leastElement();
        }

        T concrete = monoidOp.add(e1.getConcrete(), e2.getConcrete());
        Set<Integer> variables = new HashSet<>(e1.getVariables());
        variables.addAll(e2.getVariables());

        return supplier.get(concrete, variables);
    }
}
