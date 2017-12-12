package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.BalancedTreeGrammar;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexVariable;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GrammarTest {

    public Nonterminal DEFAULT_NONTERMINAL;
    public Nonterminal CONCRETE_INDEXED_NONTERMINAL;
    public Nonterminal INSTANTIABLE_INDEXED_NONTERMINAL;
    public Set<HeapConfiguration> RHS_FOR_DEFAULT_NONTERMINAL;
    public HeapConfiguration RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1;
    public Set<HeapConfiguration> RHS_FOR_CONCRETE_INDEXED_NONTERMINAL;
    public HeapConfiguration RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_1;
    public HeapConfiguration RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_2;
    public Set<HeapConfiguration> RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL;
    public HeapConfiguration RHS_FOR_DEFAULT_NONTERMINAL_1;
    public HeapConfiguration RHS_FOR_DEFAULT_NONTERMINAL_2;
    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;
    private BalancedTreeGrammar balancedTreeGrammar;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);
        balancedTreeGrammar = new BalancedTreeGrammar(sceneObject);

        DEFAULT_NONTERMINAL = constructDefaultNonterminal();
        CONCRETE_INDEXED_NONTERMINAL = constructConcreteIndexedNonterminal();
        INSTANTIABLE_INDEXED_NONTERMINAL = constructInstantiableIndexedNonterminal();

        RHS_FOR_DEFAULT_NONTERMINAL_1 = hcFactory.getListRule1();
        RHS_FOR_DEFAULT_NONTERMINAL_2 = hcFactory.getListRule2();
        RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1 = balancedTreeGrammar.createBalancedLeafRule();
        RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_1 = balancedTreeGrammar.createUnbalancedRuleLeft();
        RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_2 = balancedTreeGrammar.createUnbalancedRuleRight();
        RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL = constructRhsForInstantiableIndexedNonterminal();
        RHS_FOR_DEFAULT_NONTERMINAL = constructRhsForDefaultNonterminal();
        RHS_FOR_CONCRETE_INDEXED_NONTERMINAL = constructRhsForConcreteIndexedNonterminal();
    }

    @Test
    public void testGrammarOnDefaultNonterminal() {

        Grammar testGrammar = Grammar.builder().addRules(DEFAULT_NONTERMINAL, RHS_FOR_DEFAULT_NONTERMINAL)
                .build();

        assertEquals(RHS_FOR_DEFAULT_NONTERMINAL, testGrammar.getRightHandSidesFor(DEFAULT_NONTERMINAL));
        assertThat(testGrammar.getAllLeftHandSides(), contains(DEFAULT_NONTERMINAL));
    }

    @Test
    public void testGrammarOnIndexedNonterminals() {

        Grammar testGrammar = Grammar.builder()
                .addRules(CONCRETE_INDEXED_NONTERMINAL,
                        RHS_FOR_CONCRETE_INDEXED_NONTERMINAL)
                .addRules(INSTANTIABLE_INDEXED_NONTERMINAL,
                        RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL)
                .build();

        assertEquals(RHS_FOR_CONCRETE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL));
        assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL));
        assertThat(testGrammar.getAllLeftHandSides(),
                containsInAnyOrder(CONCRETE_INDEXED_NONTERMINAL,
                        INSTANTIABLE_INDEXED_NONTERMINAL));
    }

    @Test
    public void testGrammarOnMixedNonterminals() {

        Grammar testGrammar = Grammar.builder()
                .addRules(DEFAULT_NONTERMINAL, RHS_FOR_DEFAULT_NONTERMINAL)
                .addRules(CONCRETE_INDEXED_NONTERMINAL, RHS_FOR_CONCRETE_INDEXED_NONTERMINAL)
                .addRules(INSTANTIABLE_INDEXED_NONTERMINAL, RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL)
                .build();

        assertEquals(RHS_FOR_CONCRETE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL));
        assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL));
        assertEquals(RHS_FOR_DEFAULT_NONTERMINAL,
                testGrammar.getRightHandSidesFor(DEFAULT_NONTERMINAL));
        assertThat(testGrammar.getAllLeftHandSides(),
                containsInAnyOrder(DEFAULT_NONTERMINAL,
                        CONCRETE_INDEXED_NONTERMINAL,
                        INSTANTIABLE_INDEXED_NONTERMINAL)
        );

    }

    @Test
    public void testAddRulesAsMap() {

        Map<Nonterminal, Collection<HeapConfiguration>> rules = new LinkedHashMap<>();
        rules.put(DEFAULT_NONTERMINAL, RHS_FOR_DEFAULT_NONTERMINAL);
        rules.put(CONCRETE_INDEXED_NONTERMINAL, RHS_FOR_CONCRETE_INDEXED_NONTERMINAL);
        rules.put(INSTANTIABLE_INDEXED_NONTERMINAL, RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL);

        Grammar testGrammar = Grammar.builder().addRules(rules).build();

        assertEquals(RHS_FOR_CONCRETE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL));
        assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL));
        assertEquals(RHS_FOR_DEFAULT_NONTERMINAL,
                testGrammar.getRightHandSidesFor(DEFAULT_NONTERMINAL));
        assertThat(testGrammar.getAllLeftHandSides(),
                containsInAnyOrder(DEFAULT_NONTERMINAL,
                        CONCRETE_INDEXED_NONTERMINAL,
                        INSTANTIABLE_INDEXED_NONTERMINAL)
        );

    }

    @Test
    public void testBuildGrammarWithMapAndMultipleRule() {

        Map<Nonterminal, Collection<HeapConfiguration>> rules = new LinkedHashMap<>();
        rules.put(DEFAULT_NONTERMINAL, RHS_FOR_DEFAULT_NONTERMINAL);
        rules.put(INSTANTIABLE_INDEXED_NONTERMINAL, RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL);

        Grammar testGrammar = Grammar.builder().addRules(rules)
                .addRules(CONCRETE_INDEXED_NONTERMINAL,
                        RHS_FOR_CONCRETE_INDEXED_NONTERMINAL)
                .build();

        assertEquals(RHS_FOR_CONCRETE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL));
        assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL));
        assertEquals(RHS_FOR_DEFAULT_NONTERMINAL,
                testGrammar.getRightHandSidesFor(DEFAULT_NONTERMINAL));
        assertThat(testGrammar.getAllLeftHandSides(),
                containsInAnyOrder(DEFAULT_NONTERMINAL,
                        CONCRETE_INDEXED_NONTERMINAL,
                        INSTANTIABLE_INDEXED_NONTERMINAL)
        );
    }

    @Test
    public void testBuildGrammarWithSingleRuleAndMap() {

        Map<Nonterminal, Collection<HeapConfiguration>> rules = new LinkedHashMap<>();
        rules.put(DEFAULT_NONTERMINAL, RHS_FOR_DEFAULT_NONTERMINAL);
        rules.put(INSTANTIABLE_INDEXED_NONTERMINAL, RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL);

        Grammar testGrammar = Grammar.builder().addRule(CONCRETE_INDEXED_NONTERMINAL,
                RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1)
                .addRules(rules)
                .build();

        assertEquals(RHS_FOR_CONCRETE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL));
        assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL));
        assertEquals(RHS_FOR_DEFAULT_NONTERMINAL,
                testGrammar.getRightHandSidesFor(DEFAULT_NONTERMINAL));
        assertThat(testGrammar.getAllLeftHandSides(),
                containsInAnyOrder(DEFAULT_NONTERMINAL,
                        CONCRETE_INDEXED_NONTERMINAL,
                        INSTANTIABLE_INDEXED_NONTERMINAL)
        );
    }


    @Test
    public void testBuildGrammarWithMultipleSingleRules() {

        Grammar testGrammar = Grammar.builder()
                .addRule(CONCRETE_INDEXED_NONTERMINAL,
                        RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1)
                .addRule(INSTANTIABLE_INDEXED_NONTERMINAL,
                        RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_1)
                .addRule(INSTANTIABLE_INDEXED_NONTERMINAL,
                        RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_2)
                .build();

        assertEquals(RHS_FOR_CONCRETE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL));
        assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
                testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL));
        assertThat(testGrammar.getAllLeftHandSides(),
                containsInAnyOrder(CONCRETE_INDEXED_NONTERMINAL,
                        INSTANTIABLE_INDEXED_NONTERMINAL));
    }

    @Test
    public void testGetRuleGraphsForNonExistingNonterminal() {

        Grammar testGrammar = Grammar.builder()
                .addRule(CONCRETE_INDEXED_NONTERMINAL,
                        RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1)
                .build();

        assertThat(testGrammar.getRightHandSidesFor(DEFAULT_NONTERMINAL), empty());
    }

    private Nonterminal constructDefaultNonterminal() {

        final boolean[] reductionTentacles = new boolean[]{false, true};
        final int rank = 2;
        final String label = "List";
        return sceneObject.scene().createNonterminal(label, rank, reductionTentacles);
    }

    private Set<HeapConfiguration> constructRhsForDefaultNonterminal() {

        Set<HeapConfiguration> rhs = new LinkedHashSet<>();
        rhs.add(RHS_FOR_DEFAULT_NONTERMINAL_1);
        rhs.add(RHS_FOR_DEFAULT_NONTERMINAL_2);
        return rhs;
    }

    private IndexedNonterminal constructConcreteIndexedNonterminal() {

        ConcreteIndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
        ArrayList<IndexSymbol> lhsIndex = new ArrayList<>();
        lhsIndex.add(bottom);
        Nonterminal nt = sceneObject.scene().createNonterminal("B", 2, new boolean[]{false, true});
        return new IndexedNonterminalImpl(nt, lhsIndex);
    }

    private Set<HeapConfiguration> constructRhsForConcreteIndexedNonterminal() {

        Set<HeapConfiguration> rhs = new LinkedHashSet<>();
        rhs.add(RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1);
        return rhs;
    }

    private Nonterminal constructInstantiableIndexedNonterminal() {

        final IndexVariable var = IndexVariable.getIndexVariable();
        final ConcreteIndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        ArrayList<IndexSymbol> lhsIndex = new ArrayList<>();
        lhsIndex.add(s);
        lhsIndex.add(var);
        Nonterminal nt = sceneObject.scene().createNonterminal("B", 2, new boolean[]{false, true});
        return new IndexedNonterminalImpl(nt, lhsIndex);
    }

    private Set<HeapConfiguration> constructRhsForInstantiableIndexedNonterminal() {

        Set<HeapConfiguration> rhs = new LinkedHashSet<>();
        rhs.add(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_1);
        rhs.add(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_2);
        return rhs;
    }
}
