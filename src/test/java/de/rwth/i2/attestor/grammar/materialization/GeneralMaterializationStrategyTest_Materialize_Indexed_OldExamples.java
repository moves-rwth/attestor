package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.strategies.GeneralMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.util.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.BalancedTreeGrammar;
import de.rwth.i2.attestor.programState.indexedState.ExampleIndexedGraphFactory;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeneralMaterializationStrategyTest_Materialize_Indexed_OldExamples {

    private SceneObject sceneObject;
    private ExampleIndexedGraphFactory graphFactory;
    private GeneralMaterializationStrategy materializer;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        sceneObject.scene().options().setIndexedModeEnabled(true);

        graphFactory = new ExampleIndexedGraphFactory(sceneObject);

        BalancedTreeGrammar treeGrammar = new BalancedTreeGrammar(sceneObject);

        Grammar balancedTreeGrammar = treeGrammar.getGrammar();
        ViolationPointResolver vioResolver = new ViolationPointResolver(balancedTreeGrammar);


        IndexMatcher indexMatcher = new IndexMatcher(new DefaultIndexMaterialization());
        MaterializationRuleManager ruleManager =
                new IndexedMaterializationRuleManager(vioResolver, indexMatcher);

        GrammarResponseApplier ruleApplier =
                new IndexedGrammarResponseApplier(new IndexMaterializationStrategy(), new GraphMaterializer());
        this.materializer = new GeneralMaterializationStrategy(ruleManager, ruleApplier);
    }

    @Test
    public void testMaterialize_small_Z() {

        HeapConfiguration inputGraph = graphFactory.getInput_MaterializeSmall_Z();
        ProgramState inputState = sceneObject.scene().createProgramState(inputGraph);
        inputState.prepareHeap();

        HeapConfiguration expectedGraph = graphFactory.getExpected_MaterializeSmall_Z();
        ProgramState expectedState = sceneObject.scene().createProgramState(expectedGraph);
        expectedState.prepareHeap();

        ViolationPoints vioPoints = new ViolationPoints();
        vioPoints.add("x", "left");
        vioPoints.add("x", "right");

        Collection<HeapConfiguration> materializedStates = materializer.materialize(inputState.getHeap(), vioPoints);

        assertEquals(1, materializedStates.size());
        assertEquals(expectedState.getHeap(), materializedStates.iterator().next());
    }

    @Test
    public void testMaterialize_small_sZ() {

        HeapConfiguration inputGraph = graphFactory.getInput_MaterializeSmall_sZ();
        ProgramState inputState = sceneObject.scene().createProgramState(inputGraph);
        inputState.prepareHeap();

        ViolationPoints vioPoints = new ViolationPoints();
        vioPoints.add("x", "left");
        vioPoints.add("x", "right");

        Collection<HeapConfiguration> materializedHeaps = materializer.materialize(inputState.getHeap(), vioPoints);


        ProgramState res1 = sceneObject.scene().createProgramState(graphFactory.getExpected_MaterializeSmall2_Res1());
        res1.prepareHeap();
        ProgramState res2 = sceneObject.scene().createProgramState(graphFactory.getExpected_MaterializeSmall2_Res2());
        res2.prepareHeap();
        ProgramState res3 = sceneObject.scene().createProgramState(graphFactory.getExpected_MaterializeSmall2_Res3());
        res3.prepareHeap();

        assertTrue("should contain res1", materializedHeaps.contains(res1.getHeap()));
        assertTrue("should contain res2", materializedHeaps.contains(res2.getHeap()));
        //res3.equals( materializedStates.get(2) );
        assertTrue("should contain res3", materializedHeaps.contains(res3.getHeap()));
    }


}
