package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.ScopeExtractor;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RecursiveMethodExecutorTest {
	
	private static Scene scene;

	private RecursiveMethodExecutor testSubject;
	private ContractCollection contractCollection;

	private ProcedureRegistry procedureRegistry;
	
	@BeforeClass
	public static void setUp() {
		scene = new MockupSceneObject().scene();
	}
	
	@Before
	public void init() {
		Method method = mock(Method.class);
		ScopeExtractor scopeExtractor = mock( ScopeExtractor.class );
		contractCollection = mock( ContractCollection.class );
		procedureRegistry = mock( ProcedureRegistry.class );
		testSubject = new RecursiveMethodExecutor( method, 
													  scopeExtractor, 
													  contractCollection, 
													  procedureRegistry);	
	}

	@Test
	public void testGenerateAndAddContract_ensureExecute() {
		//given
		HeapConfigurationDummy precondition = new HeapConfigurationDummy("precondition");
		
		ProgramState inputState = scene.createProgramState( precondition );
		
		ProcedureCall call = mock( ProcedureCall.class );
		when( call.getInput() ).thenReturn( inputState );
		
		//when
		testSubject.generateAndAddContract(call);
		
		//then
		//ensure empty contract with correct preconditon is added
		ArgumentCaptor<Contract> contractCaptor = ArgumentCaptor.forClass(Contract.class);
		verify( contractCollection ).addContract(contractCaptor.capture());
		Contract res = contractCaptor.getValue();
		assertEquals( precondition, res.getPrecondition() );
		assertThat( res.getPostconditions(), empty() );
		
		//ensure call is registered for later analysis
		verify( procedureRegistry ).registerProcedure(call);	
	}


}
