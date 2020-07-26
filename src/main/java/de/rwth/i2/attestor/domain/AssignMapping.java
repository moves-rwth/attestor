package de.rwth.i2.attestor.domain;

import java.util.HashMap;
import java.util.Map;

public class AssignMapping<S, I> {
    final Map<S, I> backend = new HashMap<>();

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
