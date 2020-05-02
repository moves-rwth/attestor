package de.rwth.i2.attestor.domain;

import java.util.Map;

public class AssignMappingImpl<S, I> extends AssignMapping<S, I> {
    private final Map<S, I> backend;

    public AssignMappingImpl(Map<S, I> backend) {
        this.backend = backend;
    }

    @Override
    public I apply(S s) {
        return backend.get(s);
    }
}
