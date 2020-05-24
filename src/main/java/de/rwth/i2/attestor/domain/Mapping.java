package de.rwth.i2.attestor.domain;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Mapping<S, I> implements Function<S, I>, Iterable<S> {

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

    public static class MappingSet<S, I> implements Lattice<Mapping<S, I>> {
        private final Lattice<I> latticeOp;

        public MappingSet(Lattice<I> latticeOp) {
            this.latticeOp = latticeOp;
        }

        // Lattice operations
        @Override
        public Mapping<S, I> leastElement() {
            return new Mapping<S, I>() {
                @Override
                public I apply(S s) {
                    return latticeOp.leastElement();
                }
            };
        }

        @Override
        public Mapping<S, I> greatestElement() {
            return new Mapping<S, I>() {
                @Override
                public I apply(S s) {
                    return latticeOp.greatestElement();
                }
            };
        }

        @Override
        public Mapping<S, I> getLeastUpperBound(Set<Mapping<S, I>> elements) {
            return new Mapping<S, I>() {
                @Override
                public I apply(S s) {
                    return latticeOp.getLeastUpperBound(
                            elements.stream()
                                    .map(mapping -> apply(s))
                                    .collect(Collectors.toSet())
                    );
                }
            };
        }

        @Override
        public boolean isLessOrEqual(Mapping<S, I> m1, Mapping<S, I> m2) {
            while (m1.iterator().hasNext()) {
                S s1 = m1.iterator().next();

                if (!latticeOp.isLessOrEqual(m1.apply(s1), m2.apply(s1))) {
                    return false;
                }
            }

            return true;
        }
    }
}
