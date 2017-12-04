package de.rwth.i2.attestor.graph.digraph;

import de.rwth.i2.attestor.graph.heap.Variable;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LabeledDigraphTest {

    private void addNodeWithVariableLabel(LabeledDigraph graph, String label, int succCapacity, int predCapacity) {

        graph.addNode(new Variable(label), succCapacity, predCapacity);
    }


    @Test
    public void createEmptyTest() {

        int capacity = 5;
        LabeledDigraph digraph = new LabeledDigraph(capacity);
        assertEquals(0, digraph.size());
    }

    @Test
    public void addNodeTest() {

        int capacity = 10;
        int succCapacity = 5;
        int predCapacity = 13;
        LabeledDigraph digraph = new LabeledDigraph(capacity);
        addNodeWithVariableLabel(digraph, "label", succCapacity, predCapacity);

        assertEquals(1, digraph.size());
        assertEquals(0, digraph.successorSizeOf(0));

        addNodeWithVariableLabel(digraph, "label 2", succCapacity, predCapacity);
        assertEquals(2, digraph.size());
    }

    @Test
    public void nodeLabelOfTest() {

        LabeledDigraph digraph = new LabeledDigraph(3);

        String label1 = "label";
        addNodeWithVariableLabel(digraph, label1, 1, 2);
        assertEquals(new Variable("label"), digraph.nodeLabelOf(0));

        assertNull(digraph.nodeLabelOf(17));
    }

    @Test
    public void containsNodeTest() {

        LabeledDigraph digraph = new LabeledDigraph(3);
        addNodeWithVariableLabel(digraph, "foo", 13, 0);

        assertTrue(digraph.containsNode(0));
        assertFalse(digraph.containsNode(-5));
    }

    @Test
    public void removeNodeTest() {

        LabeledDigraph digraph = new LabeledDigraph(3);
        addNodeWithVariableLabel(digraph, "foo", 13, 0);
        assertTrue(digraph.isPacked());

        assertFalse(digraph.removeNodeAt(2));
        assertEquals(1, digraph.size());

        assertTrue(digraph.removeNodeAt(0));
        assertEquals(1, digraph.size());
        assertFalse(digraph.isPacked());

        addNodeWithVariableLabel(digraph, "buh", 23, 42);
        assertEquals(2, digraph.size());
        assertFalse(digraph.isPacked());
    }

    @Test
    public void addEdgeTest() {

        LabeledDigraph digraph = new LabeledDigraph(3);
        addNodeWithVariableLabel(digraph, "foo", 13, 0);
        assertEquals(0, digraph.successorSizeOf(0));
        assertEquals(0, digraph.predecessorSizeOf(0));

        assertEquals(0, digraph.successorSizeOf(17));
        assertEquals(0, digraph.predecessorSizeOf(17));

        digraph.addEdge(0, "bar", 1);
        assertEquals(0, digraph.successorSizeOf(0));
        assertEquals(0, digraph.predecessorSizeOf(0));

        digraph.addEdge(0, null, 0);
        assertEquals(0, digraph.successorSizeOf(0));
        assertEquals(0, digraph.predecessorSizeOf(0));

        digraph.addEdge(0, "bar", 0);
        assertEquals(1, digraph.successorSizeOf(0));
        assertEquals(1, digraph.predecessorSizeOf(0));

        addNodeWithVariableLabel(digraph, "foo", 13, 0);
        assertEquals(0, digraph.successorSizeOf(1));
        assertEquals(0, digraph.predecessorSizeOf(1));

        digraph.removeNodeAt(0);
        assertEquals(0, digraph.successorSizeOf(0));
        assertEquals(0, digraph.predecessorSizeOf(0));

        digraph.addEdge(0, "wth", 1);
        assertEquals(0, digraph.successorSizeOf(0));
        assertEquals(0, digraph.predecessorSizeOf(0));

        digraph = new LabeledDigraph(3);
        addNodeWithVariableLabel(digraph, "first", 3, 3);
        addNodeWithVariableLabel(digraph, "second", 3, 3);
        addNodeWithVariableLabel(digraph, "third", 3, 3);
        addNodeWithVariableLabel(digraph, "fourth", 3, 3);
        digraph.addEdge(0, "e01", 1);
        digraph.addEdge(0, "e02", 2);
        digraph.addEdge(0, "e03", 3);
        digraph.addEdge(1, "e12", 2);
        assertEquals(3, digraph.successorSizeOf(0));
        assertEquals(0, digraph.predecessorSizeOf(0));
        assertEquals(2, digraph.predecessorSizeOf(2));
        assertEquals("e12", digraph.edgeLabelAt(1, 0));
        assertNull(digraph.edgeLabelAt(1, 1));
        assertNull(digraph.edgeLabelAt(4, 1));
        assertEquals("e03", digraph.edgeLabelAt(0, 2));
    }

    @Test
    public void removeNodeWithEdgesTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "first", 3, 3);
        addNodeWithVariableLabel(digraph, "second", 3, 3);
        addNodeWithVariableLabel(digraph, "third", 3, 3);
        addNodeWithVariableLabel(digraph, "fourth", 3, 3);
        digraph.addEdge(0, "e01", 1);
        digraph.addEdge(0, "e02", 2);
        digraph.addEdge(0, "e03", 3);
        digraph.addEdge(1, "e12", 2);
        digraph.addEdge(2, "e03", 3);
        digraph.addEdge(2, "e03", 2);

        digraph.removeNodeAt(0);
        assertEquals(0, digraph.successorSizeOf(0));
        assertEquals(0, digraph.predecessorSizeOf(1));
        assertEquals(2, digraph.predecessorSizeOf(2));

        assertEquals(2, digraph.successorSizeOf(2));
        assertTrue(digraph.removeNodeAt(3));
    }

    @Test
    public void removeEdgeAtTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 3, 3);
        addNodeWithVariableLabel(digraph, "y", 3, 3);
        digraph.addEdge(0, "e", 1);
        digraph.addEdge(1, "f", 0);
        digraph.addEdge(0, "g", 0);
        digraph.addEdge(1, "h", 1);

        assertTrue(digraph.removeEdgeAt(0, 1));
        assertEquals(1, digraph.successorSizeOf(0));
        assertEquals(1, digraph.predecessorSizeOf(0));
        assertNull(digraph.edgeLabelAt(0, 1));
    }

    @Test
    public void removeEdgeLabelAtTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 3, 3);
        addNodeWithVariableLabel(digraph, "y", 3, 3);
        digraph.addEdge(0, "e", 1);
        digraph.addEdge(1, "f", 0);
        digraph.addEdge(0, "g", 0);
        digraph.addEdge(1, "h", 1);

        assertTrue(digraph.removeEdgeLabelAt(1, "h"));
        assertEquals(0, digraph.successorsOf(1).get(0));
        assertFalse(digraph.removeEdgeLabelAt(1, "z"));
    }

    @Test
    public void setExternalTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 3, 3);
        addNodeWithVariableLabel(digraph, "y", 3, 3);
        addNodeWithVariableLabel(digraph, "z", 3, 3);
        assertEquals(0, digraph.rank());

        digraph.setExternal(0);
        assertEquals(1, digraph.rank());

        digraph.setExternal(-2);
        assertEquals(1, digraph.rank());

        digraph.setExternal(1);
        assertEquals(2, digraph.rank());
    }

    @Test
    public void removeExternalNodeTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 3, 3);
        addNodeWithVariableLabel(digraph, "y", 3, 3);
        digraph.addEdge(0, "e", 1);
        digraph.addEdge(1, "f", 0);
        digraph.addEdge(0, "g", 0);
        digraph.addEdge(1, "h", 1);
        digraph.setExternal(0);
        digraph.setExternal(1);

        assertEquals(2, digraph.rank());

        digraph.removeNodeAt(1);
        assertEquals(1, digraph.rank());

        addNodeWithVariableLabel(digraph, "foo", 0, 0);
        digraph.setExternal(2);
        assertEquals(2, digraph.rank());
    }

    @Test
    public void externalIndexOfTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 3, 3);
        addNodeWithVariableLabel(digraph, "y", 3, 3);
        addNodeWithVariableLabel(digraph, "z", 3, 3);
        digraph.setExternal(0);
        digraph.setExternal(2);

        assertEquals(2, digraph.rank());
        assertEquals(0, digraph.externalPosOf(0));
        assertEquals(LabeledDigraph.INVALID, digraph.externalPosOf(1));
        assertEquals(1, digraph.externalPosOf(2));
        assertEquals(LabeledDigraph.INVALID, digraph.externalPosOf(17));
    }

    @Test
    public void isExternalTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 3, 3);
        addNodeWithVariableLabel(digraph, "y", 3, 3);
        addNodeWithVariableLabel(digraph, "z", 3, 3);
        digraph.setExternal(0);
        digraph.setExternal(2);

        assertTrue(digraph.isExternal(2));
        assertFalse(digraph.isExternal(1));
    }


    @Test
    public void unsetExternalTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 3, 3);
        addNodeWithVariableLabel(digraph, "y", 3, 3);
        addNodeWithVariableLabel(digraph, "z", 3, 3);
        digraph.setExternal(0);
        digraph.setExternal(2);
        digraph.unsetExternal(2);

        assertEquals(0, digraph.externalPosOf(0));
        assertEquals(LabeledDigraph.INVALID, digraph.externalPosOf(1));

        digraph.setExternal(2);
        digraph.unsetExternal(0);
        digraph.setExternal(0);
        assertEquals(0, digraph.externalPosOf(2));
        assertEquals(1, digraph.externalPosOf(0));

        digraph.unsetExternal(17);
        assertEquals(2, digraph.rank());
    }

    @Test
    public void replaceEdgeLabelTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 1, 1);
        addNodeWithVariableLabel(digraph, "y", 1, 1);
        digraph.addEdge(1, "sel", 0);
        digraph.addEdge(0, "sel", 1);

        digraph.replaceEdgeLabel(1, "sel", "foo");
        assertEquals("foo", digraph.edgeLabelAt(1, 0));
        assertEquals("sel", digraph.edgeLabelAt(0, 0));
    }

    @Test
    public void posOfEdgeLabelTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 1, 1);
        addNodeWithVariableLabel(digraph, "y", 1, 1);
        digraph.addEdge(1, "e0", 0);
        digraph.addEdge(1, "e1", 0);
        digraph.addEdge(1, "e2", 0);
        digraph.addEdge(1, "e3", 0);
        digraph.addEdge(1, "e4", 0);

        assertEquals(3, digraph.posOfEdgeLabel(1, "e3"));
        assertEquals(LabeledDigraph.INVALID, digraph.posOfEdgeLabel(1, "foo"));
        assertTrue(digraph.containsEdgeLabel(1, "e1"));
        assertFalse(digraph.containsEdgeLabel(1, "e17"));
    }

    @Test
    public void predecessorsOfAndSuccessorsOfTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 1, 1);
        addNodeWithVariableLabel(digraph, "y", 1, 1);
        addNodeWithVariableLabel(digraph, "z", 1, 1);
        digraph.addEdge(1, "e0", 0);
        digraph.addEdge(1, "e1", 0);
        digraph.addEdge(1, "e2", 2);
        digraph.addEdge(1, "e3", 0);
        digraph.addEdge(1, "e4", 2);

        TIntArrayList list = digraph.predecessorsOf(0);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(1, list.get(0));
        assertEquals(1, list.get(0));

        list = digraph.predecessorsOf(9);
        assertNull(list);

        list = digraph.successorsOf(1);
        assertEquals(5, list.size());

        list = digraph.successorsOf(-1);
        assertNull(list);
    }

    @Test
    public void edgeLabelsFromToTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 1, 1);
        addNodeWithVariableLabel(digraph, "y", 1, 1);
        addNodeWithVariableLabel(digraph, "z", 1, 1);
        digraph.addEdge(1, "ex", 0);
        digraph.addEdge(1, "ex", 0);
        digraph.addEdge(1, "e2", 2);
        digraph.addEdge(1, "e3", 1);

        List<Object> l = digraph.edgeLabelsFromTo(1, 0);
        assertEquals(2, l.size());
        assertEquals("ex", l.get(0));
        assertEquals("ex", l.get(1));

        assertEquals("e2", digraph.edgeLabelsFromTo(1, 2).get(0));

        assertNull(digraph.edgeLabelsFromTo(17, 23));
    }

    @Test
    public void packTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 3, 3);
        addNodeWithVariableLabel(digraph, "y", 3, 3);
        addNodeWithVariableLabel(digraph, "z", 3, 3);
        digraph.addEdge(0, "e1", 2);
        digraph.addEdge(2, "e2", 1);
        digraph.addEdge(1, "e3", 1);
        digraph.addEdge(2, "e4", 0);

        digraph.pack();
        assertEquals(3, digraph.size());

        digraph.removeNodeAt(1);
        assertEquals(3, digraph.size());

        digraph.pack();
        assertEquals(2, digraph.size());
        assertEquals(new Variable("z"), digraph.nodeLabelOf(1));
        assertEquals(0, digraph.successorsOf(1).get(0));
        assertEquals(1, digraph.predecessorsOf(0).get(0));

    }

    @Test
    public void copyTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 3, 3);
        addNodeWithVariableLabel(digraph, "y", 3, 3);
        addNodeWithVariableLabel(digraph, "z", 3, 3);
        digraph.addEdge(0, "e1", 1);
        digraph.addEdge(0, "e2", 2);
        digraph.addEdge(1, "e1", 2);
        digraph.addEdge(2, "e1", 1);
        digraph.setExternal(2);

        LabeledDigraph copy = new LabeledDigraph(digraph);

        assertEquals(3, copy.size());
        assertEquals("e2", copy.edgeLabelAt(0, 1));
        assertEquals(1, copy.successorsOf(0).get(0));
        assertEquals(0, copy.externalPosOf(2));
        assertEquals(1, copy.predecessorsOf(2).get(1));
    }

    @Test
    public void externalNodeAtTest() {

        LabeledDigraph digraph = new LabeledDigraph(7);
        addNodeWithVariableLabel(digraph, "x", 3, 3);
        addNodeWithVariableLabel(digraph, "y", 3, 3);
        addNodeWithVariableLabel(digraph, "z", 3, 3);
        digraph.setExternal(2);
        digraph.setExternal(1);
        digraph.setExternal(0);

        assertEquals(0, digraph.externalNodeAt(2));
        assertEquals(LabeledDigraph.INVALID, digraph.externalNodeAt(3));
    }

}
