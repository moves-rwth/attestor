package de.rwth.i2.attestor.automata.visited;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class VisitedAutomatonTest {

    private VisitedAutomaton automaton;

    @Before
    public void setup() {

        automaton = new VisitedAutomaton();
    }

    @Test
    public void testEmptyHc() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        VisitedState state = automaton.move(new ArrayList<>(), hc);
        assertTrue(state.isFinal());
    }
}
