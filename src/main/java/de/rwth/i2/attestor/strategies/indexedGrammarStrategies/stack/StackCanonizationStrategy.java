package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface StackCanonizationStrategy {

	void canonizeStack(HeapConfiguration hc);
}
