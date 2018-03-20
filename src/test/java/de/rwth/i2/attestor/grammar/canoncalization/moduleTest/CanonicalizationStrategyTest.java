package de.rwth.i2.attestor.grammar.canoncalization.moduleTest;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.AbstractionOptions;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultCanonicalizationHelper;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import gnu.trove.list.array.TIntArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CanonicalizationStrategyTest {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger("CanonicalizationStrategyTest");

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;

    private GeneralCanonicalizationStrategy canonicalizationStrategy;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);

        Nonterminal listLabel = sceneObject.scene().createNonterminal("List", 2, new boolean[]{false, true});

        Grammar grammar = Grammar.builder()
                .addRule(listLabel, hcFactory.getListRule1())
                .addRule(listLabel, hcFactory.getListRule2())
                .addRule(listLabel, hcFactory.getListRule3())
                .updateCollapsedRules()
                .build();

        AbstractionOptions options = new AbstractionOptions()
                .setAdmissibleConstants(
                        sceneObject.scene().options().isAdmissibleConstantsEnabled()
                );


        EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(options);
        CanonicalizationHelper canonicalizationHelper = new DefaultCanonicalizationHelper(checkerProvider);

        canonicalizationStrategy = new GeneralCanonicalizationStrategy(grammar, canonicalizationHelper);
    }

    @Test
    public void testSmall() {

        HeapConfiguration test = hcFactory.getCanonizationTest1();

        ProgramState testExec = new DefaultProgramState(test);
        ProgramState state = testExec.shallowCopyWithUpdateHeap(canonicalizationStrategy.canonicalize(testExec.getHeap()));

        assertEquals("Input heap should not change", hcFactory.getCanonizationTest1(), test);

        assertEquals("result not as expected", hcFactory.getCanonizationRes1(), state.getHeap());
    }

    @Test
    public void testCyclic() {

        HeapConfiguration test = hcFactory.getCyclicList();

        HeapConfiguration result = canonicalizationStrategy.canonicalize(test);

        assertEquals(hcFactory.getAbstractCyclicList(), result);

    }

    @Test
    public void testBig() {

        HeapConfiguration test = hcFactory.getCanonizationTest2();
        ProgramState testExec = new DefaultProgramState(test);
        ProgramState state = testExec.shallowCopyWithUpdateHeap(canonicalizationStrategy.canonicalize(testExec.getHeap()));

        assertEquals("Input heap should not change", hcFactory.getCanonizationTest2(), test);

        assertEquals("result not as expected", hcFactory.getCanonizationRes1(), state.getHeap());
    }

    @Test
    public void testWithVariable() {

        HeapConfiguration test = hcFactory.getCanonizationTest3();

        ProgramState testExec = new DefaultProgramState(test);
        ProgramState state = testExec.shallowCopyWithUpdateHeap(canonicalizationStrategy.canonicalize(testExec.getHeap()));

        assertEquals("Input heap should not change", hcFactory.getCanonizationTest3(), test);
        assertEquals("result not as expected", hcFactory.getCanonizationRes3(), state.getHeap());
    }

    @Test
    public void testLongSllFullAbstraction() {

        HeapConfiguration test = hcFactory.getLongConcreteSLL();
        ProgramState testExec = new DefaultProgramState(test);
        ProgramState state = testExec.shallowCopyWithUpdateHeap(canonicalizationStrategy.canonicalize(testExec.getHeap()));

        HeapConfiguration expected = hcFactory.getSLLHandle();

        assertEquals(expected, state.getHeap());
    }

    @Test
    public void testLongSllFullAbstractionWithVariables() {

        HeapConfiguration test = hcFactory.getLongConcreteSLL().clone();

        TIntArrayList nodes = test.nodes();

        test.builder()
                .addVariableEdge("x", nodes.get(0))
                .addVariableEdge("y", nodes.get(9))
                .build();


        ProgramState testExec = new DefaultProgramState(test);
        ProgramState state = testExec.shallowCopyWithUpdateHeap(canonicalizationStrategy.canonicalize(testExec.getHeap()));

        HeapConfiguration expected = hcFactory.getSLLHandle();
        TIntArrayList expectedNodes = expected.nodes();

        expected.builder()
                .addVariableEdge("x", expectedNodes.get(0))
                .addVariableEdge("y", expectedNodes.get(1))
                .build();

        assertEquals(expected, state.getHeap());
    }
}
