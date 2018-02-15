package de.rwth.i2.attestor.phases.transformers;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.phases.modelChecking.modelChecker.ModelCheckingResult;
import de.rwth.i2.attestor.phases.modelChecking.modelChecker.ModelCheckingTrace;

import java.util.Map;

public interface ModelCheckingResultsTransformer {

    Map<LTLFormula, ModelCheckingResult> getLTLResults();

    ModelCheckingTrace getTraceOf(LTLFormula formula);

    boolean hasAllLTLSatisfied();

    int getNumberSatFormulae();
}

