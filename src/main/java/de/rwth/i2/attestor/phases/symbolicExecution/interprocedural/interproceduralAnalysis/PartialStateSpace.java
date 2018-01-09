package de.rwth.i2.attestor.phases.symbolicExecution.interprocedural.interproceduralAnalysis;

public interface PartialStateSpace {

    void continueExecution(ProcedureCall call);
    //StateSpace unfinishedStateSpace;
    //ProcedureCall procedureCall;
}
