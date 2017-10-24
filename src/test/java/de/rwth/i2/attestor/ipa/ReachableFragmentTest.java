package de.rwth.i2.attestor.ipa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.rwth.i2.attestor.graph.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

public class ReachableFragmentTest {

	AbstractMethodIPA ipa = new AbstractMethodIPA( "testMethod", null );
	Type type = Settings.getInstance().factory().getType("someType");
	SelectorLabel nextLabel = BasicSelectorLabel.getSelectorLabel("next");


	@Test
	public void testPrepareInput_Simple_param() {
		HeapConfiguration input = singleNodeHeap("@parameter0:");
		HeapConfiguration expectedFragment = singleNodeExternal("@parameter0:");
		HeapConfiguration expectedReplace = singleNodeAttached();

		performTest(input, expectedFragment, expectedReplace);
	}

	@Test
	public void testPrepareInput_Simple_this(){
		HeapConfiguration input = singleNodeHeap("@this");
		HeapConfiguration expectedFragment = singleNodeExternal("@this");
		HeapConfiguration expectedReplace = singleNodeAttached();

		performTest(input, expectedFragment, expectedReplace);
	}

	@Test
	public void testPrepareInput_reachableList(){
		for( int size = 2; size < 4; size++ ){
			HeapConfiguration input = reachableList( size );
			HeapConfiguration expectedFragment = reachableList_HeadExternal( size );
			HeapConfiguration expectedReplace = singleNodeAttached();

			performTest( input, expectedFragment, expectedReplace );
		}

	}

	@Test
	public void testPrepareInput_reachableList_withVariable(){
		int listSize = 4;
		int variablePosition = 2;
		String variableName = "n1";
		HeapConfiguration input = reachableListWithVariable( listSize, variablePosition, variableName );
		HeapConfiguration expectedFragment = reachableList_HeadExternal_VarExternal( listSize, variablePosition );
		HeapConfiguration expectedReplace = twoNodesAttached( variableName );

		performTest( input, expectedFragment, expectedReplace );
	}
	
	@Test
	public void testPrepareInput_reachableList_withVariablesToSamePosition(){
		int listSize = 4;
		int variablePosition = 2;
		String variableName = "n1";
		String variableName2 = "n2";
		HeapConfiguration input = reachableListWithVariables_samePosition( listSize, variablePosition, variableName, variableName2 );
		HeapConfiguration expectedFragment = reachableList_HeadExternal_VarExternal( listSize, variablePosition );
		HeapConfiguration expectedReplace = twoNodesAttached( variableName, variableName2 );

		performTest( input, expectedFragment, expectedReplace );
	}
	
	@Test
	public void testPrepareInput_reachableList_withVariableAndParamToSamePosition(){
		int listSize = 4;
		int variablePosition = 2;
		String variableName = "n1";
		String variableName2 = "@parameter1:";
		HeapConfiguration input = reachableListWithVariables_samePosition( listSize, variablePosition, variableName, variableName2 );
		HeapConfiguration expectedFragment = reachableList_HeadExternal_VarExternal_twoParams( listSize, variablePosition, variablePosition );
		HeapConfiguration expectedReplace = twoNodesAttached( variableName );

		performTest( input, expectedFragment, expectedReplace );
	}

	@Test
	public void testPrepareInput_partlyReachableList(){
		for( int reachableListSize = 1; reachableListSize < 4; reachableListSize++ ){
			for( int unreachableListSize = 1; unreachableListSize < 4; unreachableListSize++ ){

				HeapConfiguration input = partlyReachableList( reachableListSize, unreachableListSize );
				HeapConfiguration expectedFragment = reachableList_HeadExternal( reachableListSize );
				HeapConfiguration expectedReplace = attachedToUnreachableList( unreachableListSize );

				performTest( input, expectedFragment, expectedReplace );
			}
		}
	}
	
	@Test
	public void testPrepareInput_unreachableVariable(){
		int reachableListSize = 1;
		int unreachableListSize = 1;
		String variableName = "n1";
		int variablePosition = 0;
		HeapConfiguration input = partlyReachableList_withVariable( reachableListSize, unreachableListSize,
																	variableName, variablePosition );
		HeapConfiguration expectedFragment = reachableList_HeadExternal( reachableListSize );
		HeapConfiguration expectedReplace = attachedToUnreachableList_withVariable( unreachableListSize,
																	variableName, variablePosition);

		performTest( input, expectedFragment, expectedReplace );
	}

	@Test
	public void testPrepareInput_reachableFragmentWithNonterminalEdge(){
		HeapConfiguration input = reachableNodeThroughNonterminal();
		HeapConfiguration expectedFragment = reachableNodeThroughNonterminal_HeadExternal();
		HeapConfiguration expectedReplace = singleNodeAttached();
		
		performTest( input, expectedFragment, expectedReplace );
	}
	
