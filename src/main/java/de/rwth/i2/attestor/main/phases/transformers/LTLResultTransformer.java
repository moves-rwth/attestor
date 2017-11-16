package de.rwth.i2.attestor.main.phases.transformers;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Map;

public interface LTLResultTransformer {

    Map<LTLFormula, Boolean> getLTLResults();

    boolean hasAllLTLSatisfied();

    HeapConfiguration getCounterexampleInput(LTLFormula formula);
}

