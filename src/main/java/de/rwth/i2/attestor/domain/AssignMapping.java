package de.rwth.i2.attestor.domain;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AssignMapping<S, I> extends Mapping<S, I> {
    private final Map<S, I> backend = new HashMap<>();

    public AssignMapping(Mapping<S, I> assignMapping) {
        for (S key : assignMapping) {
            backend.put(key, assignMapping.apply(key));
        }
    }

    public void assign(S key, I value) {
        backend.put(key, value);
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
