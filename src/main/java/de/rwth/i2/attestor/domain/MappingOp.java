package de.rwth.i2.attestor.domain;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MappingOp<S, L, A extends Map<S, L>> implements Lattice<A> {
    private final Supplier<A> supplier;
    private final Set<S> keySet;
    private final Lattice<L> latticeOp;

    public MappingOp(Supplier<A> supplier, Set<S> keySet, Lattice<L> latticeOp) {
        this.supplier = supplier;
        this.keySet = keySet;
        this.latticeOp = latticeOp;
    }

    public static <S, L> void assign(Map<S, L> map, S key, L value) {
        if (value == null) {
            map.remove(key);
            return;
        }

        if (map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.put(key, value);
        }
    }

    public static <S, L> boolean contains(Map<S, L> map, S key) {
        return map.containsKey(key);
    }

    // Lattice operations
    @Override
    public A leastElement() {
        A result = supplier.get();

        for (S key : keySet) {
            assign(result, key, latticeOp.leastElement());
        }

        return result;
    }

    @Override
    public A greatestElement() {
        A result = supplier.get();

        for (S key : keySet) {
            assign(result, key, latticeOp.greatestElement());
        }

        return result;
    }

    @Override
    public A getLeastUpperBound(Set<A> elements) {
        if (elements.isEmpty()) {
            return greatestElement();
        }

        A result = supplier.get();

        for (S key : keySet) {
            assign(result, key, latticeOp.getLeastUpperBound(
                    elements.stream()
                            .map(mapping -> mapping.get(key))
                            .collect(Collectors.toSet())
            ));
        }

        return result;
    }

    @Override
    public boolean isLessOrEqual(A m1, A m2) {
        for (S key : m1.keySet()) {
            if (!latticeOp.isLessOrEqual(m1.get(key), m2.get(key))) {
                return false;
            }
        }

        for (S key : m2.keySet()) {
            if (!latticeOp.isLessOrEqual(m1.get(key), m2.get(key))) {
                return false;
            }
        }

        return true;
    }
}
