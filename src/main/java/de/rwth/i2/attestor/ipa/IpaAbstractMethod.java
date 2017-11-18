package de.rwth.i2.attestor.ipa;


import java.util.*;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

public class IpaAbstractMethod extends AbstractMethod {
	
	private static Map<String, IpaAbstractMethod> knownMethods = new HashMap<>();
	
	public static IpaAbstractMethod getMethod( String signature ){
		if( ! knownMethods.containsKey(signature) ){
			knownMethods.put(signature, new IpaAbstractMethod( signature ) );
		}
		return knownMethods.get( signature );
	}

	final IpaContractCollection contracts = new IpaContractCollection();

	public IpaAbstractMethod(String displayName) {
		super();
		super.setDisplayName(displayName);
	}
	
	@Override
	public Set<ProgramState> getFinalStates(ProgramState input, SymbolicExecutionObserver observer) {
		try {
			return getResultStates(input, observer);
		} catch (StateSpaceGenerationAbortedException e) {
			throw new IllegalStateException("No contract found");
		}
	}

	
	public void addContracts( HeapConfiguration precondition, List<HeapConfiguration> postconditions ){
		if( ! contracts.hasMatchingPrecondition(precondition) ){

			contracts.addPrecondition(precondition);
		}
		List<HeapConfiguration> currentPostconditions = contracts.getPostconditions( precondition );
		currentPostconditions.addAll( postconditions );
	}

	@Override
	public Set<ProgramState> getResult(ProgramState input, SymbolicExecutionObserver observer)
													throws StateSpaceGenerationAbortedException {

		observer.update(this, input);
		return getResultStates(input, observer);
	}

	public Set<ProgramState> getResultStates(ProgramState input, SymbolicExecutionObserver observer)
			throws StateSpaceGenerationAbortedException {
		Set<ProgramState> result = new HashSet<>();
		for (HeapConfiguration postConfig : getIPAResult(input, observer)) {
			ProgramState state = input.shallowCopyWithUpdateHeap(postConfig);
			state.setProgramCounter(0);
			state.setScopeDepth(0);
			result.add(state);
		}

		return result;
	}

	public List<HeapConfiguration> getIPAResult( ProgramState input, SymbolicExecutionObserver observer )
			throws StateSpaceGenerationAbortedException{
		HeapConfiguration currentConfig = input.getHeap();
		Pair<HeapConfiguration, Pair<HeapConfiguration,Integer>> splittedConfig = prepareInput( currentConfig );
		HeapConfiguration reachableFragment = splittedConfig.first();
		HeapConfiguration remainingFragment = splittedConfig.second().first(); 
		int placeholderPos = splittedConfig.second().second();

		
		if( !contracts.hasMatchingPrecondition(reachableFragment) || !isReuseResultsEnabled() ){ 
			
			computeContract( input, reachableFragment, observer );

		}else{
			int [] reordering = contracts.getReordering( reachableFragment );
			remainingFragment = adaptExternalOrdering( reachableFragment, remainingFragment, 
														placeholderPos, reordering );
		}
		
		List<HeapConfiguration> postconditions = contracts.getPostconditions(reachableFragment);
		return applyContract(remainingFragment, placeholderPos, postconditions );
		
	}
	
	private void computeContract(ProgramState input, HeapConfiguration reachableFragment, SymbolicExecutionObserver observer)
			throws StateSpaceGenerationAbortedException {
		
		contracts.addPrecondition( reachableFragment );
		ProgramState initialState = input.shallowCopyWithUpdateHeap(reachableFragment);
		StateSpace stateSpace = observer.generateStateSpace(method, initialState);
		List<HeapConfiguration> postconditions = contracts.getPostconditions(reachableFragment);

		for( ProgramState finalState : stateSpace.getFinalStates() ){
			//otherwise, any local variables are already removed 
			if( ! Settings.getInstance().options().isRemoveDeadVariables() ){
			removeLocals( finalState );
			}
			postconditions.add( finalState.getHeap() );
		}
	}

	private void removeLocals(ProgramState finalState) {
		final HeapConfiguration finalHeap = finalState.getHeap();
		HeapConfigurationBuilder builder = finalHeap.clone().builder();
		TIntArrayList variableIndices = finalHeap.variableEdges();
		for( int i = 0; i < variableIndices.size(); i++ ){
			final int varId = variableIndices.get(i);
			String variableName = finalHeap.nameOf( varId );
			if( !( Constants.isConstant(variableName) || variableName.equals("@return")) ){
				builder.removeVariableEdge( varId );
			}
		}
	}

	/**
	 * @param input
	 * @return <reachableFragment,remainingFragment>
	 */
	protected Pair<HeapConfiguration, Pair<HeapConfiguration,Integer>> prepareInput( HeapConfiguration input ){
		ReachableFragmentComputer helper = new ReachableFragmentComputer( this.toString(), input );
		return helper.prepareInput();
	}

	private List<HeapConfiguration> applyContract( HeapConfiguration remainingFragment,
			int contractPlaceholderEdge,
			List<HeapConfiguration> contracts ){

		List<HeapConfiguration> result = new ArrayList<>();
		for( HeapConfiguration contract : contracts ){
			HeapConfigurationBuilder builder = remainingFragment.clone().builder();
			builder.replaceNonterminalEdge(contractPlaceholderEdge, contract);
			result.add( builder.build() );
		}

		return result;
	}


	protected HeapConfiguration adaptExternalOrdering( HeapConfiguration reachableFragment, 
													   HeapConfiguration remainingFragment,
													   int placeholderPosition,
													   int[] reordering 
													  )
					throws IllegalArgumentException {

		TIntArrayList oldTentacles = remainingFragment.attachedNodesOf( placeholderPosition );
		TIntArrayList newTentacles = new TIntArrayList();
		for (int aReordering : reordering) {
			newTentacles.add(oldTentacles.get(aReordering));
		} 

		Nonterminal label = remainingFragment.labelOf(placeholderPosition);
		return remainingFragment.builder().removeNonterminalEdge(placeholderPosition)
				.addNonterminalEdge(label, newTentacles ).build();

	}

	public IpaContractCollection getContracts() {
		return contracts;
	}




}
