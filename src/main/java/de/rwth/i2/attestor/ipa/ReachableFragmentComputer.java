package de.rwth.i2.attestor.ipa;

import java.util.*;

import de.rwth.i2.attestor.graph.*;
import de.rwth.i2.attestor.graph.heap.*;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

public class ReachableFragmentComputer {

	
	private String displayName;
	
	Queue<Integer> queue;
	Map<Integer, Integer> idMapping;
	Set<Integer> visitedNonterminalEdges;
	
	HeapConfiguration input;
	

	public ReachableFragmentComputer(String displayName) {
		super();
		this.displayName = displayName;
	}

	protected Pair<HeapConfiguration, HeapConfiguration> prepareInput( HeapConfiguration input ){
		
		HeapConfigurationBuilder reachableFragmentBuilder = new InternalHeapConfiguration().builder();
		HeapConfigurationBuilder replaceBuilder = input.clone().builder();
		TIntArrayList externals = new TIntArrayList();
		
		queue = new ArrayDeque<>();
		idMapping = new HashMap<>();
		visitedNonterminalEdges = new HashSet<>();
		this.input = input;
		
		findParameterNodes(input, reachableFragmentBuilder, replaceBuilder);
		computeReachableFragment(reachableFragmentBuilder, replaceBuilder, externals);
		addIpaNonterminal(replaceBuilder, externals);
		
		return new Pair<HeapConfiguration, HeapConfiguration>(reachableFragmentBuilder.build(), replaceBuilder.build() );
	}

	private void addIpaNonterminal(HeapConfigurationBuilder replaceBuilder, TIntArrayList externals) {
		final int rank = externals.size();
		final boolean[] isReductionTentacle = new boolean[rank];
		Nonterminal nt = BasicNonterminal.getNonterminal(displayName + rank, rank, isReductionTentacle);
		
		NonterminalEdgeBuilder edgeBuilder = replaceBuilder.addNonterminalEdge(nt);
		for( int i = 0; i < externals.size(); i++ ){
			edgeBuilder.addTentacle(externals.get(i) );
		}
		edgeBuilder.build();
	}


	private void computeReachableFragment(HeapConfigurationBuilder reachableFragmentBuilder, HeapConfigurationBuilder replaceBuilder,
			TIntArrayList externals) {
		while( ! queue.isEmpty() ){
			
			int nodeId = queue.poll();
			
			handleNonterminalEdges(input, reachableFragmentBuilder, replaceBuilder, queue, idMapping, nodeId);		
			handleSelectorEdges(input, reachableFragmentBuilder, replaceBuilder, queue, idMapping, nodeId);
			
			if( ! input.attachedVariablesOf(nodeId).isEmpty() || input.isExternalNode(nodeId) ){
				reachableFragmentBuilder.setExternal( idMapping.get(nodeId ) );
				externals.add(nodeId);
			}else{
				replaceBuilder.removeIsolatedNode(nodeId);
			}

		}
	}


	private void findParameterNodes(HeapConfiguration input, HeapConfigurationBuilder reachableFragmentBuilder,
			HeapConfigurationBuilder replaceBuilder) {
		TIntArrayList variables = input.variableEdges();
		for( int i = 0; i < variables.size(); i++ ){
			final int variableEdge = variables.get(i);
			String variableName = input.nameOf( variableEdge );
			if( variableName.startsWith("@param") || variableName.equals("@this") ){
				replaceBuilder.removeVariableEdge(variableEdge );
				
				int targetedNode = input.targetOf( variableEdge );
				if( ! idMapping.containsKey(targetedNode ) ){

					queue.add( targetedNode );
					addNodeToReachableFragment( targetedNode, reachableFragmentBuilder, input, idMapping );
				}
				
				reachableFragmentBuilder.addVariableEdge(variableName, idMapping.get(targetedNode));
			}
		}
	}

	private void addNodeToReachableFragment(int targetedNode, HeapConfigurationBuilder reachableFragmentBuilder,
			HeapConfiguration input, Map<Integer, Integer> idMapping) {
		Type type = input.nodeTypeOf( targetedNode );
		TIntArrayList insertedNode = new TIntArrayList();
		reachableFragmentBuilder.addNodes(type, 1, insertedNode);
		idMapping.put(targetedNode, insertedNode.get(0) );
	}

	private void handleSelectorEdges(HeapConfiguration input, HeapConfigurationBuilder reachableFragmentBuilder,
			HeapConfigurationBuilder replaceBuilder, Queue<Integer> queue, Map<Integer, Integer> idOfInsertedNode,
			int nodeId) {
		for( SelectorLabel sel : input.selectorLabelsOf(nodeId) ){
			int successorNode = input.selectorTargetOf(nodeId, sel);
			if( ! idOfInsertedNode.containsKey(successorNode) ){
				queue.add(successorNode );

				addNodeToReachableFragment(successorNode, reachableFragmentBuilder, input, idOfInsertedNode);
			}
			reachableFragmentBuilder.addSelector(idOfInsertedNode.get(nodeId), sel, idOfInsertedNode.get(successorNode));
			replaceBuilder.removeSelector(nodeId, sel);
		}
	}

	private void handleNonterminalEdges(HeapConfiguration input, HeapConfigurationBuilder reachableFragmentBuilder,
			HeapConfigurationBuilder replaceBuilder, Queue<Integer> queue, Map<Integer, Integer> idOfInsertedNode,
			int nodeId) {
		TIntArrayList nonterminalEdges = input.attachedNonterminalEdgesOf(nodeId);
		for( int i = 0; i < nonterminalEdges.size(); i++ ){
			int nonterminalEdge = nonterminalEdges.get(i);
			if( visitedNonterminalEdges.contains(nonterminalEdge)){
				continue; //each nonterminalEdge should only be considered once.
			}
			TIntArrayList attachedNodes = input.attachedNodesOf(nonterminalEdge);
			TIntArrayList translatedAttachedNodes = new TIntArrayList();
			for( int n = 0; n < attachedNodes.size(); n++ ){
				int attachedNode = attachedNodes.get( n );
				if( ! idOfInsertedNode.containsKey(attachedNode) ){
					queue.add( attachedNode );
					
					addNodeToReachableFragment(attachedNode, reachableFragmentBuilder, input, idOfInsertedNode);
				}
				translatedAttachedNodes.add(idOfInsertedNode.get(attachedNode));
			}
			reachableFragmentBuilder.addNonterminalEdge(input.labelOf(nonterminalEdge), translatedAttachedNodes);
			replaceBuilder.removeNonterminalEdge( nonterminalEdge );
			visitedNonterminalEdges.add( nonterminalEdge );
		}
	}
}
