package de.rwth.i2.attestor.ipa;

import java.util.*;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.*;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

public class AbstractMethodIPA extends AbstractMethod {

	public AbstractMethodIPA(String displayName, StateSpaceFactory factory) {
		super(displayName, factory);
	}

	@Override
	public Set<ProgramState> getResult(HeapConfiguration input, int scopeDepth) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected Pair<HeapConfiguration, HeapConfiguration> prepareInput( HeapConfiguration input ){
		
		HeapConfigurationBuilder reachableFragmentBuilder = new InternalHeapConfiguration().builder();
		HeapConfigurationBuilder replaceBuilder = input.clone().builder();
		TIntArrayList externals = new TIntArrayList();
		
		Queue<Integer> queue = new ArrayDeque<>();
		Map<Integer, Integer> idOfInsertedNode = new HashMap<>();
		
		TIntArrayList variables = input.variableEdges();
		for( int i = 0; i < variables.size(); i++ ){
			String name = input.nameOf( variables.get(i) );
			if( name.startsWith("@param") ){
				replaceBuilder.removeVariableEdge(variables.get(i) );
				
				int targetedNode = input.targetOf( variables.get(i) );
				if( ! idOfInsertedNode.containsKey(targetedNode ) ){

					queue.add( targetedNode );
					
					Type type = input.nodeTypeOf( targetedNode );
					TIntArrayList insertedNode = new TIntArrayList();
					reachableFragmentBuilder.addNodes(type, 1, insertedNode);
					idOfInsertedNode.put(targetedNode, insertedNode.get(0) );
				}
			}
		}
		
		while( ! queue.isEmpty() ){
			
			int nodeId = queue.poll();
			handleNonterminalEdges(input, reachableFragmentBuilder, replaceBuilder, queue, idOfInsertedNode, nodeId);
			
			TIntArrayList successorNodes = input.successorNodesOf(nodeId);
			for( int s = 0; s < successorNodes.size(); s++ ){
				int successorNode = successorNodes.get(s);
				if( ! idOfInsertedNode.containsKey(successorNode) ){
					queue.add(successorNode );
					
					Type type = input.nodeTypeOf( successorNode );
					TIntArrayList insertedNode = new TIntArrayList();
					reachableFragmentBuilder.addNodes(type, 1, insertedNode);
					idOfInsertedNode.put(successorNode, insertedNode.get(0) );
				}
				
				replaceBuilder.removeSelector(nodeId, sel)//TODO maybe have to iterate over selectors instead.
			}
			
			if( ! input.attachedVariablesOf(nodeId).isEmpty() ){
				reachableFragmentBuilder.setExternal( idOfInsertedNode.get(nodeId ) );
			}else{
			}
			
//			reachableFragmentBuilder.setExternal( nodes.get(i) );
//			externals.add( nodes.get(i) );
		}
		
		final int rank = externals.size();
		final boolean[] isReductionTentacle = new boolean[rank];
		Nonterminal nt = BasicNonterminal.getNonterminal(displayName + rank, rank, isReductionTentacle);
		
		NonterminalEdgeBuilder edgeBuilder = replaceBuilder.addNonterminalEdge(nt);
		for( int i = 0; i < externals.size(); i++ ){
			edgeBuilder.addTentacle(externals.get(i) );
		}
		edgeBuilder.build();
		return new Pair<HeapConfiguration, HeapConfiguration>(reachableFragmentBuilder.build(), replaceBuilder.build() );
	}

	private void handleNonterminalEdges(HeapConfiguration input, HeapConfigurationBuilder reachableFragmentBuilder,
			HeapConfigurationBuilder replaceBuilder, Queue<Integer> queue, Map<Integer, Integer> idOfInsertedNode,
			int nodeId) {
		TIntArrayList nonterminalEdges = input.attachedNonterminalEdgesOf(nodeId);
		for( int i = 0; i < nonterminalEdges.size(); i++ ){
			int nonterminalEdge = nonterminalEdges.get(i);
			TIntArrayList attachedNodes = input.attachedNodesOf(nonterminalEdge);
			for( int n = 0; n < attachedNodes.size(); n++ ){
				int attachedNode = attachedNodes.get( n );
				if( ! idOfInsertedNode.containsKey(attachedNode) ){
					queue.add( attachedNode );
					
					Type type = input.nodeTypeOf( attachedNode );
					TIntArrayList insertedNode = new TIntArrayList();
					reachableFragmentBuilder.addNodes(type, 1, insertedNode);
					idOfInsertedNode.put(attachedNode, insertedNode.get(0) );
				}
			}
			replaceBuilder.removeNonterminalEdge( nonterminalEdge );
		}
	}

}
