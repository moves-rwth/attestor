package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.types.Type;

import java.util.LinkedHashMap;
import java.util.Map;

class MockupType implements Type {

    @Override
    public boolean hasSelectorLabel(SelectorLabel selectorLabel) {

        return false;
    }

    @Override
    public void addSelectorLabel(SelectorLabel selectorLabel, String defaultValue) {

    }

    @Override
    public Map<SelectorLabel, String> getSelectorLabels() {

        return new LinkedHashMap<>();
    }

    @Override
    public boolean isOptional(SelectorLabel selectorLabel) {

        return false;
    }

    @Override
    public boolean isPrimitiveType() {

        return false;
    }
}
