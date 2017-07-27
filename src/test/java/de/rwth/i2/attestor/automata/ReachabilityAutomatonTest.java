package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.automata.implementations.ReachabilityAutomatonState;
import de.rwth.i2.attestor.automata.implementations.ReachabilityHeapAutomaton;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.tasks.GeneralNonterminal;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;
import de.rwth.i2.attestor.tasks.GeneralType;
import de.rwth.i2.attestor.tasks.RefinedNonterminalImpl;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

public class ReachabilityAutomatonTest {

    private HeapConfiguration hc;
    private Type type;

    @BeforeClass
    public static void init() {
        UnitTestGlobalSettings.reset();
    }

    @Before
    public void setup() {

        hc = new InternalHeapConfiguration();
        type = GeneralType.getType("type");
    }

    @Test
    public void testEmptyHc() {

        HeapAutomaton automaton = new ReachabilityHeapAutomaton(0, 1);
        ReachabilityAutomatonState state = (ReachabilityAutomatonState) automaton.move(hc);

        assertFalse(state.isFinal());
        assertEquals(0, state.getKernel().countNodes());
    }

    @Test
    public void testConcreteHC() {

        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(type, 5, nodes)
                .setExternal(0)
                .setExternal(1)
                .setExternal(2)
                .addSelector(0, GeneralSelectorLabel.getSelectorLabel("a"), 3)
                .addSelector(3, GeneralSelectorLabel.getSelectorLabel("a"), 4)
                .addSelector(4, GeneralSelectorLabel.getSelectorLabel("a"), 1)
                .build();

        HeapAutomaton automaton = new ReachabilityHeapAutomaton(0, 1);
        ReachabilityAutomatonState state = (ReachabilityAutomatonState) automaton.move(hc);

        assert(state.isFinal());
        assert(state.getAtomicPropositions().contains("(0,1)"));
        assertFalse(state.getAtomicPropositions().contains("(0,0)"));
        assertFalse(state.getAtomicPropositions().contains("(1,1)"));
        assertFalse(state.getAtomicPropositions().contains("(2,2)"));
        assertFalse(state.getAtomicPropositions().contains("(0,2)"));
        assertFalse(state.getAtomicPropositions().contains("(1,0)"));
        assertFalse(state.getAtomicPropositions().contains("(1,2)"));
        assertFalse(state.getAtomicPropositions().contains("(2,0)"));
        assertFalse(state.getAtomicPropositions().contains("(2,1)"));
    }

    @Test
    public void testAbstractHC() {

        List<TIntSet> map = new ArrayList<>();
        TIntSet set = new TIntHashSet();
        set.add(1);
        map.add(set);
        map.add(new TIntHashSet());
        ReachabilityAutomatonState ntState = new ReachabilityAutomatonState(map, true);

        RefinedNonterminal nt = new RefinedNonterminalImpl(
                GeneralNonterminal.getNonterminal("X", 2, new boolean[]{false, false}),
                ntState
        );

        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(type, 6, nodes)
                .setExternal(0)
                .setExternal(1)
                .addSelector(0, GeneralSelectorLabel.getSelectorLabel("a"), 2)
                .addSelector(4, GeneralSelectorLabel.getSelectorLabel("a"), 5)
                .addSelector(5, GeneralSelectorLabel.getSelectorLabel("a"), 1)
                .addNonterminalEdge(nt)
                    .addTentacle(2)
                    .addTentacle(3)
                    .build()
                .addNonterminalEdge(nt)
                    .addTentacle(3)
                    .addTentacle(4)
                    .build()
                .build();

        List<AutomatonState> ntAssignment = new ArrayList<>();
        ntAssignment.add(ntState);
        ntAssignment.add(ntState);

        HeapAutomaton automaton = new ReachabilityHeapAutomaton(0, 1);
        ReachabilityAutomatonState state = (ReachabilityAutomatonState) automaton.move(hc);

        assert(state.isFinal());
        assert(state.getAtomicPropositions().contains("(0,1)"));
        assertFalse(state.getAtomicPropositions().contains("(1,0)"));
    }


}
