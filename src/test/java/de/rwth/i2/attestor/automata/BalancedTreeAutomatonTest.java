package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.automata.implementations.balancedness.BalancedTreeAutomaton;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.indexedGrammars.ExampleIndexedGraphFactory;
import de.rwth.i2.attestor.tasks.GeneralType;
import de.rwth.i2.attestor.types.Type;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class BalancedTreeAutomatonTest {

    private HeapConfiguration hc;
    private Type type;
    private HeapAutomaton automaton;

    @BeforeClass
    public static void init() {
        UnitTestGlobalSettings.reset();
    }

    @Before
    public void setup() {

        hc = new InternalHeapConfiguration();
        type = GeneralType.getType("AVLTree");
        automaton = new BalancedTreeAutomaton();
    }

    @Test
    public void testEmptyHc() {

        AutomatonState state = automaton.move(hc);
        assertFalse(state.isFinal());
    }

    @Test
    public void testBalancedTree() {

        AnnotatedSelectorLabel parent = new AnnotatedSelectorLabel("parent", "");

        hc = ExampleIndexedGraphFactory
                .getExpected_AnnotationMaintaining()
                .builder()
                .addSelector(4, parent, 3)
                .build();

        AutomatonState state = automaton.move(hc);
        assertTrue(state.isFinal());
    }
}
