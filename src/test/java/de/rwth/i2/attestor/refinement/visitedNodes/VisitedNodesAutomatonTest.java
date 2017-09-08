package de.rwth.i2.attestor.refinement.visitedNodes;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.refinement.HeapAutomatonState;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

public class VisitedNodesAutomatonTest {

    private static Set<String> AP_VISITED = Collections.singleton("visited");
    private static Set<String> AP_NOT_VISITED = Collections.emptySet();
    private static Type NON_VISITED_TYPE = GeneralType.getType("VisitedNodesAutomatonType");
    private static Type VISITED_TYPE = VisitedTypeHelper.getVisitedType(NON_VISITED_TYPE);

    private VisitedNodesAutomaton automaton;
    private HeapConfiguration hc;
    private TIntArrayList nodes;


    @Before
    public void setup() {

        automaton = new VisitedNodesAutomaton();
        hc = new InternalHeapConfiguration();
        nodes = new TIntArrayList();
    }

    @Test
    public void testEmptyHc() {

        VisitedNodesAutomatonState state = automaton.transition(hc, Collections.emptyList());
        assertEquals(VisitedStatus.EMPTY, state.getInternalNodesStatus());
        assertEquals(0, state.rank());
        assertEquals(AP_VISITED, state.toAtomicPropositions());
    }

    @Test
    public void testExternalNodesOnly() {

        hc = hc.builder()
                .addNodes(NON_VISITED_TYPE, 1, nodes)
                .addNodes(VISITED_TYPE, 1, nodes)
                .setExternal(0)
                .setExternal(1)
                .build();


        VisitedNodesAutomatonState state = automaton.transition(hc, Collections.emptyList());
        assertEquals(VisitedStatus.EMPTY, state.getInternalNodesStatus());
        assertEquals(2, state.rank());
        assertEquals(false, state.hasVisitedExternal(0));
        assertEquals(true, state.hasVisitedExternal(1));
        assertEquals(AP_NOT_VISITED, state.toAtomicPropositions());
    }

    @Test
    public void testMixedInternalNodes() {

        hc = hc.builder()
                .addNodes(NON_VISITED_TYPE, 1, nodes)
                .addNodes(VISITED_TYPE, 1, nodes)
                .build();


        VisitedNodesAutomatonState state = automaton.transition(hc, Collections.emptyList());
        assertEquals(VisitedStatus.ERROR, state.getInternalNodesStatus());
        assertEquals(0, state.rank());
        assertEquals(AP_NOT_VISITED, state.toAtomicPropositions());
    }

