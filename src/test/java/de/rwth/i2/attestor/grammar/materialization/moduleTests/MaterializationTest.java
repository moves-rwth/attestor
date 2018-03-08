package de.rwth.i2.attestor.grammar.materialization.moduleTests;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MaterializationTest {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger("MaterializationTest");

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcImplFactory;

    private GeneralMaterializationStrategy materializer;


    @Before
    public void init() {

        sceneObject = new MockupSceneObject();
        hcImplFactory = new ExampleHcImplFactory(sceneObject);

        Nonterminal listLabel = sceneObject.scene().createNonterminal("List", 2, new boolean[]{false, true});

        Grammar grammar = Grammar.builder()
                .addRule(listLabel, hcImplFactory.getListRule1())
                .addRule(listLabel, hcImplFactory.getListRule2())
                .build();

        ViolationPointResolver vioResolver = new ViolationPointResolver(grammar);
        MaterializationRuleManager grammarManager =
                new DefaultMaterializationRuleManager(vioResolver);

        GrammarResponseApplier ruleApplier =
                new DefaultGrammarResponseApplier(new GraphMaterializer());

        materializer = new GeneralMaterializationStrategy(grammarManager, ruleApplier);
    }


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMaterialization() {

        HeapConfiguration testInput = hcImplFactory.getMaterializationTest();
        ProgramState inputConf = new DefaultProgramState(testInput);

        ViolationPoints vio = new ViolationPoints("x", "next");

        Collection<HeapConfiguration> res = materializer.materialize(inputConf.getHeap(), vio);

        assertEquals("input graph should not change", hcImplFactory.getMaterializationTest(), testInput);
        assertEquals(2, res.size());

        List<HeapConfiguration> resHCs = new ArrayList<>();
        for(HeapConfiguration hc : res)  {
            int x = hc.variableWith("x");
            int t = hc.targetOf(x);
            assertTrue(hc.selectorLabelsOf(t).contains(sceneObject.scene().getSelectorLabel("next")));
            resHCs.add(hc);
        }

        assertTrue("first expected materialization", resHCs.contains(hcImplFactory.getMaterializationRes1()));
        assertTrue("second expected materialization", resHCs.contains(hcImplFactory.getMaterializationRes2()));
    }

}
