package de.rwth.i2.attestor.ipa;

import java.util.*;

import de.rwth.i2.attestor.stateSpaceGeneration.*;

/* will be used to manage communication between procedures,
* i.e. which methods/preconditions have to be analysed,
* for which states exists updated information.
*/
public class InterproceduralAnalysisManager {
	
	class MethodAndInput{
		IpaAbstractMethod method;
		ProgramState input;
	}

	Deque<MethodAndInput> methodsToAnalyse = new ArrayDeque<>();
	Deque< ProgramState > statesToContinue = new ArrayDeque<>();
	Map< MethodAndInput, Set<ProgramState> > statesCallingInput = new HashMap<>(); 
	
	
	public void computeFixpoints( SymbolicExecutionObserver observer ) throws StateSpaceGenerationAbortedException {
		while( ! methodsToAnalyse.isEmpty() || ! statesToContinue.isEmpty() ) {
			if( !methodsToAnalyse.isEmpty() ) {
				MethodAndInput methodAndInput = methodsToAnalyse.pop();
				methodAndInput.method.getIPAResult(methodAndInput.input, observer);
			}else {
				ProgramState state = statesToContinue.pop();
				//StateSpace stateSpace = state.getStateSpace();
				//stateSpace.a
			}
		}
	}
}
