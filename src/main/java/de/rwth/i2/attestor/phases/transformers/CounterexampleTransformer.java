package de.rwth.i2.attestor.phases.transformers;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Set;

public interface CounterexampleTransformer {

    Set<LTLFormula> getFormulasWithCounterexamples();

    ProgramState getInputOf(LTLFormula formula);
}
