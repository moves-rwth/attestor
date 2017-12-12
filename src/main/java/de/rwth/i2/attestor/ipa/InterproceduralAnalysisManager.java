package de.rwth.i2.attestor.ipa;

import java.util.*;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
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
	
	private SceneObject scene;

	private Deque<MethodAndInput> methodsToAnalyse = new ArrayDeque<>();
	private Deque< ProgramState > statesToContinue = new ArrayDeque<>();
	private Map< MethodAndInput, Set<ProgramState> > statesCallingInput = new HashMap<>(); 
	private Map< StateSpace, MethodAndInput > contractComputedByStateSpace = new HashMap<>();
	
	public void computeFixpoints( SymbolicExecutionObserver observer ) throws StateSpaceGenerationAbortedException {
		while( ! methodsToAnalyse.isEmpty() || ! statesToContinue.isEmpty() ) {
			if( !methodsToAnalyse.isEmpty() ) {
				MethodAndInput methodAndInput = methodsToAnalyse.pop();
				methodAndInput.method.getIPAResult( methodAndInput.input, observer );
				//alert states, that there is a result for the method-input pair
				statesToContinue.addAll( statesCallingInput.get(methodAndInput) );
			}else {
				ProgramState state = statesToContinue.pop();
				StateSpace stateSpace = state.getContainingStateSpace();
				//update stateSpace
				new StateSpaceContinuationGeneratorBuilder(scene)
					.addInitialState(state)
					.setStateSpaceToContinue(stateSpace)
					.build()
					.generate();
				
				//adapt the corresponding contract
				MethodAndInput contractAltered = contractComputedByStateSpace.get( stateSpace );
				IpaAbstractMethod method = contractAltered.method;
				List<HeapConfiguration> finalConfigs = new ArrayList();
				stateSpace.getFinalStates().forEach( finalState -> finalConfigs.add( finalState.getHeap() ));
				method.contracts.addPostconditionsTo(contractAltered.input.getHeap(), finalConfigs );
				
				//alert states, that the result for the method-input pair changed
				statesToContinue.addAll( statesCallingInput.get(contractAltered));
				
				
			}
		}
	}
}
