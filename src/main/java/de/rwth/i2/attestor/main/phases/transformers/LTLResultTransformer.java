package de.rwth.i2.attestor.main.phases.transformers;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.modelChecking.Counterexample;

import java.util.Map;

public interface LTLResultTransformer {

    Map<LTLFormula, Counterexample> getLTLResults();

    boolean hasAllLTLSatisfied();
}

