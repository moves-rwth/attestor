package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import org.junit.Test;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.ContractMatch;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.NoContractMatch;
import de.rwth.i2.attestor.procedures.ScopedHeap;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

public class AbstractInterproceduralMethodExecutorTest {
	
	Scene scene = new MockupSceneObject().scene();

	@Test
	public void testGetPostconditions_WhenContractIsPresent() {
		
		//mock all componenets of the MethodExecutor
		Method method = mock( Method.class );
		ContractCollection contractCollection = mock( ContractCollection.class );
		ProcedureRegistry procedureRegistry = mock( ProcedureRegistry.class );
		AbstractInterproceduralMethodExecutor testSubject 
						= spy(new FakeInterproceduralMethodExecutor( method , 
																 null, 
																 contractCollection, 
																 procedureRegistry )
								);
		
		HeapConfigurationDummy callingHeap = new HeapConfigurationDummy("callingState");
		ProgramState callingState = scene.createProgramState( callingHeap );
		
		HeapConfigurationDummy heapInScope = new HeapConfigurationDummy("heapInScope");
		ScopedHeap scopedHeap = mock(ScopedHeap.class);
		when( scopedHeap.getHeapInScope() ).thenReturn(heapInScope);
		
		//ensure it does not match the given heap
		ContractMatch noMatch = NoContractMatch.NO_CONTRACT_MATCH;
		when( contractCollection.matchContract(heapInScope)).thenReturn( noMatch );
		
		testSubject.getPostconditions(callingState, scopedHeap );
		
		verify( procedureRegistry ).registerDependency( eq(callingState), any() );
	}

}
