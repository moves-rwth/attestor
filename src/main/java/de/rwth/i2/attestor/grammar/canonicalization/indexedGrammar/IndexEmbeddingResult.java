package de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * Data class holding the result of a index matching between two graphs
 * (embedding of one graph in another).
 *
 * @author Hannah
 */
public class IndexEmbeddingResult {

    private final HeapConfiguration materializedToAbstract;
    private final Nonterminal instantiatedLhs;

    public IndexEmbeddingResult(HeapConfiguration materializedToAbstract,
                                Nonterminal instantiatedLhs) {

        this.materializedToAbstract = materializedToAbstract;
        this.instantiatedLhs = instantiatedLhs;
    }


    public HeapConfiguration getMaterializedToAbstract() {

        return this.materializedToAbstract;
    }

    public Nonterminal getInstantiatedLhs() {

        return this.instantiatedLhs;
    }

}
