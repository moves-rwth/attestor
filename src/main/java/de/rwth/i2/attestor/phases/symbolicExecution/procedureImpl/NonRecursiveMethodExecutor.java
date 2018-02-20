package de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.AbstractInterproceduralMethodExecutor;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.ProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.ProcedureRegistry;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.ScopeExtractor;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

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
	protected void generateAndAddContract(ProcedureCall call, ContractCollection contractCollection) {
		call.execute();
	}

  
}
