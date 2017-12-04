package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.SelectorLabel;

public class MockupSelector implements SelectorLabel {

    private final String label;

    public MockupSelector(String label) {

        this.label = label;
    }

    @Override
    public int compareTo(SelectorLabel o) {

        if (o instanceof MockupSelector) {

            return label.compareTo(((MockupSelector) o).label);
        }

        return -1;
    }

    public boolean equals(Object o) {

        if (o instanceof MockupSelector) {

            MockupSelector m = (MockupSelector) o;
            return m.label.equals(label);
        }
        return false;
    }

    public int hashCode() {

        return label.hashCode();
    }

    @Override
    public boolean hasLabel(String label) {

        return this.label.equals(label);
    }

    @Override
    public String getLabel() {

        return label;
    }

    public String toString() {

        return label;
    }

}
