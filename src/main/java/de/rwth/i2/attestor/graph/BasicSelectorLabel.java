package de.rwth.i2.attestor.graph;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A general implementation of selector labels that consists of a single label.
 * There exists exactly one object for every selector label for every Attestor instance.
 *
 * @author Christoph
 */
public class BasicSelectorLabel implements SelectorLabel {

    /**
     * The label of the selector label.
     */
    private final String label;

    /**
     * Creates a selector label.
     *
     * @param label The name of the label.
     */
    private BasicSelectorLabel(String label) {

        this.label = label;
    }

    @Override
    public int compareTo(SelectorLabel other) {

        return this.toString().compareTo(other.toString());
    }

    public String toString() {

        return label;
    }

    @Override
    public boolean hasLabel(String label) {

        return this.label.equals(label);
    }

    @Override
    public String getLabel() {

        return label;
    }

    public static class Factory {

        private final Map<String, SelectorLabel> knownSelectorLabels = new LinkedHashMap<>();

        public SelectorLabel get(String name) {

            SelectorLabel result = knownSelectorLabels.computeIfAbsent(name, BasicSelectorLabel::new);
            return result;
        }

        public Collection<SelectorLabel> getAllAvailableSelectors() {

            return knownSelectorLabels.values();
        }
    }

}
