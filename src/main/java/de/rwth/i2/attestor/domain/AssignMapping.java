package de.rwth.i2.attestor.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AssignMapping<S, I> {
    private final Map<S, I> backend = new HashMap<>();

    public AssignMapping() {
    }

    public AssignMapping(AssignMapping<S, I> mapping) {
        backend.putAll(mapping.backend);
    }

    public I get(S key) {
        if (!backend.containsKey(key)) {
            throw new IllegalArgumentException("Unknown mapping key");
        }

        return backend.get(key);
    }

    public boolean contains(S key) {
        return backend.containsKey(key);
    }


    public void assign(S key, I value) {
        if (value == null) {
            backend.remove(key);
            return;
        }

        if (contains(key)) {
            backend.replace(key, value);
        } else {
            backend.put(key, value);
        }
    }


    public static class MappingSet<S, I> implements Lattice<AssignMapping<S, I>> {
        private final Set<S> keySet;
        private final Lattice<I> latticeOp;

        public MappingSet(Set<S> keySet, Lattice<I> latticeOp) {
            this.keySet = keySet;
            this.latticeOp = latticeOp;
        }

        // Lattice operations
        @Override
        public AssignMapping<S, I> leastElement() {
            AssignMapping<S, I> result = new AssignMapping<>();

            for (S key : keySet) {
                result.assign(key, latticeOp.leastElement());
            }

            return result;
        }

        @Override
        public AssignMapping<S, I> greatestElement() {
            AssignMapping<S, I> result = new AssignMapping<>();

            for (S key : keySet) {
                result.assign(key, latticeOp.greatestElement());
            }

            return result;
        }

        @Override
        public AssignMapping<S, I> getLeastUpperBound(Set<AssignMapping<S, I>> elements) {
            if (elements.isEmpty()) {
                return greatestElement();
            }

            AssignMapping<S, I> result = new AssignMapping<>();

            for (S key : keySet) {
                result.assign(key, latticeOp.getLeastUpperBound(
                        elements.stream()
                                .map(mapping -> mapping.get(key))
                                .collect(Collectors.toSet())
                ));
            }

            return result;
        }

        @Override
        public boolean isLessOrEqual(AssignMapping<S, I> m1, AssignMapping<S, I> m2) {
            for (S key : m1.backend.keySet()) {
                if (!latticeOp.isLessOrEqual(m1.get(key), m2.get(key))) {
                    return false;
                }
            }

            for (S key : m2.backend.keySet()) {
                if (!latticeOp.isLessOrEqual(m1.get(key), m2.get(key))) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Mapping{");
        for (S key : backend.keySet()) {
            sb.append(" ");
            sb.append(key);
            sb.append("->");
            sb.append(get(key));
        }
        sb.append("}");

        return sb.toString();
    }
}
