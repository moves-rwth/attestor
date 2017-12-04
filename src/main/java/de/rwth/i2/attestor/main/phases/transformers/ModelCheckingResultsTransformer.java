package de.rwth.i2.attestor.main.phases.transformers;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.counterexampleGeneration.Trace;

import java.util.Map;

public interface ModelCheckingResultsTransformer {

    Map<LTLFormula, Boolean> getLTLResults();

    Trace getTraceOf(LTLFormula formula);

    boolean hasAllLTLSatisfied();
}

