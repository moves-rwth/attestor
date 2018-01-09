package de.rwth.i2.attestor.main.phases.symbolicExecution.interprocedural;

import de.rwth.i2.attestor.interprocedural.InterproceduralAnalysis;
import de.rwth.i2.attestor.interprocedural.PartialStateSpace;
import de.rwth.i2.attestor.interprocedural.ProcedureCall;
import de.rwth.i2.attestor.interprocedural.ProcedureRegistry;
import de.rwth.i2.attestor.main.phases.symbolicExecution.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public class InternalProcedureRegistry implements ProcedureRegistry {

    private final InterproceduralAnalysis analysis;
    private final StateSpaceGeneratorFactory stateSpaceGeneratorFactory;

    public InternalProcedureRegistry(InterproceduralAnalysis analysis,
                                     StateSpaceGeneratorFactory stateSpaceGeneratorFactory) {

        this.analysis = analysis;
        this.stateSpaceGeneratorFactory = stateSpaceGeneratorFactory;
    }

    @Override
    public void registerProcedure(Method method, ProgramState preconditionState) {

        ProcedureCall call = new InternalProcedureCall(method, preconditionState, stateSpaceGeneratorFactory);
        analysis.registerProcedureCall(call);
    }

    @Override
    public void registerDependency(ProgramState callingState, Method method, ProgramState preconditionState) {

        ProcedureCall call = new InternalProcedureCall(method, preconditionState, stateSpaceGeneratorFactory);
        PartialStateSpace partialStateSpace = new InternalPartialStateSpace(callingState, stateSpaceGeneratorFactory);
        analysis.registerDependency(call, partialStateSpace);
    }
}
