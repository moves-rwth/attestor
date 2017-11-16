package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsObserver;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

public interface InvokeCleanup {

    ProgramState getCleanedResultState(ProgramState state, SemanticsObserver options) throws NotSufficientlyMaterializedException;
}
