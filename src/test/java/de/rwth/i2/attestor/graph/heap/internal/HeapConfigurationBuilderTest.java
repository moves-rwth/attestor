package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.morphism.Morphism;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Test;

import static org.junit.Assert.*;

public class HeapConfigurationBuilderTest {

    /**
     * Tests the intended usage of builders to add new nodes.
     */
    @Test
    public void testAddNodes() {

        MockupType type = new MockupType();
        TIntArrayList buffer = new TIntArrayList();
        int count = 1;

        HeapConfiguration hc = new InternalHeapConfiguration();
        HeapConfigurationBuilder builder = hc.builder().addNodes(type, count, buffer);
        assertTrue("The same builder should be returned", hc.builder() == builder);

        assertEquals("Exactly one new node should have been created.", count, buffer.size());
        assertEquals("The single element of a previously empty HC has ID 0.", 0, buffer.get(0));
        assertEquals("HC containsSubsumingState exactly one node", 1, hc.countNodes());

        hc.builder().addNodes(type, 7, buffer);
        assertEquals("Buffer should now contain the original and the newly created nodes.", 8, buffer.size());
        assertEquals("HC containsSubsumingState exactly one node", 8, hc.countNodes());

        for (int i = 0; i < 8; i++) {
            assertTrue("Created node IDs should range from 0 to 7", buffer.contains(i));
        }
    }

    /**
     * Tests invalid input when adding nodes.
     */
    @Test
    public void testDefensiveAddNodes() {

        MockupType type = new MockupType();
        TIntArrayList buffer = new TIntArrayList();
        int count = 8;

        HeapConfiguration hc = new InternalHeapConfiguration();
        hc.builder().addNodes(type, count, buffer).build();

        try {
            hc.builder().addNodes(null, 1, buffer);
            fail("null is not accepted as a type.");
        } catch (NullPointerException ex) {
            assertEquals(8, hc.countNodes());
            assertEquals(8, buffer.size());
        }

        try {
            hc.builder().addNodes(type, -2, buffer);
            fail("Provided count must be positive.");
        } catch (IllegalArgumentException ex) {
            assertEquals(8, hc.countNodes());
            assertEquals(8, buffer.size());
        }

        try {
            hc.builder().addNodes(type, count, null);
            fail("Provided buffer must not be null.");
        } catch (NullPointerException ex) {
            assertEquals(8, hc.countNodes());
            assertEquals(8, buffer.size());
        }
    }

    /**
     * Tests the intended usage of builders to remove isolated nodes.
     */
    @Test
    public void testRemoveIsolatedNode() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();
        TIntArrayList buffer = new TIntArrayList();
        SelectorLabel sel = new MockupSelector("test");

        hc.builder()
                .addNodes(type, 10, buffer)
                .removeIsolatedNode(3)
                .build();

        assertEquals("There should be 9 nodes remaining.", 9, hc.countNodes());

        try {
            hc.builder().addSelector(3, sel, 0).build();
            fail("Node 3 should not exist anymore.");
        } catch (IllegalArgumentException ignored) {
        }

