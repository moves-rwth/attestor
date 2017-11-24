package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.types.Type;

import java.util.HashMap;
import java.util.Map;

class MockupType implements Type {
    @Override
    public boolean hasSelectorLabel(String name) {
        return false;
    }

    @Override
    public void addSelectorLabel(String name, String defaultValue) {

    }

    @Override
    public Map<String, String> getSelectorLabels() {
        return new HashMap<>();
    }

    @Override
    public boolean isPrimitiveType(String name) {
        return false;
    }
}
