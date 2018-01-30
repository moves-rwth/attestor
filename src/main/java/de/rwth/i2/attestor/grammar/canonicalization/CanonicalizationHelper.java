package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.grammar.CollapsedHeapConfiguration;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface CanonicalizationHelper {

    /**
     * If an embedding of rhs in toAbstract can be found it computes it
     * and replaces it with a nonterminal edge labeled with lhs.
     *
     * @param toAbstract the target graph
     * @param rhs        the pattern graph
     * @param lhs        the label of the replacing nonterminal edge
     * @return the abstracted graph if an embedding of rhs can be found, null otherwise.
     */
    HeapConfiguration tryReplaceMatching(HeapConfiguration toAbstract,
                                    HeapConfiguration rhs, Nonterminal lhs);

    /**
     * If the grammar type requires a modification of the graph before it can be abstracted,
     * it is done with this method.
     *
     * @param toAbstract the graph which shall be abstracted
     * @return the modified graph (or the graph itself if no modification is necessary)
     */
    HeapConfiguration prepareHeapForCanonicalization(HeapConfiguration toAbstract);

    /**
     * If an embedding of rhs in toAbstract can be found it computes it
     * and replaces it with a nonterminal edge labeled with lhs.
     *
     * @param toAbstract the target graph
     * @param rhs        the pattern graph
     * @param lhs        the label of the replacing nonterminal edge
     * @return the abstracted graph if an embedding of rhs can be found, null otherwise.
     */
    HeapConfiguration tryReplaceMatching(HeapConfiguration toAbstract,
                                         CollapsedHeapConfiguration rhs, Nonterminal lhs);


}