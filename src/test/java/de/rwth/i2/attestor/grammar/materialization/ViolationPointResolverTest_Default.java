package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPointResolver;
import de.rwth.i2.attestor.grammar.testUtil.TestGraphs;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ViolationPointResolverTest_Default {

    public static final int TENTACLE_FOR_NEXT = 0;
    public static final int TENTACLE_WITHOUT_NEXT = 1;
    public static final String SELECTOR_NAME_NEXT = "next";
    public static final int TENTACLE_FOR_PREV = 1;
    public static final String SELECTOR_NAME_PREV = "prev";
    public Nonterminal DEFAULT_NONTERMINAL;
    public HeapConfiguration RHS_CREATING_NEXT;
    public HeapConfiguration RHS_CREATING_NEXT_PREV;
    public HeapConfiguration RHS_CREATING_PREV;
    public HeapConfiguration RHS_CREATING_NO_SELECTOR;
    private SceneObject sceneObject;

    @Before
    public void init() {

        sceneObject = new MockupSceneObject();
        TestGraphs testGraphs = new TestGraphs(sceneObject);
        DEFAULT_NONTERMINAL = createDefaultNonterminal();
        RHS_CREATING_NEXT = testGraphs.getRuleGraph_CreatingNext();
        RHS_CREATING_NEXT_PREV = testGraphs.getRuleGraph_CreatingNextAt0_PrevAt1();
        RHS_CREATING_PREV = testGraphs.getRuleGraph_creatingPrevAt1();
        RHS_CREATING_NO_SELECTOR = testGraphs.getRuleGraph_creatingNoSelector();

    }


    @Test
    public void testgetRulesCreatingSelector_Successful() {

        Grammar testGrammar = Grammar.builder().addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NEXT_PREV)
                .addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NEXT)
                .addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NO_SELECTOR)
                .addRule(DEFAULT_NONTERMINAL, RHS_CREATING_PREV)
                .build();
        ViolationPointResolver grammarLogik = new ViolationPointResolver(testGrammar);


        Map<Nonterminal, Collection<HeapConfiguration>> selectedRules
                = grammarLogik.getRulesCreatingSelectorFor(DEFAULT_NONTERMINAL,
                TENTACLE_FOR_NEXT,
                SELECTOR_NAME_NEXT);
        assertThat(selectedRules.keySet(), hasSize(1));
        assertThat(selectedRules.get(DEFAULT_NONTERMINAL),
                containsInAnyOrder(RHS_CREATING_NEXT_PREV, RHS_CREATING_NEXT)
        );

        selectedRules = grammarLogik.getRulesCreatingSelectorFor(DEFAULT_NONTERMINAL,
                TENTACLE_FOR_PREV, SELECTOR_NAME_PREV);
        assertThat(selectedRules.keySet(), hasSize(1));
        assertThat(selectedRules.get(DEFAULT_NONTERMINAL),
                containsInAnyOrder(RHS_CREATING_NEXT_PREV, RHS_CREATING_PREV));
    }

    @Test
    public void testGetRulesCreatingSelector_WrongTentacle() {

        Grammar testGrammar = Grammar.builder().addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NEXT_PREV)
                .addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NEXT)
                .addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NO_SELECTOR)
                .addRule(DEFAULT_NONTERMINAL, RHS_CREATING_PREV)
                .build();
        ViolationPointResolver grammarLogik = new ViolationPointResolver(testGrammar);


        Map<Nonterminal, Collection<HeapConfiguration>> selectedRules
                = grammarLogik.getRulesCreatingSelectorFor(DEFAULT_NONTERMINAL,
                TENTACLE_WITHOUT_NEXT,
                SELECTOR_NAME_NEXT);
        assertThat(selectedRules.keySet(),
                empty()
        );
    }

    @Test
    public void testGetRulesCreatingSelector_ImpossibleSelector() {

        Grammar testGrammar = Grammar.builder()
                .addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NEXT)
                .addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NO_SELECTOR)
                .build();
        ViolationPointResolver grammarLogik = new ViolationPointResolver(testGrammar);


        Map<Nonterminal, Collection<HeapConfiguration>> selectedRules
                = grammarLogik.getRulesCreatingSelectorFor(DEFAULT_NONTERMINAL,
                TENTACLE_WITHOUT_NEXT,
                SELECTOR_NAME_PREV);
        assertThat(selectedRules.keySet(),
                empty()
        );
    }


    private Nonterminal createDefaultNonterminal() {

        return sceneObject.scene().createNonterminal("GrammarLogikTest", 2, new boolean[]{false, false});
    }
}
