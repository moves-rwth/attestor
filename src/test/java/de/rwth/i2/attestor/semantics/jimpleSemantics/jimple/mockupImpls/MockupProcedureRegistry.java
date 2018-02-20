package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.InternalProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.ProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.ProcedureRegistry;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public class MockupProcedureRegistry implements ProcedureRegistry {

	@Override
	public InternalProcedureCall getProcedureCall(Method method, HeapConfiguration initialHeap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerProcedure(ProcedureCall call) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerDependency(ProgramState callingState, ProcedureCall call) {
		// TODO Auto-generated method stub

	}

}
