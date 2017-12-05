package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.SelectorLabel;

public class AnnotatedSelectorLabel implements SelectorLabel {

    private final BasicSelectorLabel selectorLabel;
    private final String annotation;

    public AnnotatedSelectorLabel(SelectorLabel selectorLabel, String annotation) {

        if (selectorLabel instanceof AnnotatedSelectorLabel) {
            AnnotatedSelectorLabel other = (AnnotatedSelectorLabel) selectorLabel;
            this.selectorLabel = other.selectorLabel;
        } else {
            assert selectorLabel instanceof BasicSelectorLabel;
            this.selectorLabel = (BasicSelectorLabel) selectorLabel;
        }
        this.annotation = annotation;
    }

    public AnnotatedSelectorLabel(SelectorLabel selectorLabel) {

        if (selectorLabel instanceof AnnotatedSelectorLabel) {
            AnnotatedSelectorLabel other = (AnnotatedSelectorLabel) selectorLabel;
            this.selectorLabel = other.selectorLabel;
            this.annotation = other.annotation;
        } else {
            assert selectorLabel instanceof BasicSelectorLabel;
            this.selectorLabel = (BasicSelectorLabel) selectorLabel;
            this.annotation = "";
        }
    }

    @Override
    public int compareTo(SelectorLabel o) {

        if (this.equals(o)) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
        result = prime * result + ((selectorLabel == null) ? 0 : selectorLabel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnnotatedSelectorLabel other = (AnnotatedSelectorLabel) obj;
        if (annotation == null) {
            if (other.annotation != null)
                return false;
        } else if (!annotation.equals(other.annotation))
            return false;
        if (selectorLabel == null) {
            return other.selectorLabel == null;
        } else return selectorLabel.equals(other.selectorLabel);
    }

    public boolean hasLabel(String label) {

        return selectorLabel.hasLabel(label);
    }

    @Override
    public String getLabel() {

        return this.selectorLabel.toString();
    }

    @Override
    public String toString() {

        if(annotation.isEmpty()) {
            return selectorLabel.toString();
        }
        return selectorLabel.toString() + "[" + this.annotation + "]";
    }

    public String getAnnotation() {

        return annotation;
    }
}
