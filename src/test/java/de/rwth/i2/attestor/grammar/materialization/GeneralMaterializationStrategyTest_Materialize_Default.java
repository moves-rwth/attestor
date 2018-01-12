package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.strategies.GeneralMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.util.*;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeneralMaterializationStrategyTest_Materialize_Default {

    private GeneralMaterializationStrategy materializer;

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);

        Nonterminal listLabel = sceneObject.scene().createNonterminal("List", 2, new boolean[]{false, true});

        Grammar grammar = Grammar.builder()
                .addRule(listLabel, hcFactory.getListRule1())
                .addRule(listLabel, hcFactory.getListRule2())
                .build();

        ViolationPointResolver violationPointResolver = new ViolationPointResolver(grammar);
        MaterializationRuleManager ruleManager =
                new DefaultMaterializationRuleManager(violationPointResolver);
        GraphMaterializer graphMaterializer = new GraphMaterializer();
        GrammarResponseApplier ruleApplier = new DefaultGrammarResponseApplier(graphMaterializer);

        materializer = new GeneralMaterializationStrategy(ruleManager, ruleApplier);
    }

    @Test
    public void testMaterialize_Default() {

        HeapConfiguration testInput = hcFactory.getMaterializationTest();
        ProgramState inputConf = new DefaultProgramState(testInput);


        ViolationPoints vio = new ViolationPoints("x", "next");

        Collection<HeapConfiguration> res = materializer.materialize(inputConf.getHeap(), vio);

        assertEquals("input graph should not change", hcFactory.getMaterializationTest(), testInput);
        assertEquals(2, res.size());

        List<HeapConfiguration> resHCs = new ArrayList<>();
        for(HeapConfiguration hc : res) {
            int x = hc.variableWith("x");
            int t = hc.targetOf(x);
            resHCs.add(hc);
            assertTrue(hc.selectorLabelsOf(t).contains(sceneObject.scene().getSelectorLabel("next")));
        }

        assertTrue("first expected materialization", resHCs.contains(hcFactory.getMaterializationRes1()));
        assertTrue("second expected materialization", resHCs.contains(hcFactory.getMaterializationRes2()));
    }

}
