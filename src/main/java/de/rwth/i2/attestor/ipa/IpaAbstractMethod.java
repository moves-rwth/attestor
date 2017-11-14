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

	IpaContractCollection contracts = new IpaContractCollection();

	public IpaAbstractMethod(String displayName) {
		super();
		super.setDisplayName(displayName);
	}
	
	public void addContracts( HeapConfiguration precondition, List<HeapConfiguration> postconditions ){
		if( ! contracts.hasPrecondition(precondition) ){
			contracts.addPrecondition(precondition);
		}
		List<HeapConfiguration> currentPostconditions = contracts.getPostconditions( precondition );
		currentPostconditions.addAll( postconditions );
	}

	@Override
	public Set<ProgramState> getResult(HeapConfiguration input, int scopeDepth) 
													throws StateSpaceGenerationAbortedException {

		Set<ProgramState> result = new HashSet<>();
		for( HeapConfiguration postConfig : getResult( input ) ){
			result.add( Settings.getInstance().factory().createProgramState( postConfig, 0 ) );
		}

		return result;
	}

	public List<HeapConfiguration> getResult( HeapConfiguration currentConfig ) 
			throws StateSpaceGenerationAbortedException{

		Pair<HeapConfiguration, Pair<HeapConfiguration,Integer>> splittedConfig = prepareInput( currentConfig );
		HeapConfiguration reachableFragment = splittedConfig.first();
		HeapConfiguration remainingFragment = splittedConfig.second().first(); 
		int placeholderPos = splittedConfig.second().second();
		
		if( !contracts.hasPrecondition(reachableFragment) ){ 
			
			computeContract(reachableFragment);
		}else{
			int [] reordering = contracts.getReordering( reachableFragment );
			remainingFragment = adaptExternalOrdering( reachableFragment, remainingFragment, 
														placeholderPos, reordering );
		}
		
		List<HeapConfiguration> postconditions = contracts.getPostconditions(reachableFragment);
		return applyContract(remainingFragment, placeholderPos, postconditions );
		
	}

	private void computeContract(HeapConfiguration reachableFragment) throws StateSpaceGenerationAbortedException {
		
		contracts.addPrecondition( reachableFragment );
		StateSpace stateSpace = factory.create(method, reachableFragment, 0);
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
	 * @param methodName
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
		for( int i = 0; i < reordering.length; i++ ){
			newTentacles.add( oldTentacles.get( reordering[i]) );
		} 

		Nonterminal label = remainingFragment.labelOf(placeholderPosition);
		return remainingFragment.builder().removeNonterminalEdge(placeholderPosition)
				.addNonterminalEdge(label, newTentacles ).build();

	}




}
