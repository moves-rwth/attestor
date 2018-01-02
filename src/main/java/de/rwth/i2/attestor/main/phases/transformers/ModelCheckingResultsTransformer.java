package de.rwth.i2.attestor.main.phases.transformers;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.modelChecking.ModelCheckingTrace;

import java.util.Map;

public interface ModelCheckingResultsTransformer {

    Map<LTLFormula, Boolean> getLTLResults();

    ModelCheckingTrace getTraceOf(LTLFormula formula);

    boolean hasAllLTLSatisfied();

    int getNumberSatFormulae();
}

