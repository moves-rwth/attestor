package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalContract;
import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.ScopeExtractor;

import java.util.Collection;
import java.util.LinkedHashSet;

public class RecursiveMethodExecutor extends AbstractInterproceduralMethodExecutor {

    public RecursiveMethodExecutor( Method method, 
    								ScopeExtractor scopeExtractor, 
    								ContractCollection contractCollection,
                                    ProcedureRegistry procedureRegistry ) {

        super(method, scopeExtractor, contractCollection, procedureRegistry);

    }

    /**
     * Adds an empty contract and registers the call as for recursive Methods the contract is 
     * generated in a later phase in order to detect fixpoints.
     */
	protected void generateAndAddContract( ProcedureCall call) {
		
		Collection<HeapConfiguration> postconditions = new LinkedHashSet<>();
		getContractCollection().addContract(new InternalContract(call.getInput().getHeap(), postconditions));
		
		procedureRegistry.registerProcedure( call );
	}

}
