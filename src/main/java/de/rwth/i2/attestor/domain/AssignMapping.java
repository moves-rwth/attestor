package de.rwth.i2.attestor.domain;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AssignMapping<S, I> implements Function<S, I>, Iterable<I> {

    public static class AssignMappingSet<S, I> implements Lattice<AssignMapping<S, I>> {
        private final Lattice<I> targetSet;
        private final Iterator<I> dummyIterator = new Iterator<I>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public I next() {
                return null;
            }
        };

        public AssignMappingSet(Lattice<I> targetSet) {
            this.targetSet = targetSet;
        }


        // Lattice operations
        @Override
        public AssignMapping<S, I> getLeastElement() {
            return new AssignMapping<S, I>() {
                @Override
                public Iterator<I> iterator() {
                    return dummyIterator;
                }

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
                public Iterator<I> iterator() {
                    return dummyIterator;
                }

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
        public boolean isLessOrEqual(AssignMapping<S, I> e1, AssignMapping<S, I> e2) {
            // TODO(mkh)
            throw new UnsupportedOperationException();
        }
    }
}
