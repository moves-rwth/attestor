package de.rwth.i2.attestor.types;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;

import java.util.Map;

/**
 * General interface for possible classes of an object
 * modeled by a node in a {@link HeapConfiguration}
 * or a {@link Value}.
 *
 * @author Christoph
 */
public interface Type extends NodeLabel {

    /**
     * Checks whether a node with this type has an outgoing selector label
     * with the given name.
     *
     * @param selectorLabel The selector label in question
     * @return true if and only if nodes with this type may have outgoing selector
     * edges with the given name.
     */
    boolean hasSelectorLabel(SelectorLabel selectorLabel);

    /**
     * @param selectorLabel the selector label to be added to this type
     */
    void addSelectorLabel(SelectorLabel selectorLabel, String defaultValue);

    Map<SelectorLabel, String> getSelectorLabels();

    boolean isOptional(SelectorLabel selectorLabel);

    boolean isPrimitiveType();
}