	@Test
	public void testPrepareInput_WithExternalsInInput(){
		int listSize = 3;
		int externalPosition = 2;
		HeapConfiguration input = reachableList_ExternalNode( listSize, externalPosition );
		HeapConfiguration expectedFragment = reachableList_HeadExternal_VarExternal(listSize, externalPosition);
		HeapConfiguration expectedReplace = twoNodesAttached_SecondExternal();
		
		performTest( input, expectedFragment, expectedReplace );
	}


	private void performTest(HeapConfiguration input, HeapConfiguration expectedFragment,
			HeapConfiguration expectedReplace) {
		Pair<HeapConfiguration, HeapConfiguration> result = ipa.prepareInput( input );
		assertEquals("reachable Fragment", expectedFragment, result.first());
		assertEquals("replaced Fragment", expectedReplace, result.second());
	}
	
	private HeapConfiguration reachableNodeThroughNonterminal() {
		TIntArrayList nodes = new TIntArrayList();
		return reachableNodeThroughNonterminalHelper(nodes);
	}

	private HeapConfiguration reachableNodeThroughNonterminal_HeadExternal() {
		TIntArrayList nodes = new TIntArrayList();
		return reachableNodeThroughNonterminalHelper(nodes).builder().setExternal(nodes.get(0)).build();
	}

	private HeapConfiguration reachableNodeThroughNonterminalHelper(TIntArrayList nodes) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("someType");
		final int rank = 2;
		final boolean[] isReductionTentacle = new boolean[rank];
		Nonterminal nt = BasicNonterminal.getNonterminal("AbstractMethodIpaTest", rank, isReductionTentacle);

