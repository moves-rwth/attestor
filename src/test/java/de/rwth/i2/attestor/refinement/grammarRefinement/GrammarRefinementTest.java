package de.rwth.i2.attestor.refinement.grammarRefinement;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.RefinedNonterminal;
import de.rwth.i2.attestor.refinement.visitedNodes.VisitedNodesAutomaton;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.*;

public class GrammarRefinementTest {

    private static Grammar grammar;
    private static HeapAutomaton automaton;
    private static Grammar refinedGrammar;
    private static BasicNonterminal listLabel;

    @BeforeClass
    public static void setupClass() {

        listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );
        Set<HeapConfiguration> rhs = new HashSet<>();
        rhs.add(ExampleHcImplFactory.getListRule1());
        rhs.add(ExampleHcImplFactory.getListRule2());
        grammar = new Grammar(Collections.singletonMap(listLabel, rhs));

        automaton = new VisitedNodesAutomaton();

        GrammarRefinement refinement = new GrammarRefinement(grammar, automaton) ;
        refinedGrammar = refinement.getRefinedGrammar();
    }

    @Test
    public void testRefineListGrammar() {

        assertNotNull(refinedGrammar);
        int noRules = 0;
        for(Nonterminal nt : refinedGrammar.getAllLeftHandSides()) {
            noRules += refinedGrammar.getRightHandSidesFor(nt).size();
            for(HeapConfiguration hc : refinedGrammar.getRightHandSidesFor(nt)) {
                for(HeapConfiguration hc2 : refinedGrammar.getRightHandSidesFor(nt)) {
                    if(hc != hc2) {
                        assertFalse(hc.equals(hc2));
                    }
                }
            }
        }
        // All combinations but error
        assertEquals(12, refinedGrammar.getAllLeftHandSides().size());
        assertEquals(68, noRules);
    }

    @Test
    public void testInitialHeapConfigurationRefinement() {

        HeapConfiguration initialHc = ExampleHcImplFactory.getListRule3();

        InitialHeapConfigurationRefinement refinement = new InitialHeapConfigurationRefinement(
            initialHc, refinedGrammar, automaton
        );
        List<HeapConfiguration> refinedInitialHcs = refinement.getRefinements();
        assertFalse(refinedInitialHcs.isEmpty());
        assertEquals(4, refinedInitialHcs.size());

        for(HeapConfiguration ihc : refinedInitialHcs) {
            for(int i=0; i < ihc.countNonterminalEdges(); i++) {
                RefinedNonterminal rnt = (RefinedNonterminal) ihc.labelOf(ihc.nonterminalEdges().get(i));
                assert(automaton.isInitialState(rnt.getState()));
            }
        }
    }
}
