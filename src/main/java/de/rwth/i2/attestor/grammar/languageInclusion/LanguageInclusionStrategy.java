package de.rwth.i2.attestor.grammar.languageInclusion;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface LanguageInclusionStrategy {

    boolean includes(HeapConfiguration left, HeapConfiguration right);
}