		return hc.builder().addNodes(type, rank, nodes)
				.addVariableEdge("@this", nodes.get(0))
				.addNonterminalEdge(nt)
				.addTentacle(nodes.get(0))
				.addTentacle(nodes.get(1))
				.build()
				.build();
	}

	private HeapConfiguration partlyReachableList(int reachableListSize, int unreachableListSize) {
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration config = reachableListHelper( reachableListSize , nodes);
		return addUnreachableList( unreachableListSize, config, nodes );
	}
	
	private HeapConfiguration partlyReachableList_withVariable(int reachableListSize, int unreachableListSize,
			String variableName, int variablePosition) {
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration config = reachableListHelper( reachableListSize , nodes);
		return addUnreachableListWithVariable( unreachableListSize, config, nodes, variableName, variablePosition );
	}

	private HeapConfiguration attachedToUnreachableList(int unreachableListSize) {
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration config = singleNodeAttachedHelper( nodes );
		return addUnreachableList(unreachableListSize, config, nodes );
	}
	
	private HeapConfiguration attachedToUnreachableList_withVariable(int unreachableListSize, String variableName, int variablePosition){
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration config = singleNodeAttachedHelper( nodes );
		return  addUnreachableListWithVariable( unreachableListSize, config, nodes, variableName, variablePosition );
	}

	private HeapConfiguration addUnreachableListWithVariable( int unreachableListSize, 
												HeapConfiguration config, TIntArrayList oldNodes,
												String variableName, int variablePosition ) {
		
		TIntArrayList additionalNodes = new TIntArrayList();
		HeapConfigurationBuilder builder = 
				addUnreachableListHelper(unreachableListSize, config, oldNodes, additionalNodes)
				.builder();
		builder.addVariableEdge(variableName, additionalNodes.get(variablePosition) );
		return builder.build();
	}

	
	private HeapConfiguration addUnreachableList(int unreachableListSize, HeapConfiguration config, TIntArrayList oldNodes) {
		TIntArrayList additionalNodes = new TIntArrayList();
		return addUnreachableListHelper(unreachableListSize, config, oldNodes, additionalNodes);
	}

	private HeapConfiguration addUnreachableListHelper(int unreachableListSize, HeapConfiguration config,
			TIntArrayList oldNodes, TIntArrayList additionalNodes) {
		HeapConfigurationBuilder builder = config.builder();
		builder.addNodes(type, unreachableListSize, additionalNodes);

		for( int i = 0; i < unreachableListSize -1; i++ ){
			builder.addSelector(additionalNodes.get(i), nextLabel, additionalNodes.get(i +1 ) );
		}

		builder.addSelector(additionalNodes.get(unreachableListSize - 1), nextLabel, oldNodes.get(0) );
		return builder.build();
	}
	
	private HeapConfiguration reachableListWithVariables_samePosition(int listSize, int variablePosition,
			String variableName, String variableName2) {
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration config = reachableListHelper( listSize , nodes);
		return config.builder()
				.addVariableEdge(variableName, nodes.get(variablePosition))
				.addVariableEdge(variableName2, nodes.get(variablePosition))
				.build();
	}
	

	private HeapConfiguration reachableListWithVariable(int listSize, int variablePosition, String variableName) {
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration config = reachableListHelper( listSize , nodes);
		return config.builder().addVariableEdge(variableName, nodes.get(variablePosition)).build();
	}
	


	private HeapConfiguration reachableList_HeadExternal_VarExternal_twoParams(int listSize, int variablePosition,
			int parameterPosition ) {
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration config = reachableListHelper( listSize , nodes);
		return config.builder()
				.setExternal(nodes.get(0)).setExternal(nodes.get(variablePosition))
				.addVariableEdge("@parameter1:", parameterPosition )
				.build();
	}


	private HeapConfiguration reachableList_HeadExternal_VarExternal(int listSize, int variablePosition) {
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration config = reachableListHelper( listSize , nodes);
		return config.builder().setExternal(nodes.get(0)).setExternal(nodes.get(variablePosition)).build();
	}
	
	private HeapConfiguration reachableList_ExternalNode(int listSize, int externalPosition) {
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration config = reachableListHelper( listSize , nodes);
		return config.builder().setExternal(nodes.get(externalPosition)).build();
	}

	private HeapConfiguration reachableList( int size ) {
		TIntArrayList nodes = new TIntArrayList();
		return reachableListHelper(size, nodes);
	}

	private HeapConfiguration reachableList_HeadExternal(int size) {
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration config = reachableListHelper(size, nodes);
		return config.builder().setExternal(nodes.get(0) ).build();
	}


	private HeapConfiguration reachableListHelper( int size, TIntArrayList nodes) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		HeapConfigurationBuilder builder =  hc.builder().addNodes(type, size, nodes)
				.addVariableEdge("@parameter0:", nodes.get(0) );
		for( int i = 0; i < size - 1; i++ ){
			builder.addSelector(nodes.get(i), nextLabel, nodes.get(i + 1) );
		}

		return builder.build();
	}

	private HeapConfiguration singleNodeAttached() {
		TIntArrayList nodes = new TIntArrayList();
		return singleNodeAttachedHelper(nodes);
	}

	private HeapConfiguration singleNodeAttachedHelper(TIntArrayList nodes) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		final int rank = 1;
		final boolean[] isReductionTentacle = new boolean[]{false};
		Nonterminal nt = BasicNonterminal.getNonterminal(ipa.toString() + rank, rank, isReductionTentacle);


		return hc.builder().addNodes(type, rank, nodes)
				.addNonterminalEdge(nt)
				.addTentacle(nodes.get(0))
				.build()
				.build();
	}


	private HeapConfiguration twoNodesAttached_SecondExternal() {
	TIntArrayList nodes = new TIntArrayList();
		
		return twoNodesAttachedHelper( nodes ).builder()
				.setExternal( nodes.get(1) ).build();
	}
	
	private HeapConfiguration twoNodesAttached(String variableName, String variableName2) {
		TIntArrayList nodes = new TIntArrayList();
		
		return twoNodesAttachedHelper( nodes ).builder()
				.addVariableEdge( variableName, nodes.get(1) )
				.addVariableEdge(variableName2, nodes.get(1))
				.build();
	}


	private HeapConfiguration twoNodesAttached( String variableName ) {
		TIntArrayList nodes = new TIntArrayList();
		
		return twoNodesAttachedHelper( nodes ).builder()
				.addVariableEdge( variableName, nodes.get(1) ).build();
	}

	private HeapConfiguration twoNodesAttachedHelper( TIntArrayList nodes) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("someType");
		final int rank = 2;
		final boolean[] isReductionTentacle = new boolean[rank];
		Nonterminal nt = BasicNonterminal.getNonterminal(ipa.toString() + rank, rank, isReductionTentacle);

		
		return hc.builder().addNodes(type, rank, nodes)
				.addNonterminalEdge(nt)
				.addTentacle(nodes.get(0))
				.addTentacle(nodes.get(1))
				.build()
				.build();
	}
	


	private HeapConfiguration singleNodeHeap(String variableName) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("someType");

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 1, nodes)
				.addVariableEdge(variableName, nodes.get(0) )
				.build();
	}


	private HeapConfiguration singleNodeExternal(String string) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("someType");

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 1, nodes)
				.setExternal(nodes.get(0))
				.addVariableEdge(string, nodes.get(0) )
				.build();
	}




}
