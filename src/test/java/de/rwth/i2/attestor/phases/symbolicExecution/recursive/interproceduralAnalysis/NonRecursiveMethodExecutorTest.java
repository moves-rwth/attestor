package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.ScopeExtractor;

public class NonRecursiveMethodExecutorTest {
	
	private NonRecursiveMethodExecutor testSubject;
	private ContractCollection contractCollection;
	
	
	@Before
	public void init() {
		Method method = mock(Method.class);
		ScopeExtractor scopeExtractor = mock( ScopeExtractor.class );
		contractCollection = mock( ContractCollection.class );
		ProcedureRegistry procedureRegistry = mock( ProcedureRegistry.class );
		testSubject = new NonRecursiveMethodExecutor( method, 
													  scopeExtractor, 
													  contractCollection, 
													  procedureRegistry);	
	}

	@Test
	public void testGenerateAndAddContract_ensureExecute() {
		//given
		ProcedureCall call = mock( ProcedureCall.class );
		
		//when
		testSubject.generateAndAddContract(call);
		
		//then
		verify( call ).execute();
	}

}
