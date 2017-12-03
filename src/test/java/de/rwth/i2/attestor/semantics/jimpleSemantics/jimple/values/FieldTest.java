package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FieldTest {

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;

    private Field expr;
    private Local local;
    private HeapConfiguration testGraph;
    private SelectorLabel sel;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);

        testGraph = hcFactory.getListAndConstants();
        sel = sceneObject.scene().getSelectorLabel("next");

        Type type = sceneObject.scene().getType("List");
        local = new Local(type, "y");
        expr = new Field(type, local, sel);
    }


    @Test
    public void accessTest() {

        int hash = testGraph.hashCode();

        try {

            DefaultProgramState executable = new DefaultProgramState(testGraph.clone());
            executable.prepareHeap();

            GeneralConcreteValue res = null;
            try {
                res = (GeneralConcreteValue) expr.evaluateOn(executable);
            } catch (NullPointerDereferenceException e) {
                e.printStackTrace();
            }

            assertNotNull("testGraph null", testGraph);
            assertEquals("testGraph changed", hash, testGraph.hashCode());
            assertNotNull(executable);

            GeneralConcreteValue
                    expectedRes = executable.getVariableTarget("y");

            assertFalse("Variable 'y' should exist.", expectedRes.isUndefined());

            int expNode = expectedRes.getNode();
            expNode = executable.getHeap().selectorTargetOf(expNode, sel);

            assert res != null;
            assertEquals("doesn't return correct node", expNode, res.getNode());

            HeapConfiguration resHeapConfig = executable.getHeap();

            DefaultProgramState original = new DefaultProgramState(testGraph.clone());
            original.prepareHeap();

            assertTrue("heap should not change", original.getHeap().equals(resHeapConfig));
        } catch (NotSufficientlyMaterializedException e) {
            fail("unexpected exception" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeSelectorTest() {

        int hash = testGraph.hashCode();

        DefaultProgramState testState = new DefaultProgramState(testGraph);
        testState.prepareHeap();

        try {
            DefaultProgramState executable = testState.clone();

            ConcreteValue concreteLocal = local.evaluateOn(executable);
            try {
                expr.setValue(executable, concreteLocal);
            } catch (NullPointerDereferenceException e) {
                e.printStackTrace();
            }

            int res = -1;
            try {
                res = ((GeneralConcreteValue) expr
                        .evaluateOn(executable))
                        .getNode();
            } catch (NullPointerDereferenceException e) {
                e.printStackTrace();
            }


            assertNotNull("testGraph null", testGraph);
            assertEquals("testGraph has changed", hash, testGraph.hashCode());
            assertNotNull("resultHeap null", executable);

            int expectedRes = executable
                    .getVariableTarget("y").getNode();

            assertEquals("doesn't return correct node", expectedRes, res);

            HeapConfiguration resultHeap = executable.getHeap();
            assertFalse("heap should have changed", testState.getHeap().equals(resultHeap));

            HeapConfiguration expectedGraph = hcFactory.getListAndConstantsWithChange();
            DefaultProgramState expectedState = new DefaultProgramState(expectedGraph);
            expectedState.prepareHeap();

            assertTrue("heap not as expected", expectedState.getHeap().equals(resultHeap));

        } catch (NotSufficientlyMaterializedException e) {
            fail("unexpected exception" + e.getMessage());
            e.printStackTrace();
        }

    }


}
