package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.AbstractionOptions;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestEmbeddings {

    private HeapConfiguration rhs1;
    private HeapConfiguration rhs2;

    private SceneObject sceneObject;
    private ExampleIndexedGraphFactory graphFactory;

    private AbstractionOptions abstractionOptions;

    @Before
    public void init() {

        sceneObject = new MockupSceneObject();
        sceneObject.scene().options().setIndexedModeEnabled(true);

        graphFactory = new ExampleIndexedGraphFactory(sceneObject);

        abstractionOptions = new AbstractionOptions().setAdmissibleConstants(
                sceneObject.scene().options().isAdmissibleConstantsEnabled()
        );

        AnnotatedSelectorLabel leftLabel = new AnnotatedSelectorLabel(sceneObject.scene().getSelectorLabel("left"), "0");
        AnnotatedSelectorLabel rightLabel = new AnnotatedSelectorLabel(sceneObject.scene().getSelectorLabel("right"), "0");

        rhs1 = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        rhs1 = rhs1.builder().addNodes(sceneObject.scene().getType("AVLTree"), 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes.get(0), rightLabel, nodes.get(1))
                .build();

        Type zType = sceneObject.scene().getType("int_0");
        AnnotatedSelectorLabel balance = new AnnotatedSelectorLabel(sceneObject.scene().getSelectorLabel("balancing"), "");

        rhs2 = new InternalHeapConfiguration();
        TIntArrayList nodes2 = new TIntArrayList();
        rhs2 = rhs2.builder().addNodes(sceneObject.scene().getType("AVLTree"), 2, nodes2)
                .addNodes(zType, 1, nodes2)
                .setExternal(nodes2.get(0))
                .setExternal(nodes2.get(1))
                .setExternal(nodes2.get(2))
                .addSelector(nodes2.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes2.get(0), rightLabel, nodes.get(1))
                .addSelector(nodes2.get(0), balance, nodes2.get(2))
                .build();
    }

    @Test
    public void testCanonizePractical() {

        ProgramState input = sceneObject.scene().createProgramState(graphFactory.getInput_practicalCanonize());
        input.prepareHeap();
        AbstractMatchingChecker checker = input.getHeap().getEmbeddingsOf(rhs1, abstractionOptions);
        assertTrue(checker.hasMatching());
    }

    @Test
    public void testCanonizePractical2() {

        ProgramState input = sceneObject.scene().createProgramState(graphFactory.getInput_practicalCanonize2());
        input.prepareHeap();

        AbstractMatchingChecker checker = input.getHeap().getEmbeddingsOf(rhs2, abstractionOptions);
        assertTrue(checker.hasMatching());
    }

    @Test
    public void testCanonizePractical3() {

        ProgramState input = sceneObject.scene().createProgramState(graphFactory.getInput_practicalCanonize3());
        input.prepareHeap();

        AbstractMatchingChecker checker = input.getHeap().getEmbeddingsOf(
                graphFactory.getEmbedding_practicalCanonize3(),
                abstractionOptions
        );

        assertTrue(checker.hasMatching());
    }

    @Test
    public void testCanonizeWithInst() {

        ProgramState input = sceneObject.scene().createProgramState(graphFactory.getInput_Cononize_withInstNecessary());
        input.prepareHeap();

        AbstractMatchingChecker checker = input.getHeap().getEmbeddingsOf(
                graphFactory.getRule_Cononize_withInstNecessary(),
                abstractionOptions
        );

        assertTrue(checker.hasMatching());
    }

    @Test
    public void testEmbedding5() {
        //smaller version of testCanonizeWithInst()
        ProgramState input = sceneObject.scene().createProgramState(graphFactory.getInput_Embedding5());
        input.prepareHeap();

        AbstractMatchingChecker checker = input.getHeap().getEmbeddingsOf(
                graphFactory.getRule_Cononize_withInstNecessary(),
                abstractionOptions
        );

        assertTrue(checker.hasMatching());
    }
}
