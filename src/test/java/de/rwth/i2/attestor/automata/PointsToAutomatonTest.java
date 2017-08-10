package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.graph.GeneralNonterminal;
import de.rwth.i2.attestor.graph.GeneralSelectorLabel;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;

public class PointsToAutomatonTest {

    private PointsToAutomaton automaton;

    @Before
    public void setup() {

        automaton = new PointsToAutomaton();
    }

    @Test
    public void testEmptyHc() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        AutomatonState state = automaton.move(new ArrayList<>(), hc);
        assertNotNull(state);
        assert( state instanceof PointsToState );
        PointsToState ptState = (PointsToState) state;
        assertEquals(ptState.kernel, hc);
        assertFalse(ptState.isFinal());
    }

    @Test
    public void testConcreteHc() {

        SelectorLabel selA = GeneralSelectorLabel.getSelectorLabel("a");
        SelectorLabel selB = GeneralSelectorLabel.getSelectorLabel("b");
        Type type = GeneralType.getType("type");

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(type, 4, nodes)
                .addSelector(nodes.get(0), selA, nodes.get(1))
                .addSelector(nodes.get(1), selA, nodes.get(2))
                .addSelector(nodes.get(2), selA, nodes.get(3))
                .addSelector(nodes.get(2), selB, nodes.get(3))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(3))
                .build();

        nodes.clear();
        HeapConfiguration kernel = new InternalHeapConfiguration()
                .builder()
                .addNodes(type, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .addSelector(nodes.get(1), selA, nodes.get(2))
                .addSelector(nodes.get(1), selB, nodes.get(2))
                .build();

        PointsToState state = (PointsToState) automaton.move(new ArrayList<>(), hc);
        assertEquals(kernel, state.kernel);
        assertFalse(state.isFinal());
    }

    @Test
    public void testAbstractHc() {

        SelectorLabel selA = GeneralSelectorLabel.getSelectorLabel("a");
        SelectorLabel selB = GeneralSelectorLabel.getSelectorLabel("b");
        Type type = GeneralType.getType("type");

        Nonterminal nt = GeneralNonterminal.getNonterminal("Y", 2, new boolean[]{false, false});

        HeapConfiguration hc = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(type, 4, nodes)
                .addSelector(nodes.get(0), selA, nodes.get(1))
                .addSelector(nodes.get(1), selA, nodes.get(2))
                .addSelector(nodes.get(2), selA, nodes.get(3))
                .addSelector(nodes.get(2), selB, nodes.get(3))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(3))
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(2)}))
                .build();

        nodes.clear();
        HeapConfiguration ntKernel = new InternalHeapConfiguration()
                .builder()
                .addNodes(type, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), selB, nodes.get(1))
                .build();
        AutomatonState ntState = new PointsToState(ntKernel);

        nodes.clear();
        HeapConfiguration kernel = new InternalHeapConfiguration()
                .builder()
                .addNodes(type, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .addSelector(nodes.get(0), selB, nodes.get(1))
                .addSelector(nodes.get(1), selA, nodes.get(2))
                .addSelector(nodes.get(1), selB, nodes.get(2))
                .build();

        List<AutomatonState> ntAssignment = new ArrayList<>(1);
        ntAssignment.add(ntState);
        PointsToState state = (PointsToState) automaton.move(ntAssignment, hc);
        assertEquals(kernel, state.kernel);
        assertFalse(state.isFinal());
    }
}
