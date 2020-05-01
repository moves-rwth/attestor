package de.rwth.i2.attestor.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AssignMapping<S, I> implements Function<S, I> {

    private final AssignMapping<S, I> trace;
    private final Map<S, I> fragment;

    public AssignMapping() {
        trace = null;
        fragment = new HashMap<>();
    }

    public AssignMapping(AssignMapping<S, I> trace, Map<S, I> fragment) {
        this.trace = trace;
        this.fragment = fragment;
    }

    // Function operations
    @Override
    public I apply(S s) {
        I result = fragment.get(s);

        if (result != null) {
            return result;
        } else {
            if (trace != null) {
                return trace.apply(s);
            } else {
                throw new IllegalArgumentException("specified element has no image");
            }
        }
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
        public boolean isLessOrEqual(AssignMapping<S, I> e1, AssignMapping<S, I> e2) {
            // TODO(mkh)
            throw new UnsupportedOperationException();
        }
    }
}
