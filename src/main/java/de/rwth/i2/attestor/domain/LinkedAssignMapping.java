package de.rwth.i2.attestor.domain;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;

public class LinkedAssignMapping<S, I> extends AssignMapping<S, I> {
    private final AssignMapping<S, I> parent;
    private final Map<S, I> fragment;
    private final Iterator<S> iterator = new LinkedAssignMappingIterator();

    public LinkedAssignMapping(AssignMapping<S, I> parent, Map<S, I> fragment) {
        this.parent = parent;
        this.fragment = fragment;
    }

    // Function operations
    @Override
    public I apply(S s) {
        if (fragment.containsKey(s)) {
            return fragment.get(s);
        } else {
            if (parent != null) {
                return parent.apply(s);
            } else {
                throw new IllegalArgumentException("specified element has no image");
            }
        }
    }

    // Iterable operations
    @Override
    @Nonnull
    public Iterator<S> iterator() {
        return iterator;
    }

    // Iterator
    class LinkedAssignMappingIterator implements Iterator<S> {
        Iterator<S> parentIterator = parent != null ? parent.iterator() : null;
        Iterator<S> fragmentIterator = fragment.keySet().iterator();

        @Override
        public boolean hasNext() {
            return fragmentIterator.hasNext() || (parentIterator != null && parentIterator.hasNext());
        }

        @Override
        public S next() {
            if (fragmentIterator.hasNext()) {
                return fragmentIterator.next();
            } else {
                if (parentIterator != null && parentIterator.hasNext()) {
                    return parentIterator.next();
                }
            }

            throw new IllegalStateException("AssignMapping.iterator has no next");
        }
    }
}
