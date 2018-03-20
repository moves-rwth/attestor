package de.rwth.i2.attestor.graph.morphism;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.AbstractionOptions;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2EmbeddingChecker;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2MinDistanceEmbeddingChecker;
import de.rwth.i2.attestor.main.scene.SceneObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmbeddingTest {

    ExampleHcImplFactory hcImplFactory;
    private SceneObject sceneObject;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        hcImplFactory = new ExampleHcImplFactory(sceneObject);
    }

    @Test
    public void testIdenticalGraphs() {

        Graph g = (Graph) hcImplFactory.getListRule1();
        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run(g, g);
        assertTrue("Identical graphs are embeddings of each other", checker.hasMorphism());
    }

    @Test
    public void testSimpleDLLEmbedding() {

        Graph p = (Graph) hcImplFactory.getTwoElementDLL();
        Graph t = (Graph) hcImplFactory.getThreeElementDLL();

        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run(p, t);
        assertTrue("Two element DLL is embedded in three element DLL", checker.hasMorphism());
    }

    @Test
    public void testWithInternal() {

        Graph p = (Graph) hcImplFactory.getThreeElementDLL();
        Graph t = (Graph) hcImplFactory.getFiveElementDLL();

        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run(p, t);
        assertTrue("Three element DLL is embedded in five element DLL, both with 2 external nodes", checker.hasMorphism());
    }

    @Test
    public void testNegative() {


        Graph p = (Graph) hcImplFactory.getBrokenFourElementDLL();
        Graph t = (Graph) hcImplFactory.getFiveElementDLL();

        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run(p, t);
        assertFalse("Three element DLL with one additional pointer not embedded in five element dll", checker.hasMorphism());
    }

    @Test
    public void testSLLRule2() {

        Graph p = (Graph) hcImplFactory.getListRule2();
        Graph t = (Graph) hcImplFactory.getListRule2Test();

        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run(p, t);
        assertTrue(checker.hasMorphism());
    }

    @Test
    public void testSLLRule2Fail() {

        Graph p = (Graph) hcImplFactory.getListRule2();
        Graph t = (Graph) hcImplFactory.getListRule2TestFail();

        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run(p, t);
        assertFalse(checker.hasMorphism());
    }

    @Test
    public void testSLLRule3() {

        Graph p = (Graph) hcImplFactory.getListRule3();
        Graph t = (Graph) hcImplFactory.getTestForListRule3();

        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run(p, t);
        assertTrue(checker.hasMorphism());
    }

    @Test
    public void testSLLRule3Fail() {

        Graph p = (Graph) hcImplFactory.getListRule3();
        Graph t = (Graph) hcImplFactory.getTestForListRule3Fail();

        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run(p, t);
        assertFalse(checker.hasMorphism());
    }

    @Test
    public void testAllExternal() {

        Graph p = (Graph) hcImplFactory.getTreeLeaf();
        Graph t = (Graph) hcImplFactory.get2TreeLeaf();

        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run(p, t);
        assertTrue(checker.hasMorphism());

    }

    @Test
    public void testDLLWithThirdPointer() {

        Graph p = (Graph) hcImplFactory.getDLL2Rule();
        Graph t = (Graph) hcImplFactory.getDLLTarget();

        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run(p, t);
        assertTrue(checker.hasMorphism());

    }

    @Test
    public void testHeapsWithDifferentIndices() {

        HeapConfiguration oneIndex = hcImplFactory.getInput_DifferentIndices_1();
        HeapConfiguration otherIndex = hcImplFactory.getInput_DifferentIndices_2();

        VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
        checker.run((Graph) oneIndex, (Graph) otherIndex);
        assertTrue(checker.hasMorphism());
    }

    @Test
    public void testAbstractionDistance_shouldFindEmbedding() {

        HeapConfiguration inputWithEnoughDistance = hcImplFactory.getInput_EnoughAbstractionDistance();
        HeapConfiguration matchingPattern = hcImplFactory.getPattern_PathAbstraction();

        AbstractionOptions options = new AbstractionOptions()
                .setAdmissibleAbstraction(true);

        VF2MinDistanceEmbeddingChecker checker = new VF2MinDistanceEmbeddingChecker(options);
        checker.run((Graph) matchingPattern, (Graph) inputWithEnoughDistance);
        assertTrue(checker.hasMorphism());
    }

    @Test
    public void testAbstractionDistance_shouldNotFindEmbedding() {

        HeapConfiguration inputWithoutEnoughDistance = hcImplFactory.getInput_NotEnoughAbstractionDistance();
        HeapConfiguration matchingPattern = hcImplFactory.getPattern_GraphAbstraction();

        AbstractionOptions options = new AbstractionOptions()
                .setAdmissibleAbstraction(true);

        VF2MinDistanceEmbeddingChecker checker = new VF2MinDistanceEmbeddingChecker(options);
        checker.run((Graph) matchingPattern, (Graph) inputWithoutEnoughDistance);
        assertFalse(checker.hasMorphism());
    }

    @Test
    public void testAbstractionDistance_onlyNonterminalEdge_shouldFindEmbedding() {

        HeapConfiguration inputWithEnoughDistance = hcImplFactory.getInput_OnlyNonterminalEdgesToAbstract();
        HeapConfiguration matchingPattern = hcImplFactory.getPattern_PathAbstraction();

        AbstractionOptions options = new AbstractionOptions()
                .setAdmissibleAbstraction(true);

        VF2MinDistanceEmbeddingChecker checker = new VF2MinDistanceEmbeddingChecker(options);
        checker.run((Graph) matchingPattern, (Graph) inputWithEnoughDistance);
        assertTrue(checker.hasMorphism());
    }

    @Test
    public void testAbstractionDistance_variableContains0_shouldNotFindEmbedding() {

        HeapConfiguration inputWithoutEnoughDistance = hcImplFactory.getInput_variableContains0();
        HeapConfiguration matchingPattern = hcImplFactory.getPattern_variableContains0();

        AbstractionOptions options = new AbstractionOptions()
                .setAdmissibleAbstraction(true);

        VF2MinDistanceEmbeddingChecker checker = new VF2MinDistanceEmbeddingChecker(options);
        checker.run((Graph) matchingPattern, (Graph) inputWithoutEnoughDistance);
        assertFalse(checker.hasMorphism());
    }

}
