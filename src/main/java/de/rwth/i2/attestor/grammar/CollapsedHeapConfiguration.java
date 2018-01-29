package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.list.array.TIntArrayList;

public class CollapsedHeapConfiguration {

    private final HeapConfiguration original;
    private final HeapConfiguration collapsed;
    private final TIntArrayList originalToCollapsedExternalIndices;

    public CollapsedHeapConfiguration(HeapConfiguration original,
                                      HeapConfiguration collapsed,
                                      TIntArrayList originalToCollapsedExternalIndices) {

        this.original = original;
        this.collapsed = collapsed;
        this.originalToCollapsedExternalIndices = originalToCollapsedExternalIndices;
    }

    public HeapConfiguration getOriginal() {

        return original;
    }

    public HeapConfiguration getCollapsed() {

        return collapsed;
    }

    public TIntArrayList getOriginalToCollapsedExternalIndices() {

        return originalToCollapsedExternalIndices;
    }

    public int hashCode() {

        return collapsed.hashCode();
    }

    public boolean equals(Object other) {

        if(other == this) {
            return true;
        }
        if(other == null) {
            return false;
        }
        if(other.getClass() != CollapsedHeapConfiguration.class) {
            return false;
        }
        CollapsedHeapConfiguration otherHc = (CollapsedHeapConfiguration) other;
        return original.equals(otherHc.original)
                && originalToCollapsedExternalIndices.equals(otherHc.originalToCollapsedExternalIndices);
    }

}
