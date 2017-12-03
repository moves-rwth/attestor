package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.NonterminalEdgeBuilder;
import gnu.trove.list.array.TIntArrayList;

public class InternalNonterminalEdgeBuilder implements NonterminalEdgeBuilder {

    final Nonterminal nt;
    final HeapConfigurationBuilder parentBuilder;
    final TIntArrayList tentacles = new TIntArrayList();

    public InternalNonterminalEdgeBuilder(Nonterminal nt,
                                          HeapConfigurationBuilder heapConfigurationBuilder) {

        this.nt = nt;
        parentBuilder = heapConfigurationBuilder;
    }

    @Override
    public NonterminalEdgeBuilder addTentacle(int tentacle) {

        tentacles.add(tentacle);
        return this;
    }

    @Override
    public HeapConfigurationBuilder build() {

        parentBuilder.addNonterminalEdge(nt, tentacles);
        return parentBuilder;
    }

}
