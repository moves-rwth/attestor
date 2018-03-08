package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class InterproceduralAnalysisTest {
	
	static final Scene SCENE = new MockupSceneObject().scene();

	InterproceduralAnalysis testSubject;
	StateSpace ssWithoutFinalStates;
	private InternalStateSpace ssWithFinalStates;
	

	@Before
	public void setUp() throws Exception {
		testSubject = spy(new InterproceduralAnalysis());
		ssWithoutFinalStates = new InternalStateSpace(5);
		ssWithFinalStates = new InternalStateSpace(2);
		ProgramState state = SCENE.createProgramState();
		ssWithFinalStates.addState(state );
		ssWithFinalStates.setFinal(state);
	}

	@Test
	public void testRun_WhenCallGeneratesNoFinalStates_DependenciesNotNotified() {
		//given
		ProcedureCall call = mock(ProcedureCall.class);
		when( call.execute() ).thenReturn( ssWithoutFinalStates );
		
		testSubject.registerProcedureCall(call);
		
		//when
		testSubject.run();
		
		//then
		verify( testSubject, never() ).notifyDependencies( any() );
	}
	
	@Test
	public void testRun_WhenCallGeneratesFinalStates_DependenciesAreNotified() {
		//given
		ProcedureCall call = mock(ProcedureCall.class);
		when( call.execute() ).thenReturn( ssWithFinalStates );
		
		testSubject.registerProcedureCall(call);
		
		//when
		testSubject.run();
		
		//then
		verify( testSubject ).notifyDependencies( any() );
	}
	
	@Test
	public void testRun_WhenContinuationGeneratesNoFinalStates_DependenciesNotNotified() {
		//given
		PartialStateSpace toContinue = new FakePartialStateSpace( ssWithFinalStates, ssWithFinalStates );
		ProcedureCall call = mock(ProcedureCall.class);
		
		testSubject.stateSpaceToAnalyzedCall.put(ssWithoutFinalStates, call);
		testSubject.remainingPartialStateSpaces.push(toContinue);
		
		//when
		testSubject.run();
		
		//then
		verify( testSubject, never() ).notifyDependencies( any() );
	}
	
	@Test
	public void testRun_WhenContinuationGeneratesFinalStates_DependenciesAreNotified() {
		//given
		PartialStateSpace toContinue = new FakePartialStateSpace( ssWithoutFinalStates, ssWithFinalStates );
		ProcedureCall call = mock(ProcedureCall.class);
		
		testSubject.stateSpaceToAnalyzedCall.put(ssWithoutFinalStates, call);
		testSubject.remainingPartialStateSpaces.push(toContinue);
		
		//when
		testSubject.run();
		
		//then
		verify( testSubject ).notifyDependencies( any() );
	}

}
