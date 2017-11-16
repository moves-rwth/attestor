package de.rwth.i2.attestor.ipa;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsObserver;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;
import java.util.Map.Entry;

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
	public Set<ProgramState> getFinalStates(HeapConfiguration input) {
		return null; // TODO
	}

	public void addContracts( IpaPrecondition precondition, List<HeapConfiguration> postconditions ){
		if( ! contracts.hasPrecondition(precondition) ){
			contracts.addPrecondition(precondition);
		}
		List<HeapConfiguration> currentPostconditions = contracts.getContract(precondition.config).getValue();
		currentPostconditions.addAll( postconditions );
	}

	@Override
	public Set<ProgramState> getResult(ProgramState input, SemanticsObserver observer)
													throws StateSpaceGenerationAbortedException {

		Set<ProgramState> result = new HashSet<>();
		for( HeapConfiguration postConfig : getIPAResult( input, observer ) ){
			result.add( Settings.getInstance().factory().createProgramState( postConfig, 0 ) );
		}

		return result;
	}

	public List<HeapConfiguration> getIPAResult( ProgramState input, SemanticsObserver observer )
			throws StateSpaceGenerationAbortedException{

	    HeapConfiguration currentConfig = input.getHeap();
		Pair<HeapConfiguration, Pair<HeapConfiguration,Integer>> splitConfig = prepareInput( currentConfig );
		HeapConfiguration reachableFragment = splitConfig.first();
		HeapConfiguration remainingFragment = splitConfig.second().first();
		int placeholderPos = splitConfig.second().second();

		Entry<IpaPrecondition, List<HeapConfiguration>> contract = contracts.getContract( reachableFragment );
		if( contract == null ){ 
			
			computeContract(input, reachableFragment, observer);
			contract = contracts.getContract(reachableFragment);
		}else{
			IpaPrecondition precondition = contract.getKey();
			remainingFragment = adaptExternalOrdering( precondition, reachableFragment, 
					remainingFragment, placeholderPos);
		}

			List<HeapConfiguration> postconditions = contract.getValue();
			return applyContract(remainingFragment, placeholderPos, postconditions );
		
	}

	private void computeContract(ProgramState input, HeapConfiguration reachableFragment, SemanticsObserver observer)
			throws StateSpaceGenerationAbortedException {
		final IpaPrecondition precondition = new IpaPrecondition( reachableFragment );
		contracts.addPrecondition( precondition );

        ProgramState initialState = input.shallowCopyWithUpdateHeap(reachableFragment);
		StateSpace stateSpace = observer.generateStateSpace(method, initialState);
		//StateSpace stateSpace = factory.create(method, reachableFragment, 0);
		List<HeapConfiguration> postconditions = contracts.getContract( reachableFragment ).getValue();
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

	protected HeapConfiguration adaptExternalOrdering( IpaPrecondition precondition, 
			HeapConfiguration reachableFragment,
			HeapConfiguration remainingFragment,
			int placeholderPosition )
					throws IllegalArgumentException {

		int[] reordering = precondition.getReordering( reachableFragment );
		TIntArrayList oldTentacles = remainingFragment.attachedNodesOf( placeholderPosition );
		TIntArrayList newTentacles = new TIntArrayList();
		for (int aReordering : reordering) {
			newTentacles.add(oldTentacles.get(aReordering));
		} 

		Nonterminal label = remainingFragment.labelOf(placeholderPosition);
		return remainingFragment.builder().removeNonterminalEdge(placeholderPosition)
				.addNonterminalEdge(label, newTentacles ).build();

	}




}
