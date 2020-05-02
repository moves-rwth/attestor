package de.rwth.i2.attestor.domain;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AssignMapping<S, I> implements Function<S, I>, Iterable<S> {

    @Override
    @Nonnull
    public Iterator<S> iterator() {
        return new Iterator<S>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public S next() {
                return null;
            }
        };
    }

    public static class AssignMappingSet<S, I> implements Lattice<AssignMapping<S, I>> {
        private final Lattice<I> targetSet;

        public AssignMappingSet(Lattice<I> targetSet) {
            this.targetSet = targetSet;
        }

        // Lattice operations
        @Override
        public AssignMapping<S, I> getLeastElement() {
            return new AssignMapping<S, I>() {
                @Override
                public I apply(S s) {
                    return targetSet.getLeastElement();
                }
            };
        }

        @Override
        public AssignMapping<S, I> getLeastUpperBound(Set<AssignMapping<S, I>> elements) {
            return new AssignMapping<S, I>() {
                @Override
                public I apply(S s) {
                    return targetSet.getLeastUpperBound(
                            elements.stream()
                                    .map(mapping -> apply(s))
                                    .collect(Collectors.toSet())
                    );
                }
            };
        }

        @Override
        public boolean isLessOrEqual(AssignMapping<S, I> m1, AssignMapping<S, I> m2) {
            while (m1.iterator().hasNext()) {
                S s1 = m1.iterator().next();

                if (!targetSet.isLessOrEqual(m1.apply(s1), m2.apply(s1))) {
                    return false;
                }
            }

            return true;
        }
    }
}
