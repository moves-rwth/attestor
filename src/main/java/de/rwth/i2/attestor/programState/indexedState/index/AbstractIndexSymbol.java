package de.rwth.i2.attestor.programState.indexedState.index;

import java.util.LinkedHashMap;
import java.util.Map;

public class AbstractIndexSymbol implements IndexSymbol {

    private static final Map<String, AbstractIndexSymbol> existingSymbols = new LinkedHashMap<>();
    private final String label;

    private AbstractIndexSymbol(String label) {

        super();
        this.label = label;
    }

    public static synchronized AbstractIndexSymbol get(String label) {

        if (!existingSymbols.containsKey(label)) {
            existingSymbols.put(label, new AbstractIndexSymbol(label));
        }
        return existingSymbols.get(label);
    }

    @Override
    public boolean isBottom() {

        return false;
    }

    public boolean equals(Object other) {

        return this == other;

    }

    public int hashCode() {

        return label.hashCode();
    }

    public String toString() {

        return this.label;
    }
}
