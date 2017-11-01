package de.rwth.i2.attestor.ipa;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.semantics.util.Constants;
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

	/**
	 * 
	 * @param input
	 * @return <reachableFragment,remainingFragment>
	 */
	protected Pair<HeapConfiguration, Pair<HeapConfiguration,Integer>> prepareInput( HeapConfiguration input ){

		HeapConfigurationBuilder reachableFragmentBuilder = new InternalHeapConfiguration().builder();
		HeapConfigurationBuilder remainingFragmentBuilder = input.clone().builder();
		TIntArrayList tentacles = new TIntArrayList();

		queue = new ArrayDeque<>();
		idMapping = new HashMap<>();
		visitedNonterminalEdges = new HashSet<>();
		this.input = input;

		findParameterNodes( input, reachableFragmentBuilder, remainingFragmentBuilder );
		computeReachableFragment(reachableFragmentBuilder, remainingFragmentBuilder, tentacles);
		computeCutpoints( reachableFragmentBuilder, remainingFragmentBuilder, tentacles );
		int idOfNonterminal = addIpaNonterminal( remainingFragmentBuilder, tentacles );

		HeapConfiguration reachableFragment = reachableFragmentBuilder.build();
		Pair<HeapConfiguration, Integer> remainingFragment = new Pair<>(remainingFragmentBuilder.build(), idOfNonterminal);
		return new Pair<>(reachableFragment, remainingFragment );
	}


	private int addIpaNonterminal(HeapConfigurationBuilder replaceBuilder, TIntArrayList tentacles) {
		final int rank = tentacles.size();
		final boolean[] isReductionTentacle = new boolean[rank];
		Nonterminal nt = BasicNonterminal.getNonterminal(displayName + rank, rank, isReductionTentacle);

		int idOfNonterminal = replaceBuilder.addNonterminalEdgeAndReturnId(nt, tentacles);

		return idOfNonterminal;
	}


	private void computeReachableFragment(HeapConfigurationBuilder reachableFragmentBuilder, HeapConfigurationBuilder replaceBuilder,
			TIntArrayList externals) {
		while( ! queue.isEmpty() ){

			int nodeId = queue.poll();

			handleNonterminalEdges(reachableFragmentBuilder, replaceBuilder, nodeId);		
			handleSelectorEdges(reachableFragmentBuilder, replaceBuilder, nodeId);

		}
	}



	private void findParameterNodes(HeapConfiguration input, HeapConfigurationBuilder reachableFragmentBuilder,
			HeapConfigurationBuilder replaceBuilder) {
		TIntArrayList variables = input.variableEdges();
		for( int i = 0; i < variables.size(); i++ ){
			final int variableEdge = variables.get(i);
			String variableName = input.nameOf( variableEdge );
			if( isParameter(variableName) ){
				replaceBuilder.removeVariableEdge(variableEdge );

				int targetedNode = input.targetOf( variableEdge );
				if( ! idMapping.containsKey(targetedNode ) ){

					queue.add( targetedNode );
					addNodeToReachableFragment( targetedNode, reachableFragmentBuilder, input );
				}

				reachableFragmentBuilder.addVariableEdge(variableName, idMapping.get(targetedNode));
			}else if( Constants.isConstant(variableName) ) {
				int targetedNode = input.targetOf( variableEdge );
				if( ! idMapping.containsKey(targetedNode ) ){
					addNodeToReachableFragment( targetedNode, reachableFragmentBuilder, input );
					
				}
				reachableFragmentBuilder.addVariableEdge(variableName, idMapping.get(targetedNode) );
				replaceBuilder.removeVariableEdge(variableEdge);
			}
		}
	}

	private boolean isParameter(String variableName) {
		return variableName.startsWith("@param") || variableName.equals("@this");
	}

	private void addNodeToReachableFragment(int targetedNode, HeapConfigurationBuilder reachableFragmentBuilder,
			HeapConfiguration input) {
		Type type = input.nodeTypeOf( targetedNode );
		TIntArrayList insertedNode = new TIntArrayList();
		reachableFragmentBuilder.addNodes(type, 1, insertedNode);
		idMapping.put(targetedNode, insertedNode.get(0) );
	}

	private void handleSelectorEdges(HeapConfigurationBuilder reachableFragmentBuilder, HeapConfigurationBuilder replaceBuilder,
			int nodeId) {
		for( SelectorLabel sel : input.selectorLabelsOf(nodeId) ){
			int successorNode = input.selectorTargetOf(nodeId, sel);
			if( ! idMapping.containsKey(successorNode) ){
				queue.add(successorNode );

				addNodeToReachableFragment(successorNode, reachableFragmentBuilder, input);
			}
			reachableFragmentBuilder.addSelector(idMapping.get(nodeId), sel, idMapping.get(successorNode));
			replaceBuilder.removeSelector(nodeId, sel);
		}
	}

	private void handleNonterminalEdges( HeapConfigurationBuilder reachableFragmentBuilder, 
			HeapConfigurationBuilder replaceBuilder,
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
				if( ! idMapping.containsKey(attachedNode) ){
					queue.add( attachedNode );

					addNodeToReachableFragment(attachedNode, reachableFragmentBuilder, input);
				}
				translatedAttachedNodes.add(idMapping.get(attachedNode));
			}
			reachableFragmentBuilder.addNonterminalEdge(input.labelOf(nonterminalEdge), translatedAttachedNodes);
			replaceBuilder.removeNonterminalEdge( nonterminalEdge );
			visitedNonterminalEdges.add( nonterminalEdge );
		}
	}


	private void computeCutpoints(HeapConfigurationBuilder reachableFragmentBuilder,
			HeapConfigurationBuilder remainingFragmentBuilder, TIntArrayList tentacles) {

		Deque<Integer> nodesReachableFromVariables = new ArrayDeque<>();
		Set<Integer> visited = new HashSet<>();
		//initialize with variables
		TIntArrayList variables = input.variableEdges();
		for( int i = 0; i < variables.size(); i++ ) {
			int variableEdge = variables.get(i);
			if( ! isParameter( input.nameOf(variableEdge)) ) {
				int referencedNode = input.targetOf(variableEdge);
				nodesReachableFromVariables.add( referencedNode );
			}
		}
		//initialize with externals
		TIntArrayList externals = input.externalNodes();
		for( int i = 0; i < externals.size(); i++ ) {
			nodesReachableFromVariables.add( externals.get(i) );		
		}

		while( ! nodesReachableFromVariables.isEmpty() ) {
			int n = nodesReachableFromVariables.pop();
			if( idMapping.containsKey(n) ) {
				if( ! tentacles.contains(n) ) {
					reachableFragmentBuilder.setExternal( idMapping.get(n) );
					tentacles.add( n );
				}
			} else {
				addNodesReachableThroughSelector( nodesReachableFromVariables, visited, n);
				addNodesReachableThroughNonterminalEdge( nodesReachableFromVariables, visited, n);
			}
		}

		Set<Integer> nodesInReachableFragment = idMapping.keySet();
		for( Integer n : nodesInReachableFragment ) {
			if( ! tentacles.contains( n ) ) {
				remainingFragmentBuilder.removeIsolatedNode(n);
			}
		}
	}

	private void addNodesReachableThroughSelector(Deque<Integer> nodesReachableFromVariables, Set<Integer> visited,
			int n) {
		TIntArrayList successors = input.successorNodesOf(n);
		for( int i = 0; i < successors.size(); i++ ){
			int reachableNode = successors.get(i);
			addReachableNode(nodesReachableFromVariables, reachableNode, visited );
		}
	}

	private void addNodesReachableThroughNonterminalEdge(Deque<Integer> nodesReachableFromVariables,
			Set<Integer> visited, int n) {
		TIntArrayList nonterminalEdges = input.attachedNonterminalEdgesOf(n);
		for( int edgeIndex = 0; edgeIndex < nonterminalEdges.size(); edgeIndex++) {

			TIntArrayList tentaclesOfEdge = input.attachedNodesOf( nonterminalEdges.get(edgeIndex) );
			for( int i = 0; i < tentaclesOfEdge.size(); i++ ) {
				int reachableNode = tentaclesOfEdge.get(i);
				if( reachableNode != n ) {
					addReachableNode(nodesReachableFromVariables, reachableNode, visited);
				}
			}
		}
	}

	private void addReachableNode(Deque<Integer> nodesReachableFromVariables, int reachableNode, Set<Integer> visited ) {
		if( ! visited.contains(reachableNode) ) {
			nodesReachableFromVariables.add( reachableNode );
			visited.add( reachableNode );
		}
	}
}
