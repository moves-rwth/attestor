package de.rwth.i2.attestor.automata;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.RefinedDefaultNonterminal;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public class GrammarRefinementHelperTest {

    private Nonterminal nt;
    private HeapConfiguration emptyHc;
    private SelectorLabel sel;
    private Type type;

    @BeforeClass
    public static void init() {
        UnitTestGlobalSettings.reset();
    }

    @Before
    public void setup() {

        nt = Settings.getInstance().factory().createNonterminal("X", 2, new boolean[]{false, false});
        emptyHc = Settings.getInstance().factory().createEmptyHeapConfiguration();
        sel = Settings.getInstance().factory().getSelectorLabel("s");
        type = Settings.getInstance().factory().getType("type");
    }

    @AfterClass
    public static void tearDownClass() {
        Settings.getInstance().resetAllSettings();
    }

    @Test
    public void testRefinement() {

        MockupHeapAutomaton automaton = new MockupHeapAutomaton();
        Grammar grammar = createGrammar();

        GrammarRefinementHelper helper = new GrammarRefinementHelper(grammar, automaton);

        Grammar refinedGrammar = helper.getRefinedGrammar();
        assertNotNull(refinedGrammar);
        assertEquals("The number of refined nonterminals is incorrect",
                2, refinedGrammar.getAllLeftHandSides().size());

        RefinedDefaultNonterminal nt0 = new RefinedDefaultNonterminal(nt, new MockUpState(0, false));
        RefinedDefaultNonterminal nt1 = new RefinedDefaultNonterminal(nt, new MockUpState(1, true));

        for(Nonterminal nonterminal : refinedGrammar.getAllLeftHandSides()) {

            if(nt0.equals(nonterminal)) {
                assertEquals("The number of rules with left-hand side (X,0) is wrong",
                        2, refinedGrammar.getRightHandSidesFor(nonterminal).size());
            } else if (nt1.equals(nonterminal)) {
                assertEquals("The number of rules with left-hand side (X,1) is wrong",
                        6, refinedGrammar.getRightHandSidesFor(nonterminal).size());
            } else {
                fail("Refinement lead to an unexpected nonterminal: " + nonterminal);
            }
        }



    }

    private Grammar createGrammar() {

        Map<Nonterminal, Set<HeapConfiguration>> rules = new HashMap<>();
        rules.put(nt, new HashSet<>());
        TIntArrayList nodes = new TIntArrayList();

        rules.get(nt).add(emptyHc.clone()
                .builder()
                .addNodes(type, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .build()
        );

        nodes.clear();
        rules.get(nt).add(emptyHc.clone()
                .builder()
                .addNodes(type, 2, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .build()
        );

        nodes.clear();
        rules.get(nt).add(emptyHc.clone()
                .builder()
                .addNodes(type, 3, nodes)
                .addNonterminalEdge(nt)
                    .addTentacle(nodes.get(0))
                    .addTentacle(nodes.get(1))
                    .build()
                .addNonterminalEdge(nt)
                    .addTentacle(nodes.get(1))
                    .addTentacle(nodes.get(2))
                    .build()
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .build()
        );

        nodes.clear();
        rules.get(nt).add(emptyHc.clone()
                .builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(2))
                .build()
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .build()
        );

        return new Grammar( rules, false );
    }


}
