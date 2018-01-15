package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewExprTest {

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcImplFactory;

    private NewExpr expr;
    private HeapConfiguration testGraph;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        hcImplFactory = new ExampleHcImplFactory(sceneObject);
        Type type = sceneObject.scene().getType("NewExprTestNode");
        SelectorLabel next = sceneObject.scene().getSelectorLabel("next");
        type.addSelectorLabel(next, Constants.NULL);
        expr = new NewExpr(type);
        testGraph = hcImplFactory.getThreeElementDLLWithConstants();
    }

    @Test
    public void test() {

        int hash = testGraph.hashCode();
        int oldNodeNumber = testGraph.countNodes();

        ProgramState executable = sceneObject.scene().createProgramState(testGraph.clone());

        expr.evaluateOn(executable);

        assertNotNull("testGraph null", testGraph);
        assertEquals("testGraph changed", hash, testGraph.hashCode());
        assertEquals("node number did not increase by one", oldNodeNumber + 1, executable.getHeap().countNodes());
        assertFalse(testGraph.equals(executable.getHeap()));
        HeapConfiguration expected = hcImplFactory.getExepectedResultTestNewExprTest();
        assertEquals(expected, executable.getHeap());
    }

}
