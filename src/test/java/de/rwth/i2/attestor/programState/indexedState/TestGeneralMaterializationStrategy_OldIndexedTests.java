package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.strategies.GeneralMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.util.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestGeneralMaterializationStrategy_OldIndexedTests {

    ViolationPoints inputVioPoints;
    private MaterializationStrategy materializer;
    private SceneObject sceneObject;
    private ExampleIndexedGraphFactory graphFactory;

    @Before
    public void setup() {

        sceneObject = new MockupSceneObject();
        sceneObject.scene().options().setIndexedModeEnabled(true);
        graphFactory = new ExampleIndexedGraphFactory(sceneObject);

        BalancedTreeGrammar balancedTreeGrammar = new BalancedTreeGrammar(sceneObject);

        Grammar grammar = balancedTreeGrammar.getGrammar();
        ViolationPointResolver violationPointResolver = new ViolationPointResolver(grammar);

        IndexMatcher indexMatcher = new IndexMatcher(new DefaultIndexMaterialization());
        MaterializationRuleManager grammarManager =
                new IndexedMaterializationRuleManager(violationPointResolver, indexMatcher);

        GrammarResponseApplier ruleApplier =
                new IndexedGrammarResponseApplier(new IndexMaterializationStrategy(), new GraphMaterializer());

        this.materializer = new GeneralMaterializationStrategy(grammarManager, ruleApplier);

        inputVioPoints = new ViolationPoints();
        inputVioPoints.add("x", "left");
    }

    @Test
    public void testMaterialize_small() {

        HeapConfiguration inputGraph
                = graphFactory.getInput_MaterializeSmall_Z();
        ProgramState inputState = sceneObject.scene().createProgramState(inputGraph).prepareHeap();

        ProgramState expected = sceneObject.scene().createProgramState(
               graphFactory.getExpected_MaterializeSmall_Z()
        ).prepareHeap();

        Collection<HeapConfiguration> materializedStates = materializer.materialize(inputState.getHeap(), inputVioPoints);
        assertEquals(1, materializedStates.size());
        assertTrue(materializedStates.contains(expected.getHeap()));
    }


    @Test
    public void testMaterialize_small2() {

        HeapConfiguration inputGraph
                = graphFactory.getInput_MaterializeSmall_sZ();
        ProgramState inputState = sceneObject.scene().createProgramState(inputGraph).prepareHeap();
        Collection<HeapConfiguration> materializedStates = materializer.materialize(inputState.getHeap(), inputVioPoints);
        assertEquals(3, materializedStates.size());


        ProgramState res1 = sceneObject.scene().createProgramState(
                graphFactory.getExpected_MaterializeSmall2_Res1()
        ).prepareHeap();
        ProgramState res2 = sceneObject.scene().createProgramState(
                graphFactory.getExpected_MaterializeSmall2_Res2()
        ).prepareHeap();
        ProgramState res3 = sceneObject.scene().createProgramState(
                graphFactory.getExpected_MaterializeSmall2_Res3()
        ).prepareHeap();

        assertTrue("should contain res1", materializedStates.contains(res1.getHeap()));
        assertTrue("should contain res2", materializedStates.contains(res2.getHeap()));
        assertTrue("should contain res3", materializedStates.contains(res3.getHeap()));
    }


    @Ignore
    public void testMaterialize_big() {

        HeapConfiguration inputGraph = graphFactory.getInput_MaterializeBig();
        ProgramState inputState = sceneObject.scene().createProgramState(inputGraph).prepareHeap();
        Collection<HeapConfiguration> materializedStates = materializer.materialize(inputState.getHeap(), inputVioPoints);
        assertEquals(5, materializedStates.size());


        ProgramState res1 = sceneObject.scene().createProgramState(
                graphFactory.getExpected_MaterializeBig_Res1()
        ).prepareHeap();
        ProgramState res2 = sceneObject.scene().createProgramState(
                graphFactory.getExpected_MaterializeBig_Res2()
        ).prepareHeap();
        ProgramState res3 = sceneObject.scene().createProgramState(
                graphFactory.getExpected_MaterializeBig_Res3()
        ).prepareHeap();
        ProgramState res4 = sceneObject.scene()
                .createProgramState(graphFactory.getExpected_MaterializeBig_Res4())
                .prepareHeap();
        ProgramState res5 = sceneObject.scene()
                .createProgramState(graphFactory.getExpected_MaterializeBig_Res5()).prepareHeap();


        assertTrue("should contain res1", materializedStates.contains(res1));
        assertTrue("should contain res2", materializedStates.contains(res2));
        assertTrue("should contain res3", materializedStates.contains(res3));
        assertTrue("should contain res4", materializedStates.contains(res4));
        assertTrue("should contain res5", materializedStates.contains(res5));
    }
}
