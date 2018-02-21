package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.ScopeExtractor;

public class NonRecursiveMethodExecutor extends AbstractInterproceduralMethodExecutor {



    public NonRecursiveMethodExecutor( Method method,
    								   ScopeExtractor scopeExtractor, 
    								   ContractCollection contractCollection,
                                       ProcedureRegistry procedureRegistry ) {

        super( method, scopeExtractor, contractCollection, procedureRegistry);
    }

    /**
     * generates the Contract by executing the call
     */
	@Override
	protected void generateAndAddContract(ProcedureCall call) {
		call.execute();
	}

  
}
