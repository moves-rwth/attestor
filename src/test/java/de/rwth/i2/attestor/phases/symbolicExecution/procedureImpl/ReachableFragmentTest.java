package de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes.DefaultScopeExtractor;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes.InternalScopedHeap;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.ContractMatch;
import de.rwth.i2.attestor.procedures.ScopeExtractor;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ReachableFragmentTest {

    SceneObject sceneObject = new MockupSceneObject();

    ScopeExtractor scopeExtractor = new DefaultScopeExtractor(sceneObject, "testMethod");
    String methodName = "testMethod";


    Type type = sceneObject.scene().getType("someType");
    SelectorLabel nextLabel = sceneObject.scene().getSelectorLabel("next");


    @Test
    public void testPrepareInput_Simple_param() {

        String parameterName = "@parameter0:";
        String variableName = "x";
        HeapConfiguration input = singleNodeHeap(parameterName, variableName);
        HeapConfiguration expectedFragment = singleNodeExternal(parameterName);
        HeapConfiguration expectedReplace = singleNodeAttached(variableName);

        performTest(input, expectedFragment, expectedReplace);
    }

    @Test
    public void testPrepareInput_Simple_this() {

        String parameterName = "@this";
        String variableName = "x";
        HeapConfiguration input = singleNodeHeap(parameterName, variableName);
        HeapConfiguration expectedFragment = singleNodeExternal(parameterName);
        HeapConfiguration expectedReplace = singleNodeAttached(variableName);

        performTest(input, expectedFragment, expectedReplace);
    }

    @Test
    public void testPrepareInput_reachableList() {

        for (int size = 2; size < 4; size++) {
            String variableName = "x";
            HeapConfiguration input = reachableList(size, variableName);
            HeapConfiguration expectedFragment = reachableList_HeadExternal(size);
            HeapConfiguration expectedReplace = singleNodeAttached(variableName);

            performTest(input, expectedFragment, expectedReplace);
        }

    }


    @Test
    public void testPrepareInput_reachableList_withVariable() {

        int listSize = 4;
        int variablePosition = 2;
        String variableName = "n1";
        HeapConfiguration input = reachableListWithVariable(listSize, variablePosition, variableName);
        HeapConfiguration expectedFragment = reachableList_VarExternal(listSize, variablePosition);
        HeapConfiguration expectedReplace = singleNodeAttached(variableName);

        performTest(input, expectedFragment, expectedReplace);
    }

    @Test
    public void testPrepareInput_reachableList_withVariablesToSamePosition() {

        int listSize = 4;
        int variablePosition = 2;
        String variableName = "n1";
        String variableName2 = "n2";
        HeapConfiguration input = reachableListWithVariables_samePosition(listSize, variablePosition, variableName, variableName2);
        HeapConfiguration expectedFragment = reachableList_VarExternal(listSize, variablePosition);
        HeapConfiguration expectedReplace = singleNodeAttached(variableName, variableName2);

        performTest(input, expectedFragment, expectedReplace);
    }

    @Test
    public void testPrepareInput_reachableList_withVariableAndParamToSamePosition() {

        int listSize = 4;
        int variablePosition = 2;
        String variableName = "n1";
        String variableName2 = "@parameter1:";
        HeapConfiguration input = reachableListWithVariables_samePosition(listSize, variablePosition, variableName, variableName2);
        HeapConfiguration expectedFragment = reachableList_HeadExternal_VarExternal_twoParams(listSize, variablePosition, variablePosition);
        HeapConfiguration expectedReplace = singleNodeAttached(variableName);

        performTest(input, expectedFragment, expectedReplace);
    }

    @Test
    public void testPrepareInput_partlyReachableList() {

        for (int reachableListSize = 1; reachableListSize < 4; reachableListSize++) {
            for (int unreachableListSize = 1; unreachableListSize < 4; unreachableListSize++) {

                String variableName = "x";
                HeapConfiguration input = partlyReachableList(reachableListSize, unreachableListSize, variableName);
                HeapConfiguration expectedFragment = reachableList_HeadExternal(reachableListSize);
                HeapConfiguration expectedReplace = attachedToUnreachableList(unreachableListSize, variableName);

                performTest(input, expectedFragment, expectedReplace);
            }
        }
    }

    @Test
    public void testPrepareInput_unreachableVariable() {

        int reachableListSize = 1;
        int unreachableListSize = 1;
        String variableName = "n1";
        int variablePosition = 0;
        HeapConfiguration input = partlyReachableList_withVariable(reachableListSize, unreachableListSize,
                variableName, variablePosition);
        HeapConfiguration expectedFragment = reachableList_HeadExternal(reachableListSize);
        HeapConfiguration expectedReplace = attachedToUnreachableList_withVariable(unreachableListSize,
                variableName, variablePosition);

        performTest(input, expectedFragment, expectedReplace);
    }

    @Test
    public void testPrepareInput_reachableFragmentWithNonterminalEdge() {

        String variableName = "x";
        HeapConfiguration input = reachableNodeThroughNonterminal(variableName);
        HeapConfiguration expectedFragment = reachableNodeThroughNonterminal_HeadExternal();
        HeapConfiguration expectedReplace = singleNodeAttached(variableName);

        performTest(input, expectedFragment, expectedReplace);
    }

    @Test
    public void testPrepareInput_WithExternalsInInput() {

        int listSize = 3;
        int externalPosition = 2;
        HeapConfiguration input = reachableList_ExternalNode(listSize, externalPosition);
        HeapConfiguration expectedFragment = reachableList_VarExternal(listSize, externalPosition);
        HeapConfiguration expectedReplace = singleNodeAttached_external();

        performTest(input, expectedFragment, expectedReplace);
    }

    @Test
    public void testReachableConstantWithoutVariable() {

        HeapConfiguration input = reachable_nullTerminated_List();
        HeapConfiguration expectedFragment = reachable_nullTerminated_List_nullExternal();
        HeapConfiguration expectedReplace = attachedToNull();

        performTest(input, expectedFragment, expectedReplace);
    }

    //a cutpoint which is not directly accessed by a variable
    @Test
    public void testIndirectCutpoint() {

        String parameterName = "@this";
        String variableName = "x";
        HeapConfiguration input = nodeReachableFromTwoSides(parameterName, variableName);
        HeapConfiguration expectedFragment = parameterSide(parameterName);
        HeapConfiguration expectedReplace = ntAttachedToVariableSide(variableName);

        performTest(input, expectedFragment, expectedReplace);

    }
    
    /* 
     * a nonterminal where the other tentacles cannot be reached from
     * the reachable fragment
    */
    @Test
    public void testUnpassableNonterminalEdge() {
    	String parameterName = "@parameter0:";
        String variableName = "x";
    	HeapConfiguration input = unpassableNonterminal(parameterName, variableName);
    	HeapConfiguration expectedFragment = parameterSide( parameterName );
    	HeapConfiguration expectedReplace = unpassableNtAttachedTovariableSide( variableName );
    	
    	performTest( input, expectedFragment, expectedReplace );
    }



	/*
     * a nonterminal where some of the other tentacles can
     * and some cannot be reached from the reachable fragment
     */
    @Test
    public void testPartiallyPassableNonterminalEdge() {
    	fail("Not yet implemented");
    }
    
    /*
     * a nonterminal where not every tentacle can be reached from
     * every other. Embedded such, that its tentacles have to be
     * added to the reachable fragment one by one.
     */
    @Test
    public void testPartiallyPassableNonterminalEdge_ReachedFromDifferentPoints() {
    	fail("Not yet implemented");
    }

    private void performTest(HeapConfiguration input, HeapConfiguration expectedFragment,
                             HeapConfiguration expectedReplace) {

        InternalScopedHeap scopedHeap = (InternalScopedHeap) scopeExtractor.extractScope(input);

        final HeapConfiguration reachableFragment = scopedHeap.getHeapInScope();

        Contract dummyContract = new InternalContract(expectedFragment, Collections.emptySet());
        ContractCollection dummyCollection = new InternalContractCollection(new InternalPreconditionMatchingStrategy());
        dummyCollection.addContract(dummyContract);

        ContractMatch match = dummyCollection.matchContract(reachableFragment);

        assertTrue("reachableFragment", match.hasMatch());
        int[] reordering = match.getExternalReordering();

        HeapConfiguration remainingFragmentWithReorderedTentacles = scopedHeap.reorder(reordering);
        assertEquals("replaced Fragment", expectedReplace, remainingFragmentWithReorderedTentacles);
    }

    private HeapConfiguration reachableNodeThroughNonterminal(String variableName) {

        TIntArrayList nodes = new TIntArrayList();
        return reachableNodeThroughNonterminalHelper(nodes).builder()
                .addVariableEdge(variableName, nodes.get(0))
                .build();
    }

    private HeapConfiguration reachableNodeThroughNonterminal_HeadExternal() {

        TIntArrayList nodes = new TIntArrayList();
        return reachableNodeThroughNonterminalHelper(nodes).builder().setExternal(nodes.get(0)).build();
    }

    private HeapConfiguration reachableNodeThroughNonterminalHelper(TIntArrayList nodes) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = getSomeType();
        final int rank = 2;
        final boolean[] isReductionTentacle = new boolean[rank];
        Nonterminal nt = sceneObject.scene().createNonterminal("AbstractMethodIpaTest", rank, isReductionTentacle);

        return hc.builder().addNodes(type, rank, nodes)
                .addVariableEdge("@this", nodes.get(0))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .build();
    }
    
    private HeapConfiguration unpassableNonterminal(String parameterName, String variableName) {
		TIntArrayList nodes = new TIntArrayList();
		 HeapConfiguration hc = new InternalHeapConfiguration();
		 
		 hc.builder().addNodes(type, 2, nodes).build();
		 addParameterSide(hc, nodes.get(0), parameterName);
		 addVariableSide( hc, nodes.get(1), variableName );
		 addDirectedNonterminal( hc, nodes.get(1), nodes.get(0) );
		 
		 return hc;
	}
    

	private HeapConfiguration unpassableNtAttachedTovariableSide(String variableName) {
		TIntArrayList nodes = new TIntArrayList();
		 
		 HeapConfiguration hc = singleNodeAttachedHelper(nodes);
		 hc.builder().addNodes(type, 1, nodes).build();
		 addVariableSide(hc, nodes.get(1), variableName);
		 addDirectedNonterminal(hc, nodes.get(1), nodes.get(0));
		 hc.builder().setExternal(nodes.get(0)).build();
		 
		 return hc;
	}



	private void addDirectedNonterminal(HeapConfiguration hc, int from, int to) {
		Nonterminal nt = sceneObject.scene().createNonterminal("sll", 2, new boolean[] {false,false});
		Map<Integer,Collection<Integer>> reachabilityMap = new HashMap<>();
		reachabilityMap.put(0, SingleElementUtil.createSet(1));
		reachabilityMap.put(1, new HashSet<Integer>() );
		nt.setReachableTentacles(reachabilityMap);
		
		hc.builder().addNonterminalEdge(nt)
					.addTentacle(from)
					.addTentacle(to)
					.build();
		
	}

	private HeapConfiguration partlyReachableList(int reachableListSize, int unreachableListSize, String variableName) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration config = reachableListHelper(reachableListSize, nodes);
        return addUnreachableList(unreachableListSize, config, nodes, variableName);
    }

    private HeapConfiguration partlyReachableList_withVariable(int reachableListSize, int unreachableListSize,
                                                               String variableName, int variablePosition) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration config = reachableListHelper(reachableListSize, nodes);
        return addUnreachableListWithVariable(unreachableListSize, config, nodes, variableName, variablePosition);
    }

    private HeapConfiguration attachedToUnreachableList(int unreachableListSize, String variableName) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration config = singleNodeAttachedHelper(nodes);
        return addUnreachableList(unreachableListSize, config, nodes, variableName);
    }

    private HeapConfiguration attachedToUnreachableList_withVariable(int unreachableListSize, String variableName, int variablePosition) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration config = singleNodeAttachedHelper(nodes);
        return addUnreachableListWithVariable(unreachableListSize, config, nodes, variableName, variablePosition);
    }

    private HeapConfiguration addUnreachableListWithVariable(int unreachableListSize,
                                                             HeapConfiguration config, TIntArrayList oldNodes,
                                                             String variableName, int variablePosition) {

        TIntArrayList additionalNodes = new TIntArrayList();
        HeapConfigurationBuilder builder =
                addUnreachableListHelper(unreachableListSize, config, oldNodes, additionalNodes)
                        .builder();
        builder.addVariableEdge(variableName, additionalNodes.get(variablePosition));
        return builder.build();
    }


    private HeapConfiguration addUnreachableList(int unreachableListSize, HeapConfiguration config,
                                                 TIntArrayList oldNodes, String VariableName) {

        TIntArrayList additionalNodes = new TIntArrayList();
        return addUnreachableListHelper(unreachableListSize, config, oldNodes, additionalNodes)
                .builder()
                .addVariableEdge(VariableName, additionalNodes.get(0))
                .build();
    }

    private HeapConfiguration addUnreachableListHelper(int unreachableListSize, HeapConfiguration config,
                                                       TIntArrayList oldNodes, TIntArrayList additionalNodes) {

        HeapConfigurationBuilder builder = config.builder();
        builder.addNodes(type, unreachableListSize, additionalNodes);

        for (int i = 0; i < unreachableListSize - 1; i++) {
            builder.addSelector(additionalNodes.get(i), nextLabel, additionalNodes.get(i + 1));
        }

        builder.addSelector(additionalNodes.get(unreachableListSize - 1), nextLabel, oldNodes.get(0));
        return builder.build();
    }

    private HeapConfiguration reachableListWithVariables_samePosition(int listSize, int variablePosition,
                                                                      String variableName, String variableName2) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration config = reachableListHelper(listSize, nodes);
        return config.builder()
                .addVariableEdge(variableName, nodes.get(variablePosition))
                .addVariableEdge(variableName2, nodes.get(variablePosition))
                .build();
    }


    private HeapConfiguration reachableListWithVariable(int listSize, int variablePosition, String variableName) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration config = reachableListHelper(listSize, nodes);
        return config.builder().addVariableEdge(variableName, nodes.get(variablePosition)).build();
    }


    private HeapConfiguration reachableList_HeadExternal_VarExternal_twoParams(int listSize, int variablePosition,
                                                                               int parameterPosition) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration config = reachableListHelper(listSize, nodes);
        return config.builder()
                .setExternal(nodes.get(variablePosition))
                .addVariableEdge("@parameter1:", parameterPosition)
                .build();
    }


    private HeapConfiguration reachableList_VarExternal(int listSize, int variablePosition) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration config = reachableListHelper(listSize, nodes);
        return config.builder()
                .setExternal(nodes.get(variablePosition))
                .build();
    }

    private HeapConfiguration reachableList_ExternalNode(int listSize, int externalPosition) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration config = reachableListHelper(listSize, nodes);
        return config.builder().setExternal(nodes.get(externalPosition)).build();
    }

    private HeapConfiguration reachableList(int size, String variableName) {

        TIntArrayList nodes = new TIntArrayList();
        return reachableListHelper(size, nodes).builder()
                .addVariableEdge(variableName, nodes.get(0))
                .build();
    }

    private HeapConfiguration reachableList_HeadExternal(int size) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration config = reachableListHelper(size, nodes);
        return config.builder().setExternal(nodes.get(0)).build();
    }


    private HeapConfiguration reachableListHelper(int size, TIntArrayList nodes) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        HeapConfigurationBuilder builder = hc.builder().addNodes(type, size, nodes)
                .addVariableEdge("@parameter0:", nodes.get(0));
        for (int i = 0; i < size - 1; i++) {
            builder.addSelector(nodes.get(i), nextLabel, nodes.get(i + 1));
        }

        return builder.build();
    }

    private HeapConfiguration singleNodeAttached(String variableName) {

        TIntArrayList nodes = new TIntArrayList();
        return singleNodeAttachedHelper(nodes).builder()
                .addVariableEdge(variableName, nodes.get(0)).build();
    }

    private HeapConfiguration singleNodeAttachedHelper(TIntArrayList nodes) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        final int rank = 1;
        final boolean[] isReductionTentacle = new boolean[]{false};
        Nonterminal nt = sceneObject.scene().createNonterminal(methodName + rank, rank, isReductionTentacle);


        return hc.builder().addNodes(type, rank, nodes)
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .build()
                .build();
    }


    private HeapConfiguration singleNodeAttached_external() {

        TIntArrayList nodes = new TIntArrayList();

        return singleNodeAttachedHelper(nodes).builder()
                .setExternal(nodes.get(0)).build();
    }

    private HeapConfiguration singleNodeAttached(String variableName, String variableName2) {

        TIntArrayList nodes = new TIntArrayList();

        return singleNodeAttachedHelper(nodes).builder()
                .addVariableEdge(variableName, nodes.get(0))
                .addVariableEdge(variableName2, nodes.get(0))
                .build();
    }


    private HeapConfiguration singleNodeHeap(String parameterName, String variableName) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = getSomeType();

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 1, nodes)
                .addVariableEdge(parameterName, nodes.get(0))
                .addVariableEdge(variableName, nodes.get(0))
                .build();
    }


    private HeapConfiguration singleNodeExternal(String string) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = getSomeType();

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 1, nodes)
                .setExternal(nodes.get(0))
                .addVariableEdge(string, nodes.get(0))
                .build();
    }

    private HeapConfiguration attachedToNull() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type nullType = getNullType();
        final int rank = 1;
        final boolean[] isReductionTentacle = new boolean[rank];
        Nonterminal nt = sceneObject.scene().createNonterminal(methodName + rank, rank, isReductionTentacle);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder()
                .addNodes(nullType, 1, nodes)
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .build()
                .build();
    }

    private HeapConfiguration reachable_nullTerminated_List_nullExternal() {

        TIntArrayList nodes = new TIntArrayList();
        return nullTerminatedList(nodes).builder()
                .setExternal(nodes.get(0))
                .build();
    }

    private HeapConfiguration reachable_nullTerminated_List() {

        TIntArrayList nodes = new TIntArrayList();
        return nullTerminatedList(nodes);
    }

    /**
     * null node is nodes.get(0)
     *
     * @param nodes Node buffer
     * @return \@parameter0:-(.)-next->(NULL)-null
     */
    private HeapConfiguration nullTerminatedList(TIntArrayList nodes) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = getSomeType();
        Type nullType = getNullType();

        return hc.builder()
                .addNodes(nullType, 1, nodes)
                .addNodes(type, 1, nodes)
                .addVariableEdge("@parameter0:", nodes.get(1))
                .addVariableEdge("null", nodes.get(0))
                .addSelector(nodes.get(1), nextLabel, nodes.get(0))
                .build();
    }

    private Type getSomeType() {

        return sceneObject.scene().getType("someType");
    }

    private Type getNullType() {

        return Types.NULL;
    }


    private HeapConfiguration nodeReachableFromTwoSides(String parameterName, String variableName) {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        hc.builder().addNodes(type, 1, nodes).build();
        int meetingPoint = nodes.get(0);
        addParameterSide(hc, meetingPoint, parameterName);
        addVariableSide(hc, meetingPoint, variableName);
        return hc;
    }

    private HeapConfiguration parameterSide(String parameterName) {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        hc.builder().addNodes(type, 1, nodes)
                .setExternal(nodes.get(0))
                .build();
        int meetingPoint = nodes.get(0);
        addParameterSide(hc, meetingPoint, parameterName);
        return hc;
    }

    private HeapConfiguration ntAttachedToVariableSide(String variableName) {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration hc = singleNodeAttachedHelper(nodes);
        int meetingPoint = nodes.get(0);
        addParameterSide(hc, meetingPoint, variableName);
        return hc;
    }

    private void addVariableSide(HeapConfiguration hc, int meetingPoint, String variableName) {

        addParameterSide(hc, meetingPoint, variableName);
    }

    private void addParameterSide(HeapConfiguration hc, int meetingPoint, String parameterName) {

        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge(parameterName, nodes.get(0))
                .addSelector(nodes.get(0), nextLabel, meetingPoint)
                .build();
    }


}
