package de.rwth.i2.attestor.domain;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MappingOp<I, M extends AssignMapping<I>> implements Lattice<M> {
    private final Supplier<M> supplier;
    private final Lattice<I> latticeOp;

    public MappingOp(Supplier<M> supplier, Lattice<I> latticeOp) {
        this.supplier = supplier;
        this.latticeOp = latticeOp;
    }

    // Lattice operations
    @Override
    public M leastElement() {
        M result = supplier.get();
        for (Integer key : result.keySet()) {
            result.assign(key, latticeOp.leastElement());
        }

        return result;
    }

    @Override
    public M greatestElement() {
        M result = supplier.get();
        for (Integer key : result.keySet()) {
            result.assign(key, latticeOp.greatestElement());
        }

        return result;
    }

    @Override
    public M getLeastUpperBound(Set<M> elements) {
        if (elements.isEmpty()) {
            return greatestElement();
        }

        M result = supplier.get();
        for (Integer key : result.keySet()) {
            result.assign(key, latticeOp.getLeastUpperBound(
                    elements.stream()
                            .map(mapping -> mapping.get(key))
                            .collect(Collectors.toSet())
            ));
        }

        return result;
    }

    @Override
    public boolean isLessOrEqual(M m1, M m2) {
        if (!m1.keySet().equals(m2.keySet())) {
            throw new IllegalArgumentException("Key sets of assign mapping must be compatible.");
        }

        for (Integer key : m1.keySet()) {
            if (!latticeOp.isLessOrEqual(m1.get(key), m2.get(key))) {
                return false;
            }
        }

        return true;
    }
}
