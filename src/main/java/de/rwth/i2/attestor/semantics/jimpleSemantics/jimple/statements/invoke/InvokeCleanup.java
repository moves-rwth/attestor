package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsOptions;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

public interface InvokeCleanup {

    ProgramState getCleanedResultState(ProgramState state, SemanticsOptions options) throws NotSufficientlyMaterializedException;
}
