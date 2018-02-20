package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.InternalProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.ProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.ProcedureRegistry;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

public class ProcedureRegistryStub implements ProcedureRegistry {

	SceneObject scenenObject;
	
	public ProcedureRegistryStub(SceneObject sceneObject) {
		this.scenenObject = sceneObject;
	}

	@Override
	public ProcedureCall getProcedureCall(Method method, HeapConfiguration initialHeap) {
		ProgramState initialState = scenenObject.scene().createProgramState( initialHeap );
		return new FakeProcedureCall(method, initialState );
	}

	@Override
	public void registerProcedure(ProcedureCall call) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerDependency(ProgramState callingState, ProcedureCall call) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerStateSpace(ProcedureCall call, StateSpace generatedStateSpace) {
		// TODO Auto-generated method stub
		
	}

}
