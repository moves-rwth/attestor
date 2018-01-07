package de.rwth.i2.attestor.interprocedural;

public interface PartialStateSpace {

    void continueExecution(ProcedureCall call);
    //StateSpace unfinishedStateSpace;
    //ProcedureCall procedureCall;
}
