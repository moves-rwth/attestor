package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HeapConfigurationTest {

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);
    }


    /**
     * Tests access to builders through HeapConfiguration.
     */
    @Test
    public void testBuilder() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        hc.builder().build();

        assertTrue(hc.builder() == hc.builder());

        HeapConfigurationBuilder builder = hc.builder();
        builder.build();

        assertFalse("Builders of heap configurations should be unique.", builder == hc.builder());

        assertTrue("Builders should work on the same HC as passed to them.", hc == hc.builder().build());

        try {
            new InternalHeapConfigurationBuilder(null);
            fail("HeapConfigurationBuilder cannot be initialized with null.");
        } catch (NullPointerException ignored) {

        }
    }

    /**
     * Tests access to all nodes in a HeapConfiguration.
     */
    @Test
    public void testNodes() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(new MockupType(), 3, nodes)
                .removeIsolatedNode(nodes.get(1))
                .addNodes(new MockupType(), 17, nodes)
                .build();

        assertEquals(19, hc.nodes().size());

        for (int i = 0; i < 19; i++) {

            if (i != 1) {
                assertTrue(hc.nodes().contains(i));
            }
        }
    }

    /**
     * Tests the access to the type of a node.
     */
    @Test
    public void testNodeTypeOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();

        hc.builder().addNodes(type, 1, new TIntArrayList()).build();

        assertEquals(type, hc.nodeTypeOf(0));
    }

    /**
     * Tests invalid inputs when requesting the type of a node.
     */
    @Test
    public void testDefensiveNodeTypeOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();

        try {
            hc.nodeTypeOf(0);
            fail("Provided ID does not exist.");
        } catch (IllegalArgumentException ignored) {
        }

        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .build();

        try {
            hc.nodeTypeOf(hc.variableEdges().get(0));
            fail("Provided ID does not correspond to a node.");
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests access to the variables attached to a node.
     */
    @Test
    public void testAttachedVariablesOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addNodes(type, 3, nodes)
                .addVariableEdge("y", nodes.get(0))
                .addVariableEdge("z", nodes.get(2))
                .build();

        assertEquals(2, hc.attachedVariablesOf(nodes.get(0)).size());
        assertTrue(hc.attachedVariablesOf(nodes.get(0)).contains(1));
        assertTrue(hc.attachedVariablesOf(nodes.get(0)).contains(5));

        assertEquals(0, hc.attachedVariablesOf(nodes.get(1)).size());

        assertEquals(1, hc.attachedVariablesOf(nodes.get(2)).size());
        assertTrue(hc.attachedVariablesOf(nodes.get(2)).contains(6));
    }

    /**
     * Tests invalid inputs when requesting the variables attached to a node.
     */
    @Test
    public void testDefensiveAttachedVariablesOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        try {
            hc.attachedVariablesOf(1);
            fail("Provided ID does not exist");
        } catch (IllegalArgumentException ignored) {
        }

        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addNodes(type, 3, nodes)
                .addVariableEdge("y", nodes.get(0))
                .addVariableEdge("z", nodes.get(2))
                .build();

        try {
            hc.attachedVariablesOf(1);
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests access to the nonterminal edges attached to a node.
     */
    @Test
    public void testAttachedNonterminalEdgesOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 3, nodes)
                .addNonterminalEdge(new MockupNonterminal("nt", 3), nodes)
                .addNonterminalEdge(new MockupNonterminal("X", 2), new TIntArrayList(new int[]{nodes.get(1), nodes.get(0)}))
                .build();

        assertEquals(2, hc.attachedNonterminalEdgesOf(nodes.get(1)).size());
        assertEquals(1, hc.attachedNonterminalEdgesOf(nodes.get(2)).size());

        assertEquals(
                "nt",
                hc.labelOf(hc.attachedNonterminalEdgesOf(nodes.get(1)).get(0)).toString()
        );

    }

    /**
     * Tests invalid inputs when accessing the nonterminal edges of a node.
     */
    @Test
    public void testDefensiveAttachedNonterminalEdgesOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        try {
            hc.attachedNonterminalEdgesOf(1);
            fail("Provided ID does not exist");
        } catch (IllegalArgumentException ignored) {
        }

        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addNodes(type, 3, nodes)
                .addVariableEdge("y", nodes.get(0))
                .addVariableEdge("z", nodes.get(2))
                .build();

        try {
            hc.attachedNonterminalEdgesOf(1);
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests access to the successor nodes of a node.
     */
    @Test
    public void testSuccessorNodesOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 5, nodes)
                .addSelector(nodes.get(0), new MockupSelector("first"), nodes.get(1))
                .addSelector(nodes.get(0), new MockupSelector("second"), nodes.get(2))
                .addSelector(nodes.get(0), new MockupSelector("third"), nodes.get(3))
                .addSelector(nodes.get(0), new MockupSelector("fourth"), nodes.get(4))
                .build();

        assertEquals(4, hc.successorNodesOf(nodes.get(0)).size());
        assertEquals(nodes.get(3), hc.successorNodesOf(nodes.get(0)).get(2));
    }

    /**
     * Tests invalid inputs for accessing successor nodes of a given node.
     */
    @Test
    public void testDefensiveSuccessorNodesOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        try {
            hc.successorNodesOf(1);
            fail("Provided ID does not exist");
        } catch (IllegalArgumentException ignored) {
        }

        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addNodes(type, 3, nodes)
                .addVariableEdge("y", nodes.get(0))
                .addVariableEdge("z", nodes.get(2))
                .build();

        try {
            hc.successorNodesOf(1);
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests access to the predecessor nodes of a node.
     */
    @Test
    public void testPredecessorNodesOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 5, nodes)
                .addSelector(nodes.get(1), new MockupSelector("first"), nodes.get(0))
                .addSelector(nodes.get(2), new MockupSelector("second"), nodes.get(0))
                .addSelector(nodes.get(3), new MockupSelector("third"), nodes.get(0))
                .addSelector(nodes.get(4), new MockupSelector("fourth"), nodes.get(0))
                .build();

        assertEquals(4, hc.predecessorNodesOf(nodes.get(0)).size());
        assertEquals(nodes.get(3), hc.predecessorNodesOf(nodes.get(0)).get(2));
    }

    /**
     * Tests invalid inputs for accessing predecessor nodes of a given node.
     */
    @Test
    public void testDefensivePredecessorNodesOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        try {
            hc.predecessorNodesOf(1);
            fail("Provided ID does not exist");
        } catch (IllegalArgumentException ignored) {
        }

        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addNodes(type, 3, nodes)
                .addVariableEdge("y", nodes.get(0))
                .addVariableEdge("z", nodes.get(2))
                .build();

        try {
            hc.predecessorNodesOf(1);
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests access to all selectors occurring on outgoing edges of a node.
     */
    @Test
    public void testSelectorLabelsOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 5, nodes)
                .addSelector(nodes.get(0), new MockupSelector("first"), nodes.get(1))
                .addSelector(nodes.get(0), new MockupSelector("second"), nodes.get(2))
                .addSelector(nodes.get(0), new MockupSelector("third"), nodes.get(3))
                .addSelector(nodes.get(0), new MockupSelector("fourth"), nodes.get(4))
                .build();

        assertEquals(4, hc.selectorLabelsOf(nodes.get(0)).size());
        assertEquals("third", hc.selectorLabelsOf(nodes.get(0)).get(2).toString());
    }

    /**
     * Tests invalid inputs when accessing selectors occurring on outgoing edges of a node.
     */
    @Test
    public void testDefensiveSelectorLabelsOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        try {
            hc.selectorLabelsOf(1);
            fail("Provided ID does not exist");
        } catch (IllegalArgumentException ignored) {
        }

        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addNodes(type, 3, nodes)
                .addVariableEdge("y", nodes.get(0))
                .addVariableEdge("z", nodes.get(2))
                .build();

        try {
            hc.selectorLabelsOf(1);
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests access to the node reached from a given node using a given selector.
     */
    @Test
    public void testSelectorTargetOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();
        MockupSelector sel = new MockupSelector("mySelector");

        hc.builder()
                .addNodes(type, 5, nodes)
                .addSelector(nodes.get(0), new MockupSelector("first"), nodes.get(1))
                .addSelector(nodes.get(0), new MockupSelector("second"), nodes.get(2))
                .addSelector(nodes.get(0), sel, nodes.get(3))
                .addSelector(nodes.get(0), new MockupSelector("fourth"), nodes.get(4))
                .build();

        assertEquals(nodes.get(3), hc.selectorTargetOf(nodes.get(0), sel));
    }

    /**
     * Tests invalid inputs when accessing the node reached from a given node by a given selector.
     */
    @Test
    public void testDefensiveSelectorTargetOf() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        try {
            hc.selectorTargetOf(2, new MockupSelector("test"));
            fail("Provided ID does not exist");
        } catch (IllegalArgumentException ignored) {
        }

        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addNodes(type, 3, nodes)
                .addVariableEdge("y", nodes.get(0))
                .addVariableEdge("z", nodes.get(2))
                .build();

        try {
            hc.selectorTargetOf(2, null);
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests creation of deep copies of HeapConfigurations.
     */
    @Test
    public void testClone() {

        InternalHeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        SelectorLabel sel1 = new MockupSelector("first");

        hc.builder()
                .addNodes(new MockupType(), 7, nodes)
                .addVariableEdge("x", nodes.get(1))
                .addNonterminalEdge(new MockupNonterminal("nt", 2), new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .addSelector(nodes.get(1), sel1, nodes.get(4))
                .addNodes(new MockupType(), 1, nodes)
                .addVariableEdge("y", nodes.get(7))
                .setExternal(nodes.get(5))
                .build();

        HeapConfiguration cloned = hc.clone();

        assertEquals(hc.countNodes(), cloned.countNodes());
        assertEquals(hc.countNonterminalEdges(), cloned.countNonterminalEdges());
        assertEquals(hc.countVariableEdges(), cloned.countVariableEdges());
        assertEquals(hc.countExternalNodes(), cloned.countExternalNodes());

        assertEquals(hc.attachedVariablesOf(nodes.get(1)), cloned.attachedVariablesOf(nodes.get(1)));

        assertTrue(hc.equals(cloned));
    }

    @Test
    public void testHash() {

        final HeapConfiguration testInput = hcFactory.getInput_testHash();
        final HeapConfiguration testInput_permuted = hcFactory.getInput_testHash_Permuted();

        assertEquals("Inputs not considered equal", testInput, testInput_permuted);
        assertEquals("Hash code not identical", testInput.hashCode(), testInput_permuted.hashCode());
    }
}
