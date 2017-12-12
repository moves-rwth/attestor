package de.rwth.i2.attestor.graph.heap;

public interface NonterminalEdgeBuilder {

    NonterminalEdgeBuilder addTentacle(int tentacle);

    HeapConfigurationBuilder build();


}
