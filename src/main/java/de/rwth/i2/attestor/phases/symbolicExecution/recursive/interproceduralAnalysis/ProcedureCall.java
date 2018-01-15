package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public interface ProcedureCall {

    PartialStateSpace execute();
    Method getMethod();
    ProgramState getInput();
}
