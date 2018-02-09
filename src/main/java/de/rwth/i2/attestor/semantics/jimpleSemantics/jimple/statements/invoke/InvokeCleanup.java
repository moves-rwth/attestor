package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public interface InvokeCleanup {

    ProgramState getCleanedResultState(ProgramState state);
}
