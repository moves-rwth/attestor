package de.rwth.i2.attestor.graph.morphism;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2IsomorphismChecker;
import de.rwth.i2.attestor.main.scene.SceneObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class IsomorphismCheckerTest {

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;


    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);
    }

    @Test
    public void checkIdenticalGraphs() {

        Graph g = (Graph) hcFactory.getListRule1();
        VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
        checker.run(g, g);
        assert (checker.hasMorphism());
    }

    @Test
    public void checkClonedGraphs() {

        HeapConfiguration g1 = hcFactory.getListRule1();
        HeapConfiguration g2 = g1.clone();

        VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
        checker.run((Graph) g1, (Graph) g2);
        assert (checker.hasMorphism());
    }

    @Test
    public void checkTwoFactoryCopies() {

        HeapConfiguration g1 = hcFactory.getListRule1();
        HeapConfiguration g2 = hcFactory.getListRule1();

        VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
        checker.run((Graph) g1, (Graph) g2);
        assert (checker.hasMorphism());
    }

    @Test
    public void checkNegative() {

        HeapConfiguration g1 = hcFactory.getListRule1();
        HeapConfiguration g2 = hcFactory.getListRule2();

        VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
        checker.run((Graph) g1, (Graph) g2);
        assertFalse(checker.hasMorphism());
    }

    @Test
    public void checkIdenticalWithNonterminals() {

        HeapConfiguration g1 = hcFactory.getListRule3();

        VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
        checker.run((Graph) g1, (Graph) g1);

        assert (checker.hasMorphism());

    }

    @Test
    public void checkTwoFactoryCopiesWithNonterminals() {

        HeapConfiguration g1 = hcFactory.getListRule3();
        HeapConfiguration g2 = hcFactory.getListRule3();

        VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
        checker.run((Graph) g1, (Graph) g2);

        assert (checker.hasMorphism());
    }

    @Test
    public void testGraphWithConstants() {

        HeapConfiguration g1 = hcFactory.getListAndConstantsWithChange();
        HeapConfiguration g2 = hcFactory.getListAndConstantsWithChange().clone();

        VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
        checker.run((Graph) g1, (Graph) g2);

        assert (checker.hasMorphism());

    }

    @Test
    public void testExpectedResultBoolList() {

        HeapConfiguration g1 = hcFactory.getExpectedResult_AssignStmt();
        HeapConfiguration g2 = hcFactory.getExpectedResult_AssignStmt();

        int var = g2.variableWith("XYZ");
        int node = g2.targetOf(var);

        g2.builder()
                .removeVariableEdge(var)
                .addVariableEdge("XYZ", node)
                .build();

        VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
        checker.run((Graph) g1, (Graph) g2);

        assert (checker.hasMorphism());
    }

    @Test
    public void testRejectSubgraphs() {

        HeapConfiguration empty = new InternalHeapConfiguration();
        HeapConfiguration list = hcFactory.getList();


        VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
        checker.run((Graph) empty, (Graph) list);
        assertFalse(checker.hasMorphism());
    }

    @Test
    public void testHeapsWithDifferentIndices() {

        HeapConfiguration oneIndex = hcFactory.getInput_DifferentIndices_1();
        HeapConfiguration otherIndex = hcFactory.getInput_DifferentIndices_2();

        VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
        checker.run((Graph) oneIndex, (Graph) otherIndex);
        assertFalse(checker.hasMorphism());
    }

}