        InternalHeapConfiguration ihc = (InternalHeapConfiguration) hc;
        assertEquals("Underlying graph should be shrinked to 9 nodes.", 9, ihc.graph.size());
    }

    /**
     * Tests invalid inputs when removing isolated nodes.
     */
    @Test
    public void testDefensiveRemoveIsolatedNode() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        SelectorLabel sel = new MockupSelector("test");

        try {
            hc.builder().removeIsolatedNode(0);
            fail("Provided node to remove does not exist.");
        } catch (IllegalArgumentException ignored) {
        }

        TIntArrayList nodeBuffer = new TIntArrayList();

        try {
            hc.builder()
                    .addNodes(new MockupType(), 3, nodeBuffer)
                    .addSelector(nodeBuffer.get(0), sel, nodeBuffer.get(1))
                    .removeIsolatedNode(nodeBuffer.get(0))
                    .build();
            fail("Provided node is not isolated.");
        } catch (IllegalArgumentException ignored) {

        }

        try {
            hc.builder()
                    .addNodes(new MockupType(), 3, nodeBuffer)
                    .addSelector(nodeBuffer.get(0), sel, nodeBuffer.get(1))
                    .removeIsolatedNode(nodeBuffer.get(1))
                    .build();
            fail("Provided node is not isolated due to selector.");
        } catch (IllegalArgumentException ignored) {

        }

        try {
            hc.builder()
                    .addNodes(new MockupType(), 3, nodeBuffer)
                    .addVariableEdge("myVariable", nodeBuffer.get(2))
                    .removeIsolatedNode(nodeBuffer.get(2))
                    .build();
            fail("Provided node is not isolated due to variable.");
        } catch (IllegalArgumentException ignored) {

        }

        try {
            nodeBuffer.clear();
            hc.builder()
                    .addNodes(new MockupType(), 3, nodeBuffer)
                    .addNonterminalEdge(new MockupNonterminal("myNT", 2),
                            new TIntArrayList(new int[]{nodeBuffer.get(0), nodeBuffer.get(2)}))
                    .removeIsolatedNode(nodeBuffer.get(2))
                    .build();
            fail("Provided node is not isolated due to nonterminal edge.");
        } catch (IllegalArgumentException ignored) {

        }
    }

    /**
     * Tests the intended usage of builders to add selector edges
     */
    @Test
    public void testAddSelector() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();
        TIntArrayList buffer = new TIntArrayList();
        SelectorLabel sel = new MockupSelector("test");
        SelectorLabel otherSel = new MockupSelector("other");

        hc.builder()
                .addNodes(type, 4, buffer)
                .addSelector(buffer.get(1), sel, buffer.get(2))
                .addSelector(buffer.get(0), sel, buffer.get(1))
                .addSelector(buffer.get(2), sel, buffer.get(1))
                .addSelector(buffer.get(2), otherSel, buffer.get(0))
                .build();

        assertEquals("Node should have exactly one selector.", 1, hc.selectorLabelsOf(buffer.get(1)).size());
        assertEquals("Node should have exactly two selectors.", 2, hc.selectorLabelsOf(buffer.get(2)).size());
    }

    /**
     * Tests invalid inputs when adding selector edges.
     */
    @Test
    public void testDefensiveAddSelector() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();
        TIntArrayList buffer = new TIntArrayList();
        SelectorLabel sel = new MockupSelector("test");

        hc.builder().addNodes(type, 4, buffer).build();

        try {
            hc.builder()
                    .addSelector(buffer.get(2), sel, buffer.get(1))
                    .addSelector(buffer.get(2), sel, buffer.get(0))
                    .build();
            fail("Selector already exists.");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            hc.builder()
                    .addSelector(buffer.get(3), sel, HeapConfiguration.INVALID_ELEMENT)
                    .build();
            fail("Selectors may not be pointed to invalid elements");
        } catch(IllegalArgumentException ignored) {
        }

        try {
            hc.builder()
                    .addSelector(HeapConfiguration.INVALID_ELEMENT, sel, buffer.get(3))
                    .build();
            fail("Selectors may not originate from invalid elements");
        } catch(IllegalArgumentException ignored) {
        }

        try {
            hc.builder()
                    .addVariableEdge("var", buffer.get(0))
                    .addSelector(buffer.get(1), sel, 4)
                    .build();
            fail("Provided target node does not exist.");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            hc.builder()
                    .addVariableEdge("var2", buffer.get(0))
                    .addSelector(buffer.get(1), null, buffer.get(0))
                    .build();
            fail("Provided SelectorLabel must not be Null");
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * Tests the intended use of builders to remove selector edges
     */
    @Test
    public void testRemoveSelector() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        SelectorLabel s1 = new MockupSelector("first");
        SelectorLabel s2 = new MockupSelector("second");

        hc.builder()
                .addNodes(new MockupType(), 3, nodes)
                .addSelector(nodes.get(0), s1, nodes.get(1))
                .addSelector(nodes.get(1), s2, nodes.get(1))
                .addSelector(nodes.get(0), s2, nodes.get(2))
                .build();

        hc.builder().removeSelector(nodes.get(1), s2);
        assertEquals("There should be no remaining selector", 0, hc.selectorLabelsOf(nodes.get(1)).size());

        try {
            hc.builder().removeSelector(nodes.get(1), s2).build();
        } catch (Exception e) {
            fail("Removing non-existing selectors should have no effect");
        }

        assertTrue("The wrong selector was removed", hc.builder()
                .removeSelector(nodes.get(0), s2)
                .build()
                .selectorLabelsOf(nodes.get(0)).contains(s1)
        );
    }

    /**
     * Tests invalid inputs when removing selectors.
     */
    @Test
    public void testDefensiveRemoveSelector() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        SelectorLabel sel = new MockupSelector("test");

        try {
            hc.builder().removeSelector(0, sel);
            fail("Provided node does not exist.");
        } catch (IllegalArgumentException ignored) {

        }

        TIntArrayList nodes = new TIntArrayList();
        hc.builder().addNodes(new MockupType(), 3, nodes);

        try {
            hc.builder().removeSelector(nodes.get(0), null);
            fail("Provided selector must not be null.");
        } catch (NullPointerException ignored) {
        }

        hc.builder().addVariableEdge("var", nodes.get(0)).build();

        try {
            hc.builder().removeSelector(3, sel);
            fail("Provided ID is not a node.");
        } catch (IllegalArgumentException ignored) {

        }
    }

    /**
     * Tests the intended use of builders to replace the label of a selector edge.
     */
    @Test
    public void testReplaceSelector() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        SelectorLabel s1 = new MockupSelector("first");
        SelectorLabel s2 = new MockupSelector("second");
        SelectorLabel s3 = new MockupSelector("third");
        MockupType type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(1), s2, nodes.get(0))
                .addSelector(nodes.get(1), s1, nodes.get(0))
                .replaceSelector(nodes.get(1), s2, s3)
                .build();

        assertEquals("Number of selectors should remain unchanged.", 2, hc.selectorLabelsOf(nodes.get(1)).size());
        assertTrue("Node should now have a selector edge labeled with s3.", hc.selectorLabelsOf(nodes.get(1)).contains(s3));
        assertFalse("Node should not have a selector edge labeled with s2 anymore.", hc.selectorLabelsOf(nodes.get(1)).contains(s2));
    }

    /**
     * Tests invalid inputs when replacing selector labels
     */
    @Test
    public void testDefensiveReplaceSelector() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        SelectorLabel s1 = new MockupSelector("first");
        SelectorLabel s2 = new MockupSelector("second");
        MockupType type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(1), s2, nodes.get(0))
                .addSelector(nodes.get(1), s1, nodes.get(0))
                .build();

        try {
            hc.builder().replaceSelector(17, s2, s1);
            fail("Provided ID does not exist.");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            hc.builder().replaceSelector(nodes.get(0), null, s1);
            fail("First selector label is null.");
        } catch (NullPointerException ignored) {
        }

        try {
            hc.builder().replaceSelector(nodes.get(0), s1, null);
            fail("Second selector label is null.");
        } catch (NullPointerException ignored) {
        }

        hc.builder().addVariableEdge("var", nodes.get(1));
        try {
            hc.builder().replaceSelector(3, s1, s2);
            fail("Provided ID does not correspond to a node.");
        } catch (IllegalArgumentException ignored) {
        }


    }

    /**
     * Tests the intended usage of builders to mark a node as external.
     */
    @Test
    public void testSetExternal() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type type = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 3, nodes)
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .build();

        assertEquals("There should be two external nodes.", 2, hc.countExternalNodes());
        assertEquals("Node 1 should be external node 0.", 0, hc.externalIndexOf(nodes.get(1)));
        assertEquals("Node 2 should be external node 1.", 1, hc.externalIndexOf(nodes.get(2)));
        assertTrue("Node 1 is external.", hc.isExternalNode(nodes.get(1)));

        assertEquals("There should be two external nodes.", 2, hc.externalNodes().size());
        assertEquals("First external node is Node 1.", nodes.get(1), hc.externalNodes().get(0));
        assertEquals("Second external node is Node 2.", nodes.get(2), hc.externalNodeAt(1));
    }

    /**
     * Tests invalid inputs when marking a node as external.
     */
    @Test
    public void testDefensiveSetExternal() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        try {
            hc.builder().setExternal(1);
            fail("Provided ID does not exist.");
        } catch (IllegalArgumentException ignored) {
        }

        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(new MockupType(), 3, nodes)
                .setExternal(nodes.get(1))
                .build();

        try {
            hc.builder().setExternal(nodes.get(1)).build();
            fail("Provided node is already external.");
        } catch (IllegalArgumentException ignored) {
        }

        hc.builder().addVariableEdge("test", nodes.get(0)).build();

        try {
            hc.builder().setExternal(3).build();
            fail("Provided ID does not correspond to a node.");
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests the intended use of builders to mark a node as not being external anymore
     */
    @Test
    public void testUnsetExternal() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(new MockupType(), 5, nodes)
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(4))
                .build();

        hc.builder().unsetExternal(nodes.get(0)).build();

        assertFalse("Node 0 should not be external anymore", hc.isExternalNode(nodes.get(0)));
        assertTrue("Node 2 should remain external", hc.isExternalNode(nodes.get(2)));
        assertTrue("Node 4 should remain external", hc.isExternalNode(nodes.get(4)));
        assertEquals("The second external node is now Node 4.", nodes.get(4), hc.externalNodeAt(1));

    }

    /**
     * Tests invalid inputs for removing external nodes.
     */
    @Test
    public void testDefensiveUnsetExternal() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        try {
            hc.builder().unsetExternal(17);
            fail("Provided ID does not exist.");
        } catch (IllegalArgumentException ignored) {
        }

        TIntArrayList nodes = new TIntArrayList();
        hc.builder().addNodes(new MockupType(), 4, nodes).build();

        try {
            hc.builder().unsetExternal(nodes.get(0)).build();
            fail("Provided node is not external.");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            hc.builder()
                    .addVariableEdge("test", nodes.get(0))
                    .unsetExternal(4)
                    .build();
            fail("Provided ID does not correspond to a node.");
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests the intended usage of builders to add variable edges.
     */
    @Test
    public void testAddVariableEdge() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(new MockupType(), 2, nodes)
                .addVariableEdge("firstVar", nodes.get(0))
                .addVariableEdge("second", nodes.get(0))
                .addVariableEdge("third", nodes.get(1))
                .build();

        assertEquals("Variable 'second' is attached to Node 0.", nodes.get(0), hc.targetOf(hc.variableWith("second")));
        assertEquals("Variable 'third' is attached to Node 1.", nodes.get(1), hc.targetOf(hc.variableWith("third")));
        assertEquals("There should be three variables.", 3, hc.countVariableEdges());

        assertEquals("Variable with ID 3 should have name 'second'", "second", hc.nameOf(3));
    }

    /**
     * Tests invalid inputs when adding variable edges.
     */
    @Test
    public void testDefensiveAddVariableEdge() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        try {
            hc.builder().addVariableEdge("v", 1).build();
            fail("Provided ID does not exist.");
        } catch (IllegalArgumentException ignored) {
        }

        TIntArrayList nodes = new TIntArrayList();
        hc.builder().addNodes(new MockupType(), 3, nodes).build();

        try {
            hc.builder().addVariableEdge(null, nodes.get(1));
            fail("Variable name must not be null.");
        } catch (NullPointerException ignored) {
        }

        try {
            hc.builder()
                    .addVariableEdge("test", nodes.get(1))
                    .addVariableEdge("test", nodes.get(1))
                    .build();
            fail("Variable with provided name already exists.");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            hc.builder().addVariableEdge("foo", hc.variableWith("test")).build();
            fail("Provided ID does not correspond to a node.");
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests the intended use of builders to remove variable edges.
     */
    @Test
    public void testRemoveVariableEdge() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(new MockupType(), 4, nodes)
                .addVariableEdge("v0", nodes.get(0))
                .addVariableEdge("v1", nodes.get(1))
                .addVariableEdge("v2", nodes.get(2))
                .addVariableEdge("v3", nodes.get(3))
                .addVariableEdge("v3b", nodes.get(3))
                .build();

        hc.builder().removeVariableEdge(hc.variableWith("v0")).build();
        assertEquals("Variable v0 should be removed.", 0, hc.attachedVariablesOf(nodes.get(0)).size());

        hc.builder().removeVariableEdge(hc.variableWith("v3b")).build();
        assertEquals("Variable v3b should be removed.", 1, hc.attachedVariablesOf(nodes.get(3)).size());
        assertEquals("Variable v3 should still exist.", "v3", hc.nameOf(hc.attachedVariablesOf(nodes.get(3)).get(0)));
    }

    /**
     * Tests invalid inputs for removing variable edges.
     */
    @Test
    public void testDefensiveRemoveVariableEdge() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(new MockupType(), 2, nodes)
                .addVariableEdge("v1", nodes.get(0))
                .addVariableEdge("v2", nodes.get(1))
                .addVariableEdge("v3", nodes.get(1))
                .build();

        try {
            hc.builder().removeVariableEdge(9).build();
            fail("Provided ID does not exist.");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            hc.builder()
                    .removeVariableEdge(nodes.get(0))
                    .build();
            fail("Provided ID does not correspond to a variable");
        } catch (IllegalArgumentException ignored) {
        }

    }

    /**
     * Tests the intended usage of builders to add nonterminal edges.
     */
    @Test
    public void testAddNonterminalEdge() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        Nonterminal nt = new MockupNonterminal("ntlab", 3);

        assertEquals("There should be no nonterminal edges yet.", 0, hc.countNonterminalEdges());

        hc.builder()
                .addNodes(new MockupType(), 3, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{0, 1, 2}))
                .build();

        assertEquals("There should be exactly one nonterminal edge.", 1, hc.countNonterminalEdges());
        assertEquals("The label of the created edge is nt.", nt, hc.labelOf(3));
        assertEquals("There should be 3 nodes attached to the new edge.", 3, hc.attachedNodesOf(3).size());
        assertEquals("The rank of the new edge should be 3.", 3, hc.rankOf(3));

    }

    /**
     * Tests invalid inputs for adding nonterminal edge.
     */
    @Test
    public void testDefensiveAddNonterminalEdge() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        try {
            hc.builder().addNonterminalEdge(null, null).build();
            fail("The provided label must not be null.");
        } catch (NullPointerException ignored) {
        }

        Nonterminal nt = new MockupNonterminal("test", 2);

        try {
            hc.builder().addNonterminalEdge(nt, new TIntArrayList()).build();
            fail("The rank of the provided label and the size of the list of attached nodes must coincide.");
        } catch (IllegalArgumentException ignored) {
        }

        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(new MockupType(), 2, nodes)
                .addVariableEdge("var", nodes.get(0))
                .build();

        try {
            TIntArrayList attached = new TIntArrayList(new int[]{nodes.get(0), 2});
            hc.builder().addNonterminalEdge(nt, attached).build();
            fail("One of the provided attached nodes is not a node.");
        } catch (IllegalArgumentException ignored) {
        }


    }

    /**
     * Tests the intended usage of builders for removing nonterminal edges.
     */
    @Test
    public void testRemoveNonterminalEdge() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        Nonterminal nt = new MockupNonterminal("label", 5);

        hc.builder()
                .addNodes(new MockupType(), 5, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{0, 1, 2, 3, 4}))
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{0, 1, 2, 4, 3}))
                .removeNonterminalEdge(5)
                .build();

        assertEquals("There should be one NT edge remaining.", 1, hc.countNonterminalEdges());
        assertEquals("The list of nonterminal edges should contain 1 element.", 1, hc.nonterminalEdges().size());
        assertTrue("The remaining edge should have its 5. attached node be attached to 3.",
                hc.attachedNodesOf(hc.nonterminalEdges().get(0)).get(4) == 3);
    }

    /**
     * Tests invalid inputs when removing nonterminal edges.
     */
    @Test
    public void testDefensiveRemoveNonterminalEdge() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        Nonterminal nt = new MockupNonterminal("label", 5);

        hc.builder()
                .addNodes(new MockupType(), 5, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{0, 1, 2, 3, 4}))
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{0, 1, 2, 4, 3}))
                .build();

        try {
            hc.builder().removeNonterminalEdge(23).build();
            fail("Provided ID does not exist.");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            hc.builder().removeNonterminalEdge(nodes.get(2)).build();
            fail("Provided ID does not correspond to a nonterminal edge.");
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests the intended usage of builders to substitute a label of a nonterminal edge.
     */
    @Test
    public void testReplaceNonterminal() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(new MockupType(), 3, nodes)
                .addNonterminalEdge(new MockupNonterminal("oldLabel", 3), nodes)
                .replaceNonterminal(3, new MockupNonterminal("newLabel", 3))
                .build();

        assertEquals("The label of the nonterminal should be set to 'newLabel'.",
                "newLabel",
                hc.labelOf(hc.nonterminalEdges().get(0)).toString()
        );
    }

    /**
     * Tests invalid inputs when replacing the label of a nonterminal edge by a new nonterminal.
     */
    @Test
    public void testDefensiveReplaceNonterminal() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(new MockupType(), 3, nodes)
                .addNonterminalEdge(new MockupNonterminal("oldLabel", 3), nodes)
                .build();

        try {
            hc.builder().replaceNonterminal(23, new MockupNonterminal("newLabel", 3)).build();
            fail("The provided ID does not exist.");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            hc.builder().replaceNonterminal(nodes.get(1), new MockupNonterminal("newLabel", 3)).build();
            fail("The provided ID does not correspond to a nonterminal edge.");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            hc.builder().replaceNonterminal(3, null).build();
            fail("The provided new label must not be null.");
        } catch (NullPointerException ignored) {
        }

        try {
            hc.builder().replaceNonterminal(3, new MockupNonterminal("invalidLabel", 2)).build();
            fail("The rank of the provided label does not match the rank of the original label.");
        } catch (IllegalArgumentException ignored) {
        }

    }

    /**
     * Tests the intended usage of builders to perform hyperedge replacement.
     */
    @Test
    public void testReplaceNonterminalEdge() {

        HeapConfiguration source = new InternalHeapConfiguration();
        TIntArrayList srcNodes = new TIntArrayList();

        source.builder()
                .addNodes(new MockupType(), 3, srcNodes)
                .addSelector(srcNodes.get(1), new MockupSelector("sel"), srcNodes.get(2))
                .addNonterminalEdge(new MockupNonterminal("nt", 2), new TIntArrayList(new int[]{srcNodes.get(1), srcNodes.get(2)}))
                .build();

        HeapConfiguration repl = new InternalHeapConfiguration();
        TIntArrayList replNodes = new TIntArrayList();

        repl.builder()
                .addNodes(new MockupType(), 3, replNodes)
                .addSelector(replNodes.get(0), new MockupSelector("next"), replNodes.get(1))
                .addSelector(replNodes.get(1), new MockupSelector("prev"), replNodes.get(0))
                .setExternal(replNodes.get(0))
                .setExternal(replNodes.get(1))
                .addNonterminalEdge(new MockupNonterminal("NEW", 1), new TIntArrayList(new int[]{replNodes.get(1)}))
                .addSelector(replNodes.get(0), new MockupSelector("to2"), replNodes.get(2))
                .build();

        source.builder()
                .replaceNonterminalEdge(
                        source.nonterminalEdges().get(0),
                        repl
                )
                .build();

        assertEquals("The total number of nodes should be 4 (3 + 1 non-external node).", 4, source.countNodes());

        assertEquals("There should be no external nodes.", 0, source.countExternalNodes());

        assertEquals("There should be exactly one nonterminal hyperedge", 1, source.countNonterminalEdges());

        assertEquals("The single nonterminal hyperedge should correspond to the one in repl.",
                "NEW", source.labelOf(source.nonterminalEdges().get(0)).toString());

        assertEquals("Node 1 should now have three outgoing selector edges.",
                3, source.selectorLabelsOf(srcNodes.get(1)).size());

        assertEquals("Selector edge 'next' should have been added between the two nodes attached to the nonterminal edge 'nt'.",
                "next", source.selectorLabelsOf(srcNodes.get(1)).get(1).toString());
    }

    /**
     * Tests the intended usage of builders to perform hyperedge replacement
     * in the context of interprocedural analysis with contracts.
     */
    @Test
    public void testReplaceNonterminalEdgeWithVariables() {

        HeapConfiguration source = new InternalHeapConfiguration();
        TIntArrayList srcNodes = new TIntArrayList();

        source.builder()
                .addNodes(new MockupType(), 3, srcNodes)
                .addSelector(srcNodes.get(1), new MockupSelector("sel"), srcNodes.get(2))
                .addNonterminalEdge(new MockupNonterminal("nt", 2), new TIntArrayList(new int[]{srcNodes.get(1), srcNodes.get(2)}))
                .build();

        HeapConfiguration repl = new InternalHeapConfiguration();
        TIntArrayList replNodes = new TIntArrayList();

        repl.builder()
                .addNodes(new MockupType(), 3, replNodes)
                .addSelector(replNodes.get(0), new MockupSelector("next"), replNodes.get(1))
                .addSelector(replNodes.get(1), new MockupSelector("prev"), replNodes.get(0))
                .setExternal(replNodes.get(0))
                .setExternal(replNodes.get(1))
                .addNonterminalEdge(new MockupNonterminal("NEW", 1), new TIntArrayList(new int[]{replNodes.get(1)}))
                .addSelector(replNodes.get(0), new MockupSelector("to2"), replNodes.get(2))
                .addVariableEdge("@return", replNodes.get(0))
                .build();

        source.builder()
                .replaceNonterminalEdge(
                        source.nonterminalEdges().get(0),
                        repl
                )
                .build();

        assertEquals("There should be exactly one variable edge", 1, source.countVariableEdges());

        assertEquals("The name of the single variable edge should correspond to the one in repl.",
                "@return", source.nameOf(source.variableEdges().get(0)).toString());

        assertEquals("Node 1 should be attached to node which used to be the first tentacle",
                srcNodes.get(1), source.variableTargetOf("@return"));

    }

    /**
     * Tests invalid inputs when replacing nonterminal edges by HeapConfigurations.
     */
    @Test
    public void testDefensiveReplaceNonterminalEdge() {

        HeapConfiguration source = new InternalHeapConfiguration();
        TIntArrayList srcNodes = new TIntArrayList();

        source.builder()
                .addNodes(new MockupType(), 3, srcNodes)
                .addSelector(srcNodes.get(1), new MockupSelector("sel"), srcNodes.get(2))
                .addNonterminalEdge(new MockupNonterminal("nt", 2), new TIntArrayList(new int[]{srcNodes.get(1), srcNodes.get(2)}))
                .build();

        try {
            source.builder().replaceNonterminalEdge(3, null);
            fail("Replacement HeapConfiguration must not be null.");
        } catch (NullPointerException ignored) {
        }

        HeapConfiguration repl = new InternalHeapConfiguration();

        try {
            source.builder().replaceNonterminalEdge(srcNodes.get(1), repl);
            fail("Provided ID does not correspond to a nonterminal edge.");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            source.builder().replaceNonterminalEdge(3, repl);
            fail("The rank of the provided nonterminal does not match the number of external nodes of the replacement.");
        } catch (IllegalArgumentException ignored) {
        }

    }

    @Test
    public void testReplaceNonterminalEdgeDoubleTentacle() {

        HeapConfiguration source = new InternalHeapConfiguration();
        TIntArrayList srcNodes = new TIntArrayList();

        source.builder()
                .addNodes(new MockupType(), 3, srcNodes)
                .addNonterminalEdge(new MockupNonterminal("nt", 2), new TIntArrayList(new int[]{srcNodes.get(1), srcNodes.get(1)}))
                .build();

        HeapConfiguration rule = new InternalHeapConfiguration();
        TIntArrayList ruleNodes = new TIntArrayList();
        rule.builder()
                .addNodes(new MockupType(), 2, ruleNodes)
                .addSelector(ruleNodes.get(0), new MockupSelector("sel"), ruleNodes.get(1))
                .setExternal(ruleNodes.get(0))
                .setExternal(ruleNodes.get(1))
                .build();

        source.builder().replaceNonterminalEdge(3, rule).build();

        int target = source.selectorTargetOf(srcNodes.get(1), new MockupSelector("sel"));
        assertEquals(srcNodes.get(1), target);

    }

    /**
     * Tests the intended usage of builders to replace matchings.
     */
    @Test
    public void testReplaceMatching() {

        HeapConfiguration pattern = new InternalHeapConfiguration();
        TIntArrayList patternNodes = new TIntArrayList();
        SelectorLabel sel = new MockupSelector("sel");
        Nonterminal nt = new MockupNonterminal("myNt", 2);

        pattern.builder()
                .addNodes(new MockupType(), 3, patternNodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{patternNodes.get(1), patternNodes.get(2)}))
                .addSelector(patternNodes.get(0), sel, patternNodes.get(1))
                .setExternal(patternNodes.get(0))
                .setExternal(patternNodes.get(1))
                .build();


        HeapConfiguration target = new InternalHeapConfiguration();
        TIntArrayList targetNodes = new TIntArrayList();

        target.builder()
                .addNodes(new MockupType(), 4, targetNodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{targetNodes.get(3), targetNodes.get(1)}))
                .addSelector(targetNodes.get(0), sel, targetNodes.get(2))
                .addSelector(targetNodes.get(2), sel, targetNodes.get(3))
                .build();

        // Note that these are private IDs!
        Morphism morphism = new Morphism(new int[]{
                2,
                3,
                1,
                4
        });
        Matching matching = new InternalMatching(pattern, morphism, target);

        target.builder()
                .replaceMatching(matching, nt)
                .build();

        assertEquals("The resulting HC should have three nodes.", 3, target.countNodes());

        assertEquals("There should be one nonterminal edge remaining.", 1, target.countNonterminalEdges());

        assertEquals("The single nonterminal edge should be attached to the externals of the pattern, i.e. 0 -> 2",
                2, target.attachedNodesOf(target.nonterminalEdges().get(0)).get(0));
        assertEquals("The single nonterminal edge should be attached to the externals of the pattern, i.e. 1 -> 3",
                3, target.attachedNodesOf(target.nonterminalEdges().get(0)).get(1));
    }

    /**
     * Tests invalid inputs when replacing matchings by nonterminal edges.
     */
    @Test
    public void testDefensiveReplaceMatching() {

        HeapConfiguration pattern = new InternalHeapConfiguration();
        TIntArrayList patternNodes = new TIntArrayList();
        SelectorLabel sel = new MockupSelector("sel");
        Nonterminal nt = new MockupNonterminal("myNt", 2);

        pattern.builder()
                .addNodes(new MockupType(), 3, patternNodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{patternNodes.get(1), patternNodes.get(2)}))
                .addSelector(patternNodes.get(0), sel, patternNodes.get(1))
                .setExternal(patternNodes.get(0))
                .setExternal(patternNodes.get(1))
                .build();


        HeapConfiguration target = new InternalHeapConfiguration();
        TIntArrayList targetNodes = new TIntArrayList();

        target.builder()
                .addNodes(new MockupType(), 4, targetNodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{targetNodes.get(3), targetNodes.get(1)}))
                .addSelector(targetNodes.get(0), sel, targetNodes.get(2))
                .addSelector(targetNodes.get(2), sel, targetNodes.get(3))
                .build();

        // Note that these are private IDs!
        Morphism morphism = new Morphism(new int[]{
                2,
                3,
                1,
                4
        });
        Matching matching = new InternalMatching(pattern, morphism, target);

        try {
            target.builder().replaceMatching(null, nt).build();
            fail("Provided matching may not be null.");
        } catch (NullPointerException ignored) {
        }

        try {
            target.builder().replaceMatching(matching, null).build();
            fail("Provided nonterminal may not be null.");
        } catch (NullPointerException ignored) {
        }

        try {
            HeapConfiguration wrongPattern = new InternalHeapConfiguration();
            target.builder().replaceMatching(new InternalMatching(wrongPattern, morphism, target), nt).build();
            fail("The number of external nodes of the matching pattern have to coincide with the rank of the provided nonterminal.");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testChangeNodeType() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        SelectorLabel s1 = new MockupSelector("first");
        SelectorLabel s2 = new MockupSelector("second");
        SelectorLabel s3 = new MockupSelector("third");
        MockupType type = new MockupType();
        MockupType otherType = new MockupType();
        TIntArrayList nodes = new TIntArrayList();

        hc.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(1), s2, nodes.get(0))
                .addSelector(nodes.get(1), s1, nodes.get(0))
                .replaceSelector(nodes.get(1), s2, s3)
                .replaceNodeType(nodes.get(1), otherType)
                .build();

        assertEquals(hc.nodeTypeOf(nodes.get(0)), type);
        assertEquals(hc.nodeTypeOf(nodes.get(1)), otherType);
        assertEquals(hc.nodeTypeOf(nodes.get(2)), type);
    }

    @Test
    public void testMergeExternals() {


        SelectorLabel s1 = new MockupSelector("first");
        SelectorLabel s2 = new MockupSelector("second");
        MockupType type = new MockupType();

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration test = new InternalHeapConfiguration().builder()
                .addNodes(type, 4, nodes)
                .addSelector(nodes.get(1), s2, nodes.get(0))
                .addSelector(nodes.get(1), s1, nodes.get(2))
                .addSelector(nodes.get(2), s1, nodes.get(3))
                .addSelector(nodes.get(2), s2, nodes.get(1))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(3))
                .build();

        TIntArrayList expectedNodes = new TIntArrayList();
        HeapConfiguration expected = new InternalHeapConfiguration().builder()
                .addNodes(type, 3, expectedNodes)
                .addSelector(expectedNodes.get(1), s2, expectedNodes.get(0))
                .addSelector(expectedNodes.get(1), s1, expectedNodes.get(2))
                .addSelector(expectedNodes.get(2), s1, expectedNodes.get(0))
                .addSelector(expectedNodes.get(2), s2, expectedNodes.get(1))
                .setExternal(expectedNodes.get(0))
                .setExternal(expectedNodes.get(1))
                .setExternal(expectedNodes.get(2))
                .build();

        TIntArrayList extMapping = new TIntArrayList();
        extMapping.add(0);
        extMapping.add(1);
        extMapping.add(2);
        extMapping.add(0);

        HeapConfiguration result = test.clone().builder().mergeExternals(extMapping).build();
        assertEquals(expected, result);
    }

}
