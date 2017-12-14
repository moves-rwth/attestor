package de.rwth.i2.attestor.grammar.languageInclusion;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public interface LanguageInclusionStrategy {

    boolean includes(ProgramState left, ProgramState right);
}
