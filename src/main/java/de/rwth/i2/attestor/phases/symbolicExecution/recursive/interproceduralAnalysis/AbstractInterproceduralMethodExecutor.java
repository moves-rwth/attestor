package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import java.util.Collection;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public abstract class AbstractInterproceduralMethodExecutor extends AbstractMethodExecutor {

	protected final Method method;
	protected ProcedureRegistry procedureRegistry;

	public AbstractInterproceduralMethodExecutor( Method method, 
												  ScopeExtractor scopeExtractor, 
												  ContractCollection contractCollection, 
												  ProcedureRegistry procedureRegistry ) {
		super(scopeExtractor, contractCollection);
		this.method = method;
		this.procedureRegistry = procedureRegistry;
	}

	// template method. can be configured by overriding generateAndAddContract.
	@Override
	protected final Collection<HeapConfiguration> getPostconditions(ProgramState callingState, ScopedHeap scopedHeap) {
	
	    HeapConfiguration heapInScope = scopedHeap.getHeapInScope();
	    ContractMatch contractMatch = getContractCollection().matchContract(heapInScope);
	    if( contractMatch.hasMatch() ) {
	    	heapInScope = contractMatch.getPrecondition();
	    }
	    
	    ProcedureCall call = procedureRegistry.getProcedureCall( method, heapInScope );
	    procedureRegistry.registerDependency( callingState, call );
	    
	    if(!contractMatch.hasMatch()) {
	        
	        ContractCollection contractCollection = getContractCollection();
	        generateAndAddContract( call);
	        contractMatch = contractCollection.matchContract(heapInScope);
	    }
	    
	    return scopedHeap.merge(contractMatch);
	}
	
	/**
	 * Is called when no contract for this call is found. 
     * @param call the procedureCall for this method and input
	 */
	abstract protected void generateAndAddContract( ProcedureCall call);

}