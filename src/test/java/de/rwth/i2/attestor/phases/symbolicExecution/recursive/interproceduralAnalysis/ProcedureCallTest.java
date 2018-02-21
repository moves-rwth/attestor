package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.InternalProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;

public class ProcedureCallTest {

	SceneObject sceneObject = new MockupSceneObject();
	
	@Test
	public void testExecute() {
		ProcedureRegistry registry = mock(ProcedureRegistry.class);
		
		
		Method methodMock = mock(Method.class);
	
		HeapConfiguration preconditionDummy = new HeapConfigurationDummy("precondition");
		
		HeapConfigurationDummy finalHeap1 = new HeapConfigurationDummy("f1");
		ProgramState finalStateDummy1 = sceneObject.scene().createProgramState( finalHeap1 );
		HeapConfigurationDummy finalHeap2 = new HeapConfigurationDummy("f2");
		ProgramState finalStateDummy2 = sceneObject.scene().createProgramState( finalHeap2 );
		StateSpace fakeResult = new InternalStateSpace(3);
		fakeResult.addState( finalStateDummy1  );
		fakeResult.setFinal( finalStateDummy1 );
		fakeResult.addState( finalStateDummy2 );
		fakeResult.setFinal( finalStateDummy2 );
		StateSpaceGeneratorFactory fakeFaktory = createFakeFactory(fakeResult);
		
		ProcedureCall call = new InternalProcedureCall( methodMock, preconditionDummy, fakeFaktory, registry );
		call.execute();
		
		verify(registry).registerStateSpace( call, fakeResult );
		verifyNoMoreInteractions( registry );
		
		final ArgumentCaptor<Contract> captor = ArgumentCaptor.forClass(Contract.class);
		verify( methodMock ).addContract( captor.capture() );
		assertEquals( preconditionDummy, captor.getValue().getPrecondition() );
		assertThat( captor.getValue().getPostconditions(), containsInAnyOrder( finalHeap1, finalHeap2) );
	}

	/**
	 * creates a factory which directly returns a fake stateSpaceGenerator
	 * which in turn returns only fakeResult when invoked.
	 * Furthermore, a fakeScene is used which creates ProgramStatedummies
	 * (just to avoid nullPointers)
	 * @param fakeResult
	 * @return
	 */
	private StateSpaceGeneratorFactory createFakeFactory(StateSpace fakeResult ) {
		StateSpaceGenerator fakeGenerator = mock( StateSpaceGenerator.class);
		try {
			when( fakeGenerator.generate() ).thenReturn( fakeResult );
		} catch (StateSpaceGenerationAbortedException e) {
			fail("As the method is mocked, it should not invoke an exception");
		}
		
		
		StateSpaceGeneratorFactory fakeFaktory = mock(StateSpaceGeneratorFactory.class);
		when( fakeFaktory.create(any(Program.class), any(ProgramState.class)) ).thenReturn( fakeGenerator );
		when( fakeFaktory.scene() ).thenReturn( sceneObject.scene() );
		return fakeFaktory;
	}

}
