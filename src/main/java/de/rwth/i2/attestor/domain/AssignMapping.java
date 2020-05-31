package de.rwth.i2.attestor.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AssignMapping<S, I> extends HashMap<S, I> {
    private final Set<S> keySet = new HashSet<>();

    public AssignMapping(Set<S> keySet) {
        this.keySet.addAll(keySet);
    }

    public AssignMapping(AssignMapping<S, I> mapping) {
        keySet.addAll(mapping.keySet);
        putAll(mapping);
    }

    public void assign(S key, I value) {
        keySet.add(key);
        put(key, value);
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
            AssignMapping<S, I> result = new AssignMapping<>(keySet);

            for (S key : keySet) {
                result.put(key, latticeOp.leastElement());
            }

            return result;
        }

        @Override
        public AssignMapping<S, I> greatestElement() {
            AssignMapping<S, I> result = new AssignMapping<>(keySet);

            for (S key : keySet) {
                result.put(key, latticeOp.greatestElement());
            }

            return result;
        }

        @Override
        public AssignMapping<S, I> getLeastUpperBound(Set<AssignMapping<S, I>> elements) {
            if (elements.isEmpty()) {
                return greatestElement();
            }

            AssignMapping<S, I> result = new AssignMapping<>(keySet);

            for (S key : keySet) {
                result.put(key, latticeOp.getLeastUpperBound(
                        elements.stream()
                                .map(mapping -> mapping.get(key))
                                .collect(Collectors.toSet())
                ));
            }

            return result;
        }

        @Override
        public boolean isLessOrEqual(AssignMapping<S, I> m1, AssignMapping<S, I> m2) {
            for (S key : m1.keySet) {
                if (!latticeOp.isLessOrEqual(m1.get(key), m2.get(key))) {
                    return false;
                }
            }

            for (S key : m2.keySet) {
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
        for (S key : this.keySet) {
            sb.append(" ");
            sb.append(key);
            sb.append("->");
            sb.append(get(key));
        }
        sb.append("}");

        return sb.toString();
    }
}
