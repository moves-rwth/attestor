package de.rwth.i2.attestor.phases.symbolicExecution.recursive;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalContract;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.ProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.ProcedureRegistry;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

public class InternalProcedureCall extends SceneObject implements ProcedureCall {

	private StateSpaceGeneratorFactory factory;
	private ProcedureRegistry registry;
	
    private Method method;
    private ProgramState preconditionState;

    
    public InternalProcedureCall( Method method, HeapConfiguration precondition, 
    							  StateSpaceGeneratorFactory factory, ProcedureRegistry registry ) {

    	super( factory );//as SceneObject
    	this.registry = registry; 
    	
        this.method = method;
        this.preconditionState = scene().createProgramState(precondition);
        this.factory = factory;
    }



    @Override
    public StateSpace execute() {
    	
    	ProgramState initialState = preconditionState.clone();

        try {
            StateSpace stateSpace = factory.create( method.getBody(), initialState ).generate();

            List<HeapConfiguration> finalHeaps = new ArrayList<>();
            stateSpace.getFinalStates().forEach( finalState -> finalHeaps.add(finalState.getHeap()) );
            Contract contract = new InternalContract(preconditionState.getHeap(), finalHeaps);
            method.addContract(contract);
            
            registry.registerStateSpace( this, stateSpace );
            
            return stateSpace;
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Procedure call execution failed.");
        }
    }

    @Override
    public Method getMethod() {

        return method;
    }

    @Override
    public ProgramState getInput() {

        return preconditionState;
    }

    @Override
    public int hashCode() {

        if(preconditionState == null) {
            return Objects.hashCode(method);
        } else {
            return Objects.hash(method, preconditionState.getHeap());
        }
    }

    @Override
    public boolean equals(Object otherOject) {

        if(this == otherOject) {
            return true;
        }
        if(otherOject == null) {
            return false;
        }
        if(otherOject.getClass() != InternalProcedureCall.class) {
            return false;
        }
        InternalProcedureCall call = (InternalProcedureCall) otherOject;
        return method.equals(call.method) &&
                preconditionState.equals(call.preconditionState);
    }
    
    @Override
    public String toString() {
    	return method.getName();
    }
}
