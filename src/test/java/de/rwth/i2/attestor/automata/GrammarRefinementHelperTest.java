package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.tasks.StateAnnotatedNonterminal;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class GrammarRefinementHelperTest {

    private Nonterminal nt;
    private HeapConfiguration emptyHc;
    private SelectorLabel sel;
    private Type type;

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

        HeapAutomatonTransition transitions = new MockUpTransition();
        Grammar grammar = createGrammar();

        GrammarRefinementHelper helper = new GrammarRefinementHelper(grammar, transitions);

        Grammar refinedGrammar = helper.getRefinedGrammar();
        assertNotNull(refinedGrammar);
        assertEquals("The number of refined nonterminals is incorrect",
                2, refinedGrammar.getAllLeftHandSides().size());

        StateAnnotatedNonterminal nt0 = new StateAnnotatedNonterminal(nt, new MockUpState(0, false));
        StateAnnotatedNonterminal nt1 = new StateAnnotatedNonterminal(nt, new MockUpState(1, true));

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

        return new Grammar(rules);
    }


}
