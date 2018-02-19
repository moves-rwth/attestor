package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

public interface PartialStateSpace {

    void continueExecution(ProcedureCall call);
    StateSpace unfinishedStateSpace();
    //ProcedureCall procedureCall;
}
