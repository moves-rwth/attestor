package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import org.junit.Test;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.InternalProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class ProcedureCallTest {

	@Test
	public void testExecute() {
		ProcedureRegistry registry = mock(ProcedureRegistry.class);
		
		StateSpace fakeResult = new InternalStateSpace(3);
		StateSpaceGeneratorFactory fakeFaktory = createFakeFactory(fakeResult);
		
		ProcedureCall call = new InternalProcedureCall( mock(Method.class), null, fakeFaktory, registry );
		call.execute();
		
		verify(registry).registerStateSpace( call, fakeResult );
		verifyNoMoreInteractions( registry );
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
		
		Scene fakeScene = mock(Scene.class );
		when( fakeScene.createProgramState( any() ) ).thenReturn( mock( ProgramState.class ));
		
		StateSpaceGeneratorFactory fakeFaktory = mock(StateSpaceGeneratorFactory.class);
		when( fakeFaktory.create(any(Program.class), any(ProgramState.class)) ).thenReturn( fakeGenerator );
		when( fakeFaktory.scene() ).thenReturn( fakeScene );
		return fakeFaktory;
	}

}
