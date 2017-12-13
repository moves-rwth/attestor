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
		
		MethodAndInput(IpaAbstractMethod method, ProgramState input){
			this.method = method;
			this.input = input;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((input == null) ? 0 : input.hashCode());
			result = prime * result + ((method == null) ? 0 : method.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MethodAndInput other = (MethodAndInput) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (input == null) {
				if (other.input != null)
					return false;
			} else if (!input.equals(other.input))
				return false;
			if (method == null) {
				if (other.method != null)
					return false;
			} else if (!method.equals(other.method))
				return false;
			return true;
		}

		private InterproceduralAnalysisManager getOuterType() {
			return InterproceduralAnalysisManager.this;
		}
		
		
	}
	
	private SceneObject scene;

	private Deque<MethodAndInput> methodsToAnalyse = new ArrayDeque<>();
	private Deque< ProgramState > statesToContinue = new ArrayDeque<>();
	private Map< MethodAndInput, Set<ProgramState> > statesCallingInput = new LinkedHashMap<>(); 
	private Map< StateSpace, MethodAndInput > contractComputedByStateSpace = new LinkedHashMap<>();
	
	/**
	 * 
	 * @param method
	 * @param input the reachable fragment serving as precondition wrapped in a ProgramState
	 */
	public void registerToCompute( IpaAbstractMethod method, ProgramState input ) {
		MethodAndInput precondition = new MethodAndInput(method, input);
		
		if( !methodsToAnalyse.contains(precondition) ) {
			methodsToAnalyse.push(precondition);
		}
	}
	
	public void registerAsDependentOf( ProgramState dependent, IpaAbstractMethod method, ProgramState input	) {
		MethodAndInput precondition = new MethodAndInput(method, input);
		
		if( !statesCallingInput.containsKey(precondition) ) {
			statesCallingInput.put(precondition, new HashSet<>() );
		}
		
		statesCallingInput.get(precondition).add(dependent);
	}
	
	public void computeFixpoints( SymbolicExecutionObserver observer ) 
											throws StateSpaceGenerationAbortedException {
		
		while( ! methodsToAnalyse.isEmpty() || ! statesToContinue.isEmpty() ) {
			if( !methodsToAnalyse.isEmpty() ) {
				MethodAndInput methodAndInput = methodsToAnalyse.pop();
				IpaAbstractMethod method = methodAndInput.method;
				
				Program program = method.getControlFlow();
				ProgramState initialState = methodAndInput.input;
				
				StateSpace stateSpace = observer.generateStateSpace( program, initialState );
				
				//extract and store the generated contract
				List<HeapConfiguration> finalConfigs = new ArrayList<>();
				stateSpace.getFinalStates().forEach( finalState -> finalConfigs.add( finalState.getHeap() ));
				method.contracts.addPostconditionsTo( initialState.getHeap(), finalConfigs );
				
				//store the mapping from stateSpace to input for later reference
				contractComputedByStateSpace.put(stateSpace, methodAndInput);
				//alert states, that there is a result for the method-input pair
				statesToContinue.addAll( statesCallingInput.get(methodAndInput) );
			}else {
				ProgramState state = statesToContinue.pop();
				StateSpace stateSpace = state.getContainingStateSpace();
				MethodAndInput contractAltered = contractComputedByStateSpace.get( stateSpace );
				IpaAbstractMethod method = contractAltered.method;
				//update stateSpace
				new StateSpaceContinuationGeneratorBuilder(scene)
					.addEntryState(state)
					.setStateSpaceToContinue(stateSpace)
					.setProgram( method.getControlFlow() )
					.build()
					.generate();
				
				//adapt the corresponding contract
				List<HeapConfiguration> finalConfigs = new ArrayList<>();
				stateSpace.getFinalStates().forEach( finalState -> finalConfigs.add( finalState.getHeap() ));
				method.contracts.addPostconditionsTo(contractAltered.input.getHeap(), finalConfigs );
				
				//alert states, that the result for the method-input pair changed
				statesToContinue.addAll( statesCallingInput.get(contractAltered) );
			}
		}
	}
}
