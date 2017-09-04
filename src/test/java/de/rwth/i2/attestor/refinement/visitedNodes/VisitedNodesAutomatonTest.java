package de.rwth.i2.attestor.refinement.visitedNodes;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.refinement.ErrorHeapAutomatonState;
import de.rwth.i2.attestor.refinement.HeapAutomatonState;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;

public class VisitedNodesAutomatonTest {

    private static Set<String> AP_VISITED = Collections.singleton("visited");
    private static Set<String> AP_NOT_VISITED = Collections.emptySet();
    private static Type NON_VISITED_TYPE = GeneralType.getType("VisitedNodesAutomatonType");
    private static Type VISITED_TYPE = VisitedTypeHelper.getVisitedType(NON_VISITED_TYPE);

    private VisitedNodesAutomaton automaton;
    private HeapConfiguration hc;
    private HeapAutomatonState state;


    @Before
    public void setup() {

        automaton = new VisitedNodesAutomaton();
        hc = new InternalHeapConfiguration();
    }

    @Test
    public void testEmptyHc() {

        state = automaton.transition(hc, Collections.emptyList());
        assertEquals(AP_VISITED, state.toAtomicPropositions());
    }

    @Test
    public void testSimpleErrorHc() {


        TIntArrayList nodes = new TIntArrayList(3);
        hc = hc.builder()
                .addNodes(NON_VISITED_TYPE, 2, nodes)
                .addNodes(VISITED_TYPE, 1, nodes)
                .setExternal(1)
                .build();

        state = automaton.transition(hc, Collections.emptyList());
        assertEquals(AP_NOT_VISITED, state.toAtomicPropositions());

        assertEquals(ErrorHeapAutomatonState.class, state.getClass());
    }

    @Test
    public void testVisitedWithNonterminals() {

        Nonterminal nt = BasicNonterminal.getNonterminal("VisitedNodesAutomatonTestNt",
                2, new boolean[]{true, true});

        TIntArrayList nodes = new TIntArrayList(2);
        hc = hc.builder()
                .addNodes(NON_VISITED_TYPE, 2, nodes)
                .addNonterminalEdge(nt).addTentacle(nodes.get(0)).addTentacle(nodes.get(1)).build()
                .addNonterminalEdge(nt).addTentacle(nodes.get(1)).addTentacle(nodes.get(0)).build()
                .build();

        BitSet visited = new BitSet(2);
        visited.set(0, true);
        visited.set(1, false);
        List<HeapAutomatonState> statesOfNonterminals = new ArrayList<>(2);
        VisitedNodesAutomatonState ntState = new VisitedNodesAutomatonState(visited, true);
        statesOfNonterminals.add(ntState);
        statesOfNonterminals.add(ntState);

        state = automaton.transition(hc, statesOfNonterminals);
        assertEquals(AP_VISITED, state.toAtomicPropositions());
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
    }
}
