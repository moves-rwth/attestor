package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public interface ProcedureRegistry {
    
    ProcedureCall getProcedureCall( Method method,  ProgramState callingState );

	void registerProcedure(ProcedureCall call);

	void registerDependency(ProgramState callingState, ProcedureCall call);
}
