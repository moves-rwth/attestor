package de.rwth.i2.attestor.refinement.grammarRefinement;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.visitedNodes.VisitedNodesAutomaton;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;

public class GrammarRefinementTest {

    @Test
    public void testRefineListGrammar() {

        BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );
        Set<HeapConfiguration> rhs = new HashSet<>();
        rhs.add(ExampleHcImplFactory.getListRule1());
        rhs.add(ExampleHcImplFactory.getListRule2());
        Grammar grammar = new Grammar(Collections.singletonMap(listLabel, rhs));

        HeapAutomaton automaton = new VisitedNodesAutomaton();

        GrammarRefinement refinement = new GrammarRefinement(grammar, automaton) ;
        Grammar refinedGrammar = refinement.getRefinedGrammar();
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
        assertEquals(13, refinedGrammar.getAllLeftHandSides().size());
        assertEquals(108, noRules);
    }
}
