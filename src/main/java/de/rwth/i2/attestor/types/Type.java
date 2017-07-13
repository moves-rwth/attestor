package de.rwth.i2.attestor.types;

import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;

/**
 * General interface for possible classes of an object
 * modeled by a node in a {@link HeapConfiguration}
 * or a {@link Value}.
 * 
 * @author Christoph
 *
 */
public interface Type extends NodeLabel {
}
