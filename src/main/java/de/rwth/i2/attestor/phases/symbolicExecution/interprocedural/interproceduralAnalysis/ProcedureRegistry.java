package de.rwth.i2.attestor.phases.symbolicExecution.interprocedural.interproceduralAnalysis;

import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public interface ProcedureRegistry {

    void registerProcedure(Method method, ProgramState preconditionState);

    void registerDependency(ProgramState callingState, Method method, ProgramState preconditionState);
}
