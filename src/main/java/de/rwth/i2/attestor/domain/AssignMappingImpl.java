package de.rwth.i2.attestor.domain;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AssignMappingImpl<S, I> extends AssignMapping<S, I> {
    private final Map<S, I> backend = new HashMap<>();

    public AssignMappingImpl(AssignMapping<S, I> assignMapping) {
        for (S key : assignMapping) {
            backend.put(key, assignMapping.apply(key));
        }
    }

    public I set(S key, I value) {
        return backend.put(key, value);
    }

    @Override
    public I apply(S s) {
        return backend.get(s);
    }

    @Nonnull
    @Override
    public Iterator<S> iterator() {
        return backend.keySet().iterator();
    }
}
