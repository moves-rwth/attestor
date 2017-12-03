package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.morphism.Morphism;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HeapConfigurationTestOriginal {

    private final static MockupType type = new MockupType();
    private final static MockupSelector sel1 = new MockupSelector("sel1");
    private final static MockupSelector sel2 = new MockupSelector("sel2");
    private final static MockupNonterminal nt1 = new MockupNonterminal("nt1", 3);
    private final static MockupNonterminal nt2 = new MockupNonterminal("nt2", 2);

    private HeapConfiguration result;
    private TIntArrayList nodes;

    @Before
    public void setUp() {

        result = new InternalHeapConfiguration();
        nodes = new TIntArrayList();

        result.builder()
                .addNodes(type, 10, nodes)
                .addSelector(nodes.get(0), sel1, nodes.get(1))
                .addSelector(nodes.get(1), sel1, nodes.get(2))
                .addSelector(nodes.get(2), sel1, nodes.get(3))
                .addSelector(nodes.get(3), sel1, nodes.get(4))
                .addSelector(nodes.get(4), sel1, nodes.get(5))
                .addSelector(nodes.get(5), sel1, nodes.get(6))
                .addSelector(nodes.get(6), sel1, nodes.get(7))
                .addSelector(nodes.get(1), sel2, nodes.get(0))
                .addSelector(nodes.get(2), sel2, nodes.get(1))
                .addSelector(nodes.get(3), sel2, nodes.get(2))
                .addSelector(nodes.get(4), sel2, nodes.get(3))
                .addSelector(nodes.get(5), sel2, nodes.get(4))
                .addSelector(nodes.get(6), sel2, nodes.get(5))
                .addSelector(nodes.get(7), sel2, nodes.get(6))
                .addNonterminalEdge(nt1, new TIntArrayList(new int[]{nodes.get(7), nodes.get(8), nodes.get(9)}))
                .addVariableEdge("x", nodes.get(0))
                .addVariableEdge("y", nodes.get(7))
                .addVariableEdge("z", nodes.get(9))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(8))
                .build();
    }

    @Test
    public void createTestSimple() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        assertEquals(0, hc.countNodes());

        HeapConfigurationBuilder builder = hc.builder();
        assertTrue(builder == hc.builder());

        nodes.clear();

        assertEquals(3, hc.builder().addNodes(type, 3, nodes).build().countNodes());
        assertEquals(3, nodes.size());

        hc.builder().addNodes(type, 1, nodes);
        assertEquals(type, hc.nodeTypeOf(nodes.get(3)));

        hc.builder().addSelector(nodes.get(3), sel1, nodes.get(3)).build();
        assertTrue(hc.successorNodesOf(nodes.get(3)).contains(nodes.get(3)));
        assertEquals(1, hc.successorNodesOf(nodes.get(3)).size());

        assertEquals("node n has itself as only successor",
                nodes.get(3), hc.successorNodesOf(nodes.get(3)).get(0));

        try {
            hc.builder()
                    .addNonterminalEdge(nt1, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                    .build();
            assertTrue("should throw exception.", false);
        } catch (IllegalArgumentException e) {
            // expected
        }

        hc.builder()
                .addNonterminalEdge(nt1, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(2)}))
                .build();

        assertEquals(1, hc.countNonterminalEdges());
        assertEquals(1, hc.nonterminalEdges().size());
        assertEquals(nt1, hc.labelOf(hc.nonterminalEdges().get(0)));
        assertEquals(nt1.getRank(), hc.rankOf(hc.nonterminalEdges().get(0)));
        assertEquals(nodes.get(2), hc.attachedNodesOf(hc.nonterminalEdges().get(0)).get(2));
        assertEquals(4, hc.countNodes());

        assertEquals(1, hc.attachedNonterminalEdgesOf(nodes.get(1)).size());
        assertTrue("wrong attached nt edge. Expected: " + hc.nonterminalEdges().get(0)
                        + ", but found only: " + hc.attachedNonterminalEdgesOf(nodes.get(1)).get(0),
                hc.attachedNonterminalEdgesOf(nodes.get(1)).contains(hc.nonterminalEdges().get(0)));

        hc.builder().addVariableEdge("$r5", nodes.get(1)).build();
        assertEquals(1, hc.countVariableEdges());
        assertEquals("$r5", hc.nameOf(hc.variableEdges().get(0)));
        assertEquals(nodes.get(1), hc.targetOf(hc.variableEdges().get(0)));
        assertEquals(hc.variableEdges().get(0), hc.variableWith("$r5"));
        assertEquals(HeapConfiguration.INVALID_ELEMENT, hc.variableWith("my fancy variable"));

        assertEquals(1, hc.attachedVariablesOf(nodes.get(1)).size());
        assertEquals(hc.variableEdges().get(0), hc.attachedVariablesOf(nodes.get(1)).get(0));

        assertEquals("nodes.get(1) has no predecessor nodes",
                0, hc.predecessorNodesOf(nodes.get(1)).size());

        hc.builder()
                .addSelector(nodes.get(1), sel1, nodes.get(0))
                .addSelector(nodes.get(1), sel2, nodes.get(2))
                .build();

        assertEquals(2, hc.successorNodesOf(nodes.get(1)).size());

        assertEquals(hc.countNodes(), hc.nodes().size());
        assertEquals(hc.countNonterminalEdges(), hc.nonterminalEdges().size());
        assertEquals(hc.countVariableEdges(), hc.variableEdges().size());
        assertEquals(0, hc.countExternalNodes());

        hc.builder().setExternal(nodes.get(1));
        assertEquals(1, hc.countExternalNodes());
        assertTrue(hc.isExternalNode(nodes.get(1)));
        assertEquals(0, hc.externalIndexOf(nodes.get(1)));
        assertEquals(nodes.get(1), hc.externalNodeAt(0));

        hc.builder().setExternal(nodes.get(0));
        assertEquals(nodes.get(0), hc.externalNodes().get(1));
        assertEquals(nodes.get(1), hc.externalNodes().get(0));
    }

    @Test
    public void cloneTest() {

        HeapConfiguration copy = result.clone();
        assertNotNull(copy);
        assertFalse(result == copy);

        assertEquals(result.countNodes(), copy.countNodes());
        assertEquals(result.countNonterminalEdges(), copy.countNonterminalEdges());
        assertEquals(result.countVariableEdges(), copy.countVariableEdges());

        assertEquals(result.targetOf(result.variableWith("y")), copy.targetOf(result.variableWith("y")));

        assertEquals(result.predecessorNodesOf(result.nodes().get(4)).size(),
                copy.predecessorNodesOf(result.nodes().get(4)).size());
    }

    @Test
    public void removeSelectorTest() {

        TIntArrayList succ = result.successorNodesOf(nodes.get(1));

        assertEquals(2, succ.size());
        result.builder().removeSelector(nodes.get(1), sel1).build();

        succ = result.successorNodesOf(nodes.get(1));
        assertEquals(1, succ.size());
        assertEquals(nodes.get(0), succ.get(0));

        result.builder().removeSelector(nodes.get(1), sel2).build();
        succ = result.successorNodesOf(nodes.get(1));
        assertEquals(0, succ.size());

        assertEquals(0, result.predecessorNodesOf(nodes.get(0)).size());
    }

    @Test
    public void removeNonterminalEdgeTest() {

        result.builder()
                .removeNonterminalEdge(result.nonterminalEdges().get(0))
                .build();

        assertEquals(0, result.countNonterminalEdges());
        assertEquals(0, result.attachedNonterminalEdgesOf(nodes.get(8)).size());
    }

    @Test
    public void removeVariableEdgeTest() {

        result.builder()
                .removeVariableEdge(result.variableWith("x"))
                .build();

        assertEquals(2, result.countVariableEdges());
        assertTrue(result.attachedVariablesOf(nodes.get(0)).isEmpty());
        assertEquals(2, result.variableEdges().size());

        try {
            result.builder()
                    .removeVariableEdge(result.variableWith("xyz"))
                    .build();
        } catch (IllegalArgumentException ex) {
            // expected
        }

        assertEquals(2, result.countVariableEdges());
    }

    @Test
    public void removeIsolatedNodeTest() {

        result.builder()
                .removeSelector(nodes.get(1), sel2)
                .removeSelector(nodes.get(0), sel1)
                .removeVariableEdge(result.variableWith("x"))
                .removeNonterminalEdge(result.nonterminalEdges().get(0))
                .removeIsolatedNode(nodes.get(0))
                .build();

        assertEquals(9, result.countNodes());

        assertTrue(result.attachedVariablesOf(nodes.get(9)).contains(result.variableWith("z")));
    }

    @Test
    public void renameVariableEdgeTest() {

        int x = result.variableWith("x");
        int t = result.targetOf(x);

        result.builder()
                .removeVariableEdge(x)
                .addVariableEdge("xyz", t)
                .build();

        assertEquals(nodes.get(0), result.targetOf(result.variableWith("xyz")));
    }

    @Test
    public void unsetExternalTest() {

        result.builder().unsetExternal(nodes.get(8)).build();
        assertEquals(nodes.get(0), result.externalNodeAt(0));
        assertEquals(1, result.countExternalNodes());
    }

    @Test
    public void replaceHyperedgeTest() {

        TIntArrayList originNodes = new TIntArrayList();
        HeapConfiguration origin = new InternalHeapConfiguration();
        origin.builder()
                .addNodes(type, 4, originNodes)
                .addVariableEdge("x", originNodes.get(2))
                .addNonterminalEdge(nt2, new TIntArrayList(new int[]{originNodes.get(0), originNodes.get(1)}))
                .addNonterminalEdge(nt2, new TIntArrayList(new int[]{originNodes.get(1), originNodes.get(2)}))
                .addSelector(originNodes.get(2), sel1, originNodes.get(3))
                .addSelector(originNodes.get(2), sel2, originNodes.get(0))
                .build();

        TIntArrayList ruleNodes = new TIntArrayList();
        HeapConfiguration rule = new InternalHeapConfiguration();
        rule.builder()
                .addNodes(type, 4, ruleNodes)
                .addSelector(ruleNodes.get(0), sel1, ruleNodes.get(1))
                .addSelector(ruleNodes.get(1), sel1, ruleNodes.get(2))
                .addSelector(ruleNodes.get(1), sel2, ruleNodes.get(3))
                .setExternal(ruleNodes.get(0))
                .setExternal(ruleNodes.get(2))
                .build();

        int ntEdge = origin.nonterminalEdges().get(1);

        origin.builder().replaceNonterminalEdge(ntEdge, rule).build();

        assertEquals(1, origin.countNonterminalEdges());
        assertEquals(6, origin.countNodes());
        assertEquals(1, origin.successorNodesOf(originNodes.get(1)).size());
        assertEquals(sel1, origin.selectorLabelsOf(originNodes.get(1)).get(0));
        assertEquals(1, origin.countVariableEdges());
    }

    @Test
    public void replaceHyperedgeTestWithNewNt() {

        TIntArrayList originNodes = new TIntArrayList();
        HeapConfiguration origin = new InternalHeapConfiguration();
        origin.builder()
                .addNodes(type, 4, originNodes)
                .addVariableEdge("x", originNodes.get(2))
                .addNonterminalEdge(nt2, new TIntArrayList(new int[]{originNodes.get(0), originNodes.get(1)}))
                .addNonterminalEdge(nt2, new TIntArrayList(new int[]{originNodes.get(1), originNodes.get(2)}))
                .addSelector(originNodes.get(2), sel1, originNodes.get(3))
                .addSelector(originNodes.get(2), sel2, originNodes.get(0))
                .build();

        TIntArrayList ruleNodes = new TIntArrayList();
        HeapConfiguration rule = new InternalHeapConfiguration();
        rule.builder()
                .addNodes(type, 4, ruleNodes)
                .addSelector(ruleNodes.get(0), sel1, ruleNodes.get(1))
                .addSelector(ruleNodes.get(1), sel1, ruleNodes.get(2))
                .addSelector(ruleNodes.get(1), sel2, ruleNodes.get(3))
                .setExternal(ruleNodes.get(0))
                .setExternal(ruleNodes.get(2))
                .addNonterminalEdge(nt2, new TIntArrayList(new int[]{ruleNodes.get(1), ruleNodes.get(2)}))
                .build();

        int ntEdge = origin.nonterminalEdges().get(1);

        origin.builder().replaceNonterminalEdge(ntEdge, rule).build();

        assertEquals(2, origin.countNonterminalEdges());
        assertEquals(6, origin.countNodes());
        assertEquals(1, origin.successorNodesOf(originNodes.get(1)).size());
        assertEquals(sel1, origin.selectorLabelsOf(originNodes.get(1)).get(0));
        assertEquals(1, origin.countVariableEdges());
    }

    @Test
    public void replaceEmbeddingTestSimple() {

        TIntArrayList embNodes = new TIntArrayList();
        HeapConfiguration emb = new InternalHeapConfiguration();
        emb.builder()
                .addNodes(type, 2, embNodes)
                .addSelector(embNodes.get(0), sel1, embNodes.get(1))
                .setExternal(embNodes.get(0))
                .setExternal(embNodes.get(1))
                .build();

        TIntArrayList originNodes = new TIntArrayList();
        HeapConfiguration origin = new InternalHeapConfiguration();
        origin.builder()
                .addNodes(type, 4, originNodes)
                .addVariableEdge("x", originNodes.get(3))
                .addSelector(originNodes.get(0), sel1, originNodes.get(1))
                .addSelector(originNodes.get(1), sel1, originNodes.get(2))
                .addSelector(originNodes.get(2), sel1, originNodes.get(3))
                .build();

        int[] m = new int[2];
        m[0] = 2;
        m[1] = 3;

        Morphism morphism = new Morphism(m);
        Matching matching = new InternalMatching(emb, morphism, origin);

        origin.builder().replaceMatching(matching, nt2).build();
        assertEquals(1, origin.countNonterminalEdges());

        int x = origin.variableWith("x");

        assertEquals(originNodes.get(3), origin.targetOf(x));
        assertEquals(origin.targetOf(x), origin.attachedNodesOf(origin.nonterminalEdges().get(0)).get(1));
    }

    @Test
    public void nodeGetSelectorsTest() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        hc.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(1), sel1, nodes.get(2))
                .addVariableEdge("test", nodes.get(1))
                .removeIsolatedNode(nodes.get(0))
                .build();

        int v = hc.variableWith("test");
        int t = hc.targetOf(v);

        assertEquals(1, hc.selectorLabelsOf(t).size());
        assertNotNull(hc.selectorTargetOf(t, sel1));
    }

}
