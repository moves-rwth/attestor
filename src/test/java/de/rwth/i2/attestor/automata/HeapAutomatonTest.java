package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.tasks.RefinedNonterminalImpl;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.*;

/**
 * Test grammar refinement and property checks for heap configurations using a heap automaton.
 *
 * @author Christoph
 */
public class HeapAutomatonTest {

    private HeapAutomaton automaton;
    private HeapConfiguration hc;
    private Type type;
    private Nonterminal nt;


    @BeforeClass
    public static void init() {
        UnitTestGlobalSettings.reset();
    }

    @Before
    public void setup() {
        automaton = new MockupHeapAutomaton();
        hc = Settings.getInstance().factory().createEmptyHeapConfiguration();
        type = Settings.getInstance().factory().getType("type");
        nt = Settings.getInstance().factory().createNonterminal("HAX", 3, new boolean[]{false,false,false});
    }

    @Test
    public void testBaseCase() {
        try {
            assertFalse(automaton.move(hc).isFinal());
        } catch(Exception ex) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testInductiveCaseInsufficientLabels() {

        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(type, 3, nodes)
                .addNonterminalEdge(nt, nodes)
                .build();

        automaton.move(hc);
    }

    @Test
    public void testInductiveCase() {

        RefinedNonterminalImpl ntWithState = new RefinedNonterminalImpl(nt, new MockUpState(1, true));
        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(type, 3, nodes)
                .addNonterminalEdge(ntWithState, nodes)
                .build();

        assertTrue(automaton.move(hc).isFinal());
    }

    @Test
    public void testGrammarRefinementSimple() {

        Map<Nonterminal, Set<HeapConfiguration>> rules = new HashMap<>();
        TIntArrayList nodes = new TIntArrayList();
        hc.builder()
                .addNodes(type, 3, nodes)
                .setExternal(0)
                .setExternal(1)
                .setExternal(2)
                .build();
        Set<HeapConfiguration> rhs = new HashSet<>();
        rhs.add(hc);
        rules.put(nt, rhs);
        Grammar grammar = new Grammar(rules);

        Grammar result = automaton.refine(grammar);
        assertEquals(1, result.getAllLeftHandSides().size());

        Nonterminal n = result.getAllLeftHandSides().iterator().next();
        assert(n instanceof RefinedNonterminalImpl);
        RefinedNonterminalImpl sn = (RefinedNonterminalImpl) n;
        MockUpState state = (MockUpState) sn.getState();
        assertEquals(0, state.getState());
        assertEquals(nt.getRank(), sn.getRank());
    }
}