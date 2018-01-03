package de.rwth.i2.attestor.ipa;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.methods.Method;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

import java.util.*;

/* will be used to manage communication between procedures,
* i.e. which methods/preconditions have to be analysed,
* for which states exists updated information.
*/
public class InterproceduralAnalysisManager extends SceneObject{
	
	class MethodAndInput{
		Method method;
		ProgramState input; 
		
		MethodAndInput(Method method, ProgramState input){
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
	
	public InterproceduralAnalysisManager(Scene scene) {
		super(scene);
	}
	
	private Deque<MethodAndInput> methodsToAnalyse = new ArrayDeque<>();
	private Deque< ProgramState > statesToContinue = new ArrayDeque<>();
	private Map< MethodAndInput, Set<ProgramState> > statesCallingInput = new LinkedHashMap<>(); 
	private Map< StateSpace, MethodAndInput > contractComputedByStateSpace = new LinkedHashMap<>();
	


	/**
	 * 
	 * @param method
	 * @param input the reachable fragment serving as precondition wrapped in a ProgramState
	 */
	public void registerToCompute( Method method, ProgramState input ) {
		MethodAndInput precondition = new MethodAndInput(method, input);
		
		if( !methodsToAnalyse.contains(precondition) ) {
			methodsToAnalyse.push(precondition);
			statesCallingInput.put(precondition, new HashSet<>() );
		}
	}
	
	public void registerAsDependentOf( ProgramState dependent, Method method, ProgramState input	) {
		
		MethodAndInput precondition = new MethodAndInput(method, input);
		
		if( !statesCallingInput.containsKey(precondition) ) {
			statesCallingInput.put(precondition, new HashSet<>() );
		}
		
		statesCallingInput.get(precondition).add(dependent);
	}
	
	public StateSpace computeFixpoint(Method mainProgram, ProgramState initialState)
											throws StateSpaceGenerationAbortedException {
		
		// TODO StateSpace mainStateSpace = observer.generateStateSpace( mainProgram.getControlFlow(), initialState );
		StateSpace mainStateSpace = null; // TODO
		registerStateSpace(mainProgram, initialState, mainStateSpace);
		
		while( ! methodsToAnalyse.isEmpty() || ! statesToContinue.isEmpty() ) {
			if( !methodsToAnalyse.isEmpty() ) {
				MethodAndInput methodAndInput = methodsToAnalyse.pop();
				StateSpace stateSpace = computeStateSpace(methodAndInput);
				
				//store the mapping from stateSpace to input for later reference
				contractComputedByStateSpace.put(stateSpace, methodAndInput);
				//alert states, that there is a result for the method-input pair
				statesToContinue.addAll( statesCallingInput.get(methodAndInput) );
			}else {
				ProgramState state = statesToContinue.pop();
				MethodAndInput contractAltered = continueStateSpace(state);
				
				//alert states, that the result for the method-input pair changed
				statesToContinue.addAll( statesCallingInput.get(contractAltered) );
			}
		}
		
		return mainStateSpace;
	}

	private MethodAndInput continueStateSpace(ProgramState state)
			throws StateSpaceGenerationAbortedException {
		StateSpace stateSpace = state.getContainingStateSpace();
		MethodAndInput contractAltered = contractComputedByStateSpace.get( stateSpace );
		Method method = contractAltered.method;
		//update stateSpace
		// TODO observer.continueStateSpace(stateSpace, method.getControlFlow(), state);
		
		//adapt the corresponding contract
		List<HeapConfiguration> finalConfigs = new ArrayList<>();
		stateSpace.getFinalStates().forEach( finalState -> finalConfigs.add( finalState.getHeap() ));

		// TODO method.contracts.addPostconditionsTo(contractAltered.input.getHeap(), finalConfigs );
		return contractAltered;
	}

	private StateSpace computeStateSpace(MethodAndInput methodAndInput)
			throws StateSpaceGenerationAbortedException {
		Method method = methodAndInput.method;
		
		Program program = method.getBody();
		ProgramState inputState = methodAndInput.input;
		
		StateSpace stateSpace = null; // TODO observer.generateStateSpace( program, inputState );
		
		//extract and store the generated contract
		List<HeapConfiguration> finalConfigs = new ArrayList<>();
		stateSpace.getFinalStates().forEach( finalState -> finalConfigs.add( finalState.getHeap() ));
		// TODO method.contracts.addPostconditionsTo( inputState.getHeap(), finalConfigs );
		return stateSpace;
	}

	private void registerStateSpace(Method mainProgram, ProgramState initialState,
			StateSpace mainStateSpace) {
		MethodAndInput mainInput = new MethodAndInput(mainProgram, initialState);
		contractComputedByStateSpace.put(mainStateSpace, mainInput );
		statesCallingInput.put( mainInput, new LinkedHashSet<>() );
	}
}
