package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;

public class OriginalEmbedding implements Matching {

    private final CollapsedHeapConfiguration collapsedHeapConfiguration;
    private final Matching collapsedEmbedding;

    public OriginalEmbedding(CollapsedHeapConfiguration collapsedHeapConfiguration,
                             Matching collapsedEmbedding) {

        this.collapsedHeapConfiguration = collapsedHeapConfiguration;
        this.collapsedEmbedding = collapsedEmbedding;
    }

    @Override
    public HeapConfiguration pattern() {

        return collapsedHeapConfiguration.getOriginal();
    }

    @Override
    public int match(int element) {

        if(pattern().isExternalNode(element)) {
           int index = pattern().externalIndexOf(element);
           int collapsedIndex = collapsedHeapConfiguration.getOriginalToCollapsedExternalIndices().get(index);
           int extNode = collapsedHeapConfiguration.getCollapsed().externalNodeAt(collapsedIndex);
           return collapsedEmbedding.match(extNode);
        } else {
            return collapsedEmbedding.match(element);
        }
    }
}
