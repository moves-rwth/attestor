package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface IndexCanonizationStrategy {

	void canonizeStack(HeapConfiguration hc);
}