    @Test
    public void testWithNonterminalsVisited() {

        Nonterminal nt = BasicNonterminal.getNonterminal("X", 3, new boolean[]{false,false,false});

        hc = hc.builder()
                .addNodes(NON_VISITED_TYPE, 3, nodes)
                .addNodes(VISITED_TYPE, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(4))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(2))
                .build()
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(3))
                .addTentacle(nodes.get(1))
                .build()
                .build();

        BitSet visitedExternals = new BitSet(3);
        visitedExternals.set(1);
        visitedExternals.set(2);

        VisitedNodesAutomatonState ntState = new VisitedNodesAutomatonState(3, visitedExternals, VisitedStatus.ALL_VISITED, "visited");
        List<HeapAutomatonState> statesOfNonterminals = new ArrayList<>(2);
        statesOfNonterminals.add(ntState);
        statesOfNonterminals.add(ntState);

        VisitedNodesAutomatonState state = automaton.transition(hc, statesOfNonterminals);
        assertEquals(VisitedStatus.ALL_VISITED, state.getInternalNodesStatus());
        assertEquals(3, state.rank());
        assertEquals(true, state.hasVisitedExternal(0));
        assertEquals(true, state.hasVisitedExternal(1));
        assertEquals(true, state.hasVisitedExternal(2));
        assertEquals(AP_VISITED, state.toAtomicPropositions());

        ntState = new VisitedNodesAutomatonState(3, visitedExternals, VisitedStatus.EMPTY, "visited");
        statesOfNonterminals = new ArrayList<>(2);
        statesOfNonterminals.add(ntState);
        statesOfNonterminals.add(ntState);

        state = automaton.transition(hc, statesOfNonterminals);
        assertEquals(VisitedStatus.ALL_VISITED, state.getInternalNodesStatus());
        assertEquals(3, state.rank());
        assertEquals(AP_VISITED, state.toAtomicPropositions());

    }

    @Test
    public void testWithNonterminalsError() {

        Nonterminal nt = BasicNonterminal.getNonterminal("X", 3, new boolean[]{false,false,false});

        hc = hc.builder()
                .addNodes(NON_VISITED_TYPE, 3, nodes)
                .addNodes(VISITED_TYPE, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(4))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(2))
                .build()
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(3))
                .addTentacle(nodes.get(1))
                .build()
                .build();

        BitSet visitedExternals = new BitSet(3);
        visitedExternals.set(1);

        VisitedNodesAutomatonState ntState = new VisitedNodesAutomatonState(3, visitedExternals, VisitedStatus.ALL_VISITED, "visited");
        List<HeapAutomatonState> statesOfNonterminals = new ArrayList<>(2);
        statesOfNonterminals.add(ntState);
        statesOfNonterminals.add(ntState);

        VisitedNodesAutomatonState state = automaton.transition(hc, statesOfNonterminals);
        assertEquals(VisitedStatus.ERROR, state.getInternalNodesStatus());
        assertEquals(0, state.rank());
        assertEquals(AP_NOT_VISITED, state.toAtomicPropositions());

    }

    @Test
    public void testWithNonterminalsNotVisited() {

        Nonterminal nt = BasicNonterminal.getNonterminal("X", 3, new boolean[]{false,false,false});

        hc = hc.builder()
                .addNodes(NON_VISITED_TYPE, 6, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(4))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(2))
                .build()
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(3))
                .addTentacle(nodes.get(1))
                .build()
                .build();

        BitSet visitedExternals = new BitSet(3);
        VisitedNodesAutomatonState ntState = new VisitedNodesAutomatonState(3, visitedExternals, VisitedStatus.ALL_NOT_VISITED, "visted");
        List<HeapAutomatonState> statesOfNonterminals = new ArrayList<>(2);
        statesOfNonterminals.add(ntState);
        statesOfNonterminals.add(ntState);

        VisitedNodesAutomatonState state = automaton.transition(hc, statesOfNonterminals);
        assertEquals(VisitedStatus.ALL_NOT_VISITED, state.getInternalNodesStatus());
        assertEquals(3, state.rank());
        assertEquals(false, state.hasVisitedExternal(0));
        assertEquals(false, state.hasVisitedExternal(1));
        assertEquals(false, state.hasVisitedExternal(2));
        assertEquals(AP_NOT_VISITED, state.toAtomicPropositions());

        ntState = new VisitedNodesAutomatonState(3, visitedExternals, VisitedStatus.EMPTY, "visited");
        statesOfNonterminals = new ArrayList<>(2);
        statesOfNonterminals.add(ntState);
        statesOfNonterminals.add(ntState);

        state = automaton.transition(hc, statesOfNonterminals);
        assertEquals(VisitedStatus.ALL_NOT_VISITED, state.getInternalNodesStatus());
        assertEquals(3, state.rank());
        assertEquals(false, state.hasVisitedExternal(0));
        assertEquals(false, state.hasVisitedExternal(1));
        assertEquals(false, state.hasVisitedExternal(2));
        assertEquals(AP_NOT_VISITED, state.toAtomicPropositions());
    }

    @Test
    public void testPossibleHeapRewritings() {

        SelectorLabel sel = BasicSelectorLabel.getSelectorLabel("VisitedNodesAutomatonTestSel");

        TIntArrayList nodes = new TIntArrayList(4);
        hc = hc.builder()
                .addNodes(NON_VISITED_TYPE, 4, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(3))
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .addSelector(nodes.get(1), sel, nodes.get(2))
                .addSelector(nodes.get(2), sel, nodes.get(3))
                .build();

        List<HeapConfiguration> rewritings = automaton.getPossibleHeapRewritings(hc);
        assertEquals(8, rewritings.size());
        for(int i=0; i < rewritings.size()-1; i++) {
            assertFalse(rewritings.get(i).equals(rewritings.get(i+1)));
        }
    }
}
