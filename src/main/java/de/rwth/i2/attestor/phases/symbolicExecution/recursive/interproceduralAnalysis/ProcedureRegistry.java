package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

/**
 * This is the interface for the stateSpaceGeneration to give relevant information
 * to the InterproceduralAnalysis component. 
 * @author Hannah
 *
 */
public interface ProcedureRegistry {
    
	/**
	 * Wraps the method and initialHeap in a new procedureCall
	 * @param method the code of the procedure
	 * @param initialHeap the initialHeap from which the procedureCall should start
	 * @return the corresponding procedureCall
	 */
	ProcedureCall getProcedureCall(Method method, HeapConfiguration initialHeap);

	/**
	 * Enqueues the procedureCall to be analyzed later on.
	 * Semantics should call this method whenever it finds for recursive method
	 * a initialHeap without an existing contract. (non-recursive methods can
	 * instead directly be executed to obtain the contract)
	 * @param call the required procedureCall
	 */
	void registerProcedure(ProcedureCall call);
	/**
	 * Stores a dependency between the given state and procedureCall.
	 * Semantics should call this method whenever it finds a methodCall
	 * (even when it already has a contract or when the method is non-recursive.
	 * The reason is, that the contract could still change during the fixpoint 
	 * computation and then the given state also needs to be continued)
	 * @param callingState the state when the call is invoked
	 * @param call the corresponding procedureCall
	 */
	void registerDependency(ProgramState callingState, ProcedureCall call);
	/**
	 * Stores the relation between a proceduceCall and the stateSpace generated
	 * by this procedureCall. 
	 * This method should be called when a procedureCall is evaluated. It is necessary
	 * to continue stateSpaces when new contracts are discovered.
	 * @param call the call corresponding to the stateSpace
	 * @param generatedStateSpace the stateSpace corresponding to the call
	 */
	void registerStateSpace( ProcedureCall call, StateSpace generatedStateSpace );
}
