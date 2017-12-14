package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.BalancedTreeGrammar;
import de.rwth.i2.attestor.programState.indexedState.ExampleIndexedGraphFactory;
import de.rwth.i2.attestor.programState.indexedState.IndexedState;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeneralMaterializationStrategyTest_Materialize_Indexed_OldExamples {

    private SceneObject sceneObject;
    private ExampleIndexedGraphFactory graphFactory;
    private GeneralMaterializationStrategy materializer;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
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
        IndexedState inputState = new IndexedState(sceneObject, inputGraph);
        inputState.prepareHeap();

        HeapConfiguration expectedGraph = graphFactory.getExpected_MaterializeSmall_Z();
        IndexedState expectedState = new IndexedState(sceneObject, expectedGraph);
        expectedState.prepareHeap();

        ViolationPoints vioPoints = new ViolationPoints();
        vioPoints.add("x", "left");
        vioPoints.add("x", "right");

        List<ProgramState> materializedStates = materializer.materialize(inputState, vioPoints);

        assertEquals(1, materializedStates.size());
        assertEquals(expectedState, materializedStates.get(0));
    }

    @Test
    public void testMaterialize_small_sZ() {

        HeapConfiguration inputGraph = graphFactory.getInput_MaterializeSmall_sZ();
        IndexedState inputState = new IndexedState(sceneObject, inputGraph);
        inputState.prepareHeap();

        ViolationPoints vioPoints = new ViolationPoints();
        vioPoints.add("x", "left");
        vioPoints.add("x", "right");

        List<ProgramState> materializedStates = materializer.materialize(inputState, vioPoints);
        //assertEquals( 3, materializedStates.size() );


        IndexedState res1 = new IndexedState(sceneObject, graphFactory.getExpected_MaterializeSmall2_Res1());
        res1.prepareHeap();
        IndexedState res2 = new IndexedState(sceneObject, graphFactory.getExpected_MaterializeSmall2_Res2());
        res2.prepareHeap();
        IndexedState res3 = new IndexedState(sceneObject, graphFactory.getExpected_MaterializeSmall2_Res3());
        res3.prepareHeap();

        assertTrue("should contain res1", materializedStates.contains(res1));
        assertTrue("should contain res2", materializedStates.contains(res2));
        //res3.equals( materializedStates.get(2) );
        assertTrue("should contain res3", materializedStates.contains(res3));
    }


}
