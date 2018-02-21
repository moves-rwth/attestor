package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.ScopeExtractor;

public class FakeInterproceduralMethodExecutor extends AbstractInterproceduralMethodExecutor {

	public FakeInterproceduralMethodExecutor(Method method, ScopeExtractor scopeExtractor,
			ContractCollection contractCollection, ProcedureRegistry procedureRegistry) {
		super(method, scopeExtractor, contractCollection, procedureRegistry);
	}

	@Override
	protected void generateAndAddContract(ProcedureCall call) {

	}

}
