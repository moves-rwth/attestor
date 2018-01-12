package de.rwth.i2.attestor.grammar.materialization.util;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * Responsible for replacing a NonterminalEdge by a rule graph
 *
 * @author Hannah
 */
public class GraphMaterializer {


    public HeapConfiguration getMaterializedCloneWith(HeapConfiguration inputGraph,
                                                      int toReplaceIndex,
                                                      HeapConfiguration rule) {

        final HeapConfiguration cloneOfInput = inputGraph.clone();

        return cloneOfInput.builder().replaceNonterminalEdge(toReplaceIndex, rule).build();
    }

}
