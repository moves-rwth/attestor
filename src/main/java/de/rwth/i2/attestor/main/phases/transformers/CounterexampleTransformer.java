package de.rwth.i2.attestor.main.phases.transformers;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Set;

public interface CounterexampleTransformer {

    Set<LTLFormula> getFormulasWithCounterexamples();

    HeapConfiguration getInputOf(LTLFormula formula);
}
