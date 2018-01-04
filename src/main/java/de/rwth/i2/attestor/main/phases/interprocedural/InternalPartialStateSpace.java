package de.rwth.i2.attestor.main.phases.interprocedural;

import de.rwth.i2.attestor.interprocedural.PartialStateSpace;
import de.rwth.i2.attestor.interprocedural.ProcedureCall;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public class InternalPartialStateSpace implements PartialStateSpace {

    public InternalPartialStateSpace(ProgramState callingState, Method method, ProgramState preconditionState) {

    }

    @Override
    public ProcedureCall continueExecution() {

        return null;
    }

}
