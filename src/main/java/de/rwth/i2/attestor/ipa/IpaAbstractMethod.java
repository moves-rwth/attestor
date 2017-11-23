package de.rwth.i2.attestor.ipa;


import java.util.*;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
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
		contracts.addPostconditionsTp(precondition, postconditions);
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

		List<HeapConfiguration> postconditions;
		if( !contracts.hasMatchingPrecondition(reachableFragment) || !isReuseResultsEnabled() ){ 

			postconditions = computeContract( input, reachableFragment, observer );

		}else{
			int [] reordering = contracts.getReordering( reachableFragment );
			remainingFragment = adaptExternalOrdering( reachableFragment, remainingFragment, 
					placeholderPos, reordering );
			postconditions = contracts.getPostconditions(reachableFragment);
		}
		return applyContract(remainingFragment, placeholderPos, postconditions );

	}

	private List<HeapConfiguration> computeContract(ProgramState input, HeapConfiguration reachableFragment, SymbolicExecutionObserver observer)
			throws StateSpaceGenerationAbortedException {

		List<HeapConfiguration> postconditions = new ArrayList<>();
		ProgramState initialState = input.shallowCopyWithUpdateHeap(reachableFragment);
		StateSpace stateSpace = observer.generateStateSpace(method, initialState);

		for( ProgramState finalState : stateSpace.getFinalStates() ){
			postconditions.add( finalState.getHeap() );
		}

		if( isReuseResultsEnabled() ) {
			contracts.addContract(reachableFragment, postconditions);
		}

		return postconditions;
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
