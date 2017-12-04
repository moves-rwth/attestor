package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.morphism.Morphism;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ComplexGraphBuilderTest {

    private static final Logger logger = LogManager.getLogger("GraphBuilderTest");

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcImplFactory;
    private Type type;

    private SelectorLabel sel;
    private SelectorLabel selA;
    private SelectorLabel selB;
    private SelectorLabel next;
    private SelectorLabel prev;
    private SelectorLabel left;
    private SelectorLabel right;

    private Nonterminal nt;
    private Nonterminal nt2;

    private HeapConfiguration result;
    private TIntArrayList nodes;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        hcImplFactory = new ExampleHcImplFactory(sceneObject);
        type = sceneObject.scene().getType("type");

        sel = sceneObject.scene().getSelectorLabel("selector");
        selA = sceneObject.scene().getSelectorLabel("a");
        selB = sceneObject.scene().getSelectorLabel("b");
        next = sceneObject.scene().getSelectorLabel("next");
        prev = sceneObject.scene().getSelectorLabel("prev");
        left = sceneObject.scene().getSelectorLabel("left");
        right = sceneObject.scene().getSelectorLabel("right");

        nt = sceneObject.scene().createNonterminal("3", 3, new boolean[]{false, false, false});
        nt2 = sceneObject.scene().createNonterminal("2", 2, new boolean[]{false, false});

        result = new InternalHeapConfiguration();
        nodes = new TIntArrayList();
    }

    @Test
    public void testAddNode() {

        result.builder()
                .addNodes(type, 1, nodes)
                .build();

        assertEquals(1, result.countNodes());
        assertTrue(result.nodes().contains(nodes.get(0)));

        result.builder()
                .addNodes(type, 3, nodes)
                .build();

        assertEquals(4, result.countNodes());
    }

    @Test
    public void testRemoveIsolatedNodeSuccess() {

        result.builder()
                .addNodes(type, 1, nodes)
                .removeIsolatedNode(nodes.get(0))
                .build();

        assertEquals(0, result.countNodes());

        result.builder()
                .addNodes(type, 5, nodes)
                .removeIsolatedNode(nodes.get(3))
                .build();

        assertEquals(4, result.countNodes());
    }

    @Test
    public void testRemoveIsolatedExternalNode() {

        result.builder()
                .addNodes(type, 1, nodes)
                .setExternal(nodes.get(0))
                .removeIsolatedNode(nodes.get(0))
                .build();

        assertEquals(0, result.countNodes());
        assertEquals(0, result.countExternalNodes());
    }

    @Test
    public void testRemoveIsolatedNodeFail1() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addVariableEdge("x", nodes.get(1))
                .build();

        try {
            result.builder().removeIsolatedNode(nodes.get(1)).build();
            fail("this should throw an error since a variable points to the node.");
        } catch (IllegalArgumentException e) {
            //this is expected
        }
    }

    @Test
    public void testRemoveIsolatedNodeFail2() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();

        try {
            result.builder().removeIsolatedNode(nodes.get(0)).build();
            fail("this should throw an error since a selector starts at the node.");
        } catch (IllegalArgumentException e) {
            //this is expected
        }
    }

    @Test
    public void testRemoveIsolatedNodeFail3() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();

        try {
            result.builder().removeIsolatedNode(nodes.get(1)).build();
            fail("this should throw an error since a selector points to the node.");
        } catch (IllegalArgumentException e) {
            //this is expected
        }
    }

    @Test
    public void testRemoveNodeSuccess() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .addSelector(nodes.get(0), selA, nodes.get(2))
                .addSelector(nodes.get(1), sel, nodes.get(2))
                .addVariableEdge("x", nodes.get(1))
                .build();

        try {
            result.builder().removeNode(nodes.get(1)).build();
            assertEquals(2, result.countNodes());
            assertEquals(0, result.countVariableEdges());
            assertEquals(1, result.selectorLabelsOf(nodes.get(0)).size());
            assertEquals(0, result.selectorLabelsOf(nodes.get(1)).size());
        } catch (IllegalArgumentException e) {
            //this is expected
        }
    }

    @Test
    public void testAddSelector() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();

        int n = nodes.get(0);

        assertEquals("There should be one selector at node 0", 1, result.selectorLabelsOf(n).size());
        assertTrue("The selector should point to node 1", result.selectorTargetOf(n, sel) == nodes.get(1));
        assertEquals("Node 1 should have one successor", 1, result.successorNodesOf(n).size());
        assertTrue("The successor should be node 1", result.successorNodesOf(n).contains(nodes.get(1)));
        assertEquals("Node 1 should have 1 predecessor", 1, result.predecessorNodesOf(nodes.get(1)).size());
        assertEquals("The predecessor should be node 0", n, result.predecessorNodesOf(nodes.get(1)).get(0));
    }

    @Test
    public void testAddTwoSelectors() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), selB, nodes.get(1))
                .addSelector(nodes.get(0), selA, nodes.get(1))
                .build();

        assertEquals("There should be 2 selectors at node 0", 2, result.selectorLabelsOf(nodes.get(0)).size());
        assertEquals("Node 0 should have 1 successor", 1, result.successorNodesOf(nodes.get(0)).size());
        assertEquals("Node 1 should have 1 predecessor", 1, result.predecessorNodesOf(nodes.get(1)).size());
    }

    @Test
    public void testRemoveSelector() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .removeSelector(nodes.get(0), sel)
                .build();


        assertEquals("There should be no selector at node 0", 0, result.selectorLabelsOf(nodes.get(0)).size());
        assertEquals("Node 1 should have no successors", 0, result.successorNodesOf(nodes.get(0)).size());
        assertEquals("Node 1 should have 0 predecessors", 0, result.predecessorNodesOf(nodes.get(1)).size());
    }

    @Test
    public void testRemoveSelectorWithParallel() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), selB, nodes.get(1))
                .addSelector(nodes.get(0), selA, nodes.get(1))
                .removeSelector(nodes.get(0), selB)
                .build();

        assertEquals("There should be one selector at node 0", 1, result.selectorLabelsOf(nodes.get(0)).size());
        assertTrue("The selector should point to node 1", result.selectorTargetOf(nodes.get(0), selA) == nodes.get(1));
        assertEquals("Node 1 should have one successor", 1, result.successorNodesOf(nodes.get(0)).size());
        assertTrue("The successor should be node 1", result.successorNodesOf(nodes.get(0)).contains(nodes.get(1)));
        assertEquals("Node 1 should have 1 predecessor", 1, result.predecessorNodesOf(nodes.get(1)).size());
        assertEquals("The predecessor should be node 0", nodes.get(0), result.predecessorNodesOf(nodes.get(1)).get(0));
    }

    @Test
    public void testSetExternal() {

        result.builder()
                .addNodes(type, 1, nodes)
                .setExternal(nodes.get(0))
                .build();

        assertEquals("the result should have 1 external node", 1, result.countExternalNodes());
        assertTrue("n should be in the set of external nodes", result.externalNodes().contains(nodes.get(0)));
        assertTrue("n should be external", result.isExternalNode(nodes.get(0)));
    }

    @Test
    public void testSetExternalFail() {

        try {
            result.builder()
                    .addNodes(type, 1, nodes)
                    .removeIsolatedNode(nodes.get(0))
                    .setExternal(nodes.get(0))
                    .build();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testUnsetExternal() {

        result.builder()
                .addNodes(type, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .build();


        assertTrue(result.externalNodes().contains(nodes.get(0)));
        assertTrue(result.externalNodes().contains(nodes.get(1)));
        assertFalse(result.externalNodes().contains(nodes.get(2)));

        result.builder().unsetExternal(nodes.get(1)).build();

        assertTrue(result.externalNodes().contains(nodes.get(0)));
        assertFalse(result.externalNodes().contains(nodes.get(1)));
        assertFalse(result.externalNodes().contains(nodes.get(2)));

        assertTrue(result.nodes().contains(nodes.get(1)));
        assertEquals(3, result.countNodes());
        assertEquals(1, result.countExternalNodes());
    }

    @Test
    public void testAddVariableEdge() {


        result.builder()
                .addNodes(type, 4, nodes)
                .addVariableEdge("x", nodes.get(2))
                .build();

        assertEquals("there should be one variable in the graph", 1, result.variableEdges().size());
        assertEquals("the target of x should be node 2", nodes.get(2), result.targetOf(result.variableWith("x")));
        assertEquals("node 2 should have one variable attached", 1, result.attachedVariablesOf(nodes.get(2)).size());
        assertEquals("the variable at node 2 should be x", result.variableWith("x"), result.attachedVariablesOf(nodes.get(2)).get(0));
    }

    @Test
    public void testRemoveVariableEdge() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addVariableEdge("y", nodes.get(0))
                .addVariableEdge("z", nodes.get(1))
                .build();

        int x = result.variableWith("x");

        assertEquals("there should be 3 variables in the graph", 3, result.variableEdges().size());
        assertEquals("the target of x should be node 0", nodes.get(0), result.targetOf(x));
        assertEquals("node 0 should have 2 variables attached", 2, result.attachedVariablesOf(nodes.get(0)).size());
        assertTrue("the variables at node 0 should contain x", result.attachedVariablesOf(nodes.get(0)).contains(x));

        assertTrue(result.variableEdges().contains(x));

        result = result.builder().removeVariableEdge(x).build();

        int y = result.variableWith("y");

        assertEquals("there should be 2 variables in the graph", 2, result.variableEdges().size());
        assertEquals("the target of y should be node 0", nodes.get(0), result.targetOf(y));
        assertEquals("node 0 should have 1 variable attached", 1, result.attachedVariablesOf(nodes.get(0)).size());
        assertTrue("the variables at node 0 should contain y", result.attachedVariablesOf(nodes.get(0)).contains(y));
    }

    @Test
    public void testRemoveVariableName() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addVariableEdge("y", nodes.get(0))
                .addVariableEdge("z", nodes.get(1))
                .build();

        int x = result.variableWith("x");
        int y = result.variableWith("y");

        assertEquals("there should be 3 variables in the graph", 3, result.variableEdges().size());
        assertEquals("the target of x should be node 0", nodes.get(0), result.targetOf(x));
        assertEquals("node 0 should have 2 variables attached", 2, result.attachedVariablesOf(nodes.get(0)).size());
        assertTrue("the variables at node 0 should contain x", result.attachedVariablesOf(nodes.get(0)).contains(x));

        result = result.builder().removeVariableEdge(x).build();

        assertEquals("there should be 2 variables in the graph", 2, result.variableEdges().size());
        assertEquals("the target of y should be node 0", nodes.get(0), result.targetOf(y));
        assertEquals("node 0 should have 1 variable attached", 1, result.attachedVariablesOf(nodes.get(0)).size());
        assertTrue("the variables at node 0 should contain y", result.attachedVariablesOf(nodes.get(0)).contains(y));
    }

    /*
     * The builder should not crash if one tries to remove a variable
     * which isn't even in the graph (should just do nothing)
     */
    @Test
    public void testRemoveMissingVariable() {

        result.builder()
                .addNodes(type, 3, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addVariableEdge("y", nodes.get(0))
                .addVariableEdge("z", nodes.get(1))
                .build();

        int x = result.variableWith("x");
        int y = result.variableWith("y");

        assertEquals("there should be 3 variables in the graph", 3, result.variableEdges().size());
        assertEquals("the target of x should be node 0", nodes.get(0), result.targetOf(x));
        assertEquals("node 0 should have 2 variables attached", 2, result.attachedVariablesOf(nodes.get(0)).size());
        assertTrue("the variables at node 0 should contain x", result.attachedVariablesOf(nodes.get(0)).contains(x));

        try {
            result = result.builder().removeVariableEdge(result.variableWith("w")).build();
            fail("Variable w does not exist.");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        assertEquals("there should be 3 variables in the graph", 3, result.variableEdges().size());
        assertEquals("the target of y should be node 0", nodes.get(0), result.targetOf(y));
        assertEquals("node 0 should have 2 variable attached", 2, result.attachedVariablesOf(nodes.get(0)).size());
        assertTrue("the variables at node 0 should contain y", result.attachedVariablesOf(nodes.get(0)).contains(y));

    }

    @Test
    public void testAddNonterminalEdge() {

        result.builder()
                .addNodes(type, 5, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(0), nodes.get(1)}))
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(3), nodes.get(4)}))
                .build();

        assertEquals(2, result.countNonterminalEdges());
        assertEquals(2, result.attachedNonterminalEdgesOf(nodes.get(0)).size());
        assertTrue(result.nonterminalEdges().containsAll(result.attachedNonterminalEdgesOf(nodes.get(0))));
        assertEquals(nodes.get(0), result.attachedNodesOf(result.nonterminalEdges().get(0)).get(0));
    }

    @Test
    public void testAddNonterminalEdgeWithBuilder() {

        result.builder()
                .addNodes(type, 5, nodes)
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(3))
                .addTentacle(nodes.get(4))
                .build()
                .build();

        assertEquals(2, result.countNonterminalEdges());
        assertEquals(2, result.attachedNonterminalEdgesOf(nodes.get(0)).size());
        assertTrue(result.nonterminalEdges().containsAll(result.attachedNonterminalEdgesOf(nodes.get(0))));
        assertEquals(nodes.get(0), result.attachedNodesOf(result.nonterminalEdges().get(0)).get(0));
    }

    @Test
    public void testAddNonterminalWithWrongRank() {

        try {
            result.builder()
                    .addNodes(type, 5, nodes)
                    .addNonterminalEdge(nt)
                    .addTentacle(nodes.get(0))
                    .addTentacle(nodes.get(0))
                    .addTentacle(nodes.get(1))
                    .addTentacle(nodes.get(2))
                    .build();
            fail("Expect exception");
        } catch (Exception e) {
            //expected
        }
    }

    @Test
    public void testRemoveNonterminalEdge() {

        result.builder()
                .addNodes(type, 5, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(0), nodes.get(4)}))
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(3), nodes.get(4)}))
                .build();

        int toDelete = result.nonterminalEdges().get(0);

        result.builder()
                .removeNonterminalEdge(toDelete)
                .build();

        assertEquals(1, result.attachedNonterminalEdgesOf(nodes.get(0)).size());
        assertEquals(1, result.attachedNonterminalEdgesOf(nodes.get(4)).size());
        assertEquals(1, result.countNonterminalEdges());
        assertEquals(result.nodes().get(0), nodes.get(0));

        int x = result.nodes().get(0);
        int y = result.attachedNonterminalEdgesOf(x).get(0);
        int z = result.attachedNodesOf(y).get(1);

        assertFalse(z == x);
    }

    @Test
    public void testReplaceHyperedge() {

        HeapConfiguration outerGraph = new InternalHeapConfiguration();
        outerGraph.builder()
                .addNodes(type, 5, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(0), nodes.get(4)}))
                .build();

        HeapConfiguration innerGraph = new InternalHeapConfiguration();
        TIntArrayList nodes2 = new TIntArrayList();

        innerGraph.builder()
                .addNodes(type, 4, nodes2)
                .setExternal(nodes2.get(0))
                .setExternal(nodes2.get(1))
                .setExternal(nodes2.get(2))
                .addSelector(nodes2.get(0), sel, nodes2.get(1))
                .addSelector(nodes2.get(2), sel, nodes2.get(3))
                .build();

        int toReplace = outerGraph.nonterminalEdges().get(0);

        result = outerGraph.builder()
                .replaceNonterminalEdge(toReplace, innerGraph)
                .build();

        assertEquals("result should have 6 nodes", 6, result.countNodes());
        assertEquals("result should have no external nodes", 0, result.countExternalNodes());
        assertEquals("result should have no nonterminals", 0, result.countNonterminalEdges());
        assertEquals("selector if node 0 should point to 0", nodes.get(0), result.selectorTargetOf(nodes.get(0), sel));

        int x = result.selectorTargetOf(nodes.get(4), sel);
        assertFalse("selector at old node 4 should point to new node", nodes.contains(x));

        HeapConfiguration expectedGraph = new InternalHeapConfiguration();
        TIntArrayList nodes3 = new TIntArrayList();
        expectedGraph.builder()
                .addNodes(type, 6, nodes3)
                .addSelector(nodes3.get(0), sel, nodes3.get(0))
                .addSelector(nodes3.get(4), sel, nodes3.get(5))
                .build();

        assertEquals("result should be isomorphic to expected graph", expectedGraph, result);
    }

    @Test
    public void testReplaceEmbedding() {

        HeapConfiguration testGraph = new InternalHeapConfiguration();
        testGraph.builder()
                .addNodes(type, 2, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .build();

        int[] m = {0, 1};
        Morphism morphism = new Morphism(m);
        Matching embedding = new InternalMatching(testGraph, morphism, testGraph);

        result = testGraph.builder()
                .replaceMatching(embedding, nt2)
                .build();

        nodes.clear();
        HeapConfiguration expected = new InternalHeapConfiguration();
        expected.builder()
                .addNodes(type, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addNonterminalEdge(nt2, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .build();

        assertEquals(expected, result);

    }

    @Test
    public void testReplaceEmbedding2() {

        HeapConfiguration outerGraph = new InternalHeapConfiguration();
        TIntArrayList outerGraphNodes = new TIntArrayList();
        outerGraph.builder()
                .addNodes(type, 4, outerGraphNodes)
                .addSelector(outerGraphNodes.get(0), next, outerGraphNodes.get(1))
                .addSelector(outerGraphNodes.get(1), next, outerGraphNodes.get(2))
                .addSelector(outerGraphNodes.get(2), prev, outerGraphNodes.get(1))
                .addSelector(outerGraphNodes.get(1), prev, outerGraphNodes.get(0))
                .addNonterminalEdge(nt2, new TIntArrayList(new int[]{outerGraphNodes.get(2), outerGraphNodes.get(3)}))
                .build();

        HeapConfiguration innerGraph = new InternalHeapConfiguration();
        TIntArrayList innerGraphNodes = new TIntArrayList();
        innerGraph.builder()
                .addNodes(type, 3, innerGraphNodes)
                .addSelector(innerGraphNodes.get(0), next, innerGraphNodes.get(1))
                .addSelector(innerGraphNodes.get(1), prev, innerGraphNodes.get(0))
                .addNonterminalEdge(nt2, new TIntArrayList(new int[]{innerGraphNodes.get(1), innerGraphNodes.get(2)}))
                .setExternal(innerGraphNodes.get(0))
                .setExternal(innerGraphNodes.get(2))
                .build();


        HeapConfiguration expectedGraph = new InternalHeapConfiguration();
        TIntArrayList expectedGraphNodes = new TIntArrayList();
        expectedGraph.builder()
                .addNodes(type, 3, expectedGraphNodes)
                .addSelector(expectedGraphNodes.get(0), next, expectedGraphNodes.get(1))
                .addSelector(expectedGraphNodes.get(1), prev, expectedGraphNodes.get(0))
                .addNonterminalEdge(nt2, new TIntArrayList(new int[]{innerGraphNodes.get(1), innerGraphNodes.get(2)}))
                .build();

        int ntIn = innerGraph.nonterminalEdges().get(0);
        int ntOut = outerGraph.nonterminalEdges().get(0);

        int[] m = new int[4];
        m[innerGraphNodes.get(0)] = outerGraphNodes.get(1);
        m[innerGraphNodes.get(1)] = outerGraphNodes.get(2);
        m[innerGraphNodes.get(2)] = outerGraphNodes.get(3);
        m[ntIn] = ntOut;

        Morphism morphism = new Morphism(m);
        Matching embedding = new InternalMatching(innerGraph, morphism, outerGraph);

        result = outerGraph.builder()
                .replaceMatching(embedding, nt2)
                .build();

        assertEquals(expectedGraph, result);

    }

    @Test
    public void testAddNodesOnce() {

        result.builder()
                .addNodes(type, 4, nodes)
                .build();

        assertEquals(4, result.countNodes());
        assertEquals(4, nodes.size());
        assertTrue(result.nodes().containsAll(nodes));
    }

    @Test
    public void testAddNodesTwice() {

        result.builder()
                .addNodes(type, 4, nodes)
                .addNodes(type, 5, nodes)
                .build();

        assertEquals(9, result.countNodes());
        assertEquals(9, nodes.size());
        assertTrue(result.nodes().containsAll(nodes));
    }

    @Test
    public void testGetNodes() {

        assertEquals(0, result.nodes().size());
    }

    @Test
    public void testBuilder() {

        result.builder()
                .addNodes(type, 2, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();
    }

    @Test
    public void testClone() {

        List<HeapConfiguration> availableHcs = new ArrayList<>();

        availableHcs.add(hcImplFactory.getAdmissibleGraph());
        availableHcs.add(hcImplFactory.getBadTwoElementDLL());
        availableHcs.add(hcImplFactory.getCanonizationRes1());
        availableHcs.add(hcImplFactory.getEmptyGraphWithConstants());
        availableHcs.add(hcImplFactory.getInAdmissibleGraph());
        availableHcs.add(hcImplFactory.getLargerTree());
        availableHcs.add(hcImplFactory.getList());
        availableHcs.add(hcImplFactory.getListAndConstants());

        for (HeapConfiguration hc : availableHcs) {
            assertEquals(hc, hc.clone());
        }
    }

    @Test
    public void testChangeSelectorLabel() {

        result = hcImplFactory.getInput_changeSelectorLabel();

        int var = result.variableWith("x");
        int node = result.targetOf(var);

        AnnotatedSelectorLabel oldSel = new AnnotatedSelectorLabel(left, "?");
        AnnotatedSelectorLabel newSel = new AnnotatedSelectorLabel(left, "2");

        result.builder()
                .replaceSelector(node, oldSel, newSel)
                .build();

        assertEquals(hcImplFactory.getExpected_changeSelectorLabel(), result);
    }
}
