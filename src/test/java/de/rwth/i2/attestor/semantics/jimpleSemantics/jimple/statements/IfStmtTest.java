package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullConstant;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.boolExpr.EqualExpr;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedHashSet;

import static org.junit.Assert.*;

public class IfStmtTest {

    private HeapConfiguration testGraph;
    private int truePC;
    private int falsePC;
    private Type listType;

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);

        testGraph = hcFactory.getListAndConstants();
        listType = sceneObject.scene().getType("node");

        truePC = 5;
        falsePC = 7;

    }

    @Test
    public void testWithLocal() {

        int hash = testGraph.hashCode();

        ProgramState testState = sceneObject.scene().createProgramState(testGraph);
        testState.prepareHeap();

        Value leftExpr = new Local(listType, "y");
        Value rightExpr = new NullConstant();
        Value condition = new EqualExpr(leftExpr, rightExpr);

        Statement stmt = new IfStmt(sceneObject, condition, truePC, falsePC, new LinkedHashSet<>());

            ProgramState input = testState.clone();

            Collection<ProgramState> res = stmt.computeSuccessors(input);

            assertEquals("test Graph changed", hash, testGraph.hashCode());
            assertEquals("result should have size 1", 1, res.size());

            for (ProgramState resProgramState : res) {

                DefaultProgramState resState = (DefaultProgramState) resProgramState;

                assertTrue("condition should evaluate to false", resState.getProgramCounter() == falsePC);
                assertFalse("condition has evaluated to true", resState.getProgramCounter() == truePC);
                assertNotNull("resHeap null", resState.getHeap());

                assertTrue("Heap after evaluating condition should not change",
                        testState.getHeap().equals(resState.getHeap()));
            }
    }

    @Test
    public void testWithField() {

        int hash = testGraph.hashCode();

        ProgramState testState = sceneObject.scene().createProgramState(testGraph);
        testState.prepareHeap();

        SelectorLabel next = sceneObject.scene().getSelectorLabel("next");

        Value origin = new Local(listType, "y");
        Value leftExpr = new Field(listType, origin, next);
        Value rightExpr = new NullConstant();
        Value condition = new EqualExpr(leftExpr, rightExpr);

        Statement stmt = new IfStmt(sceneObject, condition, truePC, falsePC, new LinkedHashSet<>());

            ProgramState input = testState.clone();
            Collection<ProgramState> res = stmt.computeSuccessors(input);

            assertEquals("test Graph changed", hash, testGraph.hashCode());
            assertEquals("result should have size 1", 1, res.size());

            for (ProgramState resProgramState : res) {

                DefaultProgramState resState = (DefaultProgramState) resProgramState;

                assertTrue("condition should evaluate to false", resState.getProgramCounter() == falsePC);
                assertFalse("condition has evaluated to true", resState.getProgramCounter() == truePC);

                assertTrue("Heap after evaluating condition should not change",
                        testState.getHeap().equals(resState.getHeap()));
            }
    }

    @Test
    public void testToTrue() {

        int hash = testGraph.hashCode();
        SelectorLabel next = sceneObject.scene().getSelectorLabel("next");

        ProgramState testState = sceneObject.scene().createProgramState(testGraph);
        testState.prepareHeap();

        Value origin1 = new Local(listType, "y");
        Value origin2 = new Field(listType, origin1, next);
        Value origin3 = new Field(listType, origin2, next);
        Value leftExpr = new Field(listType, origin3, next);
        Value rightExpr = new NullConstant();
        Value condition = new EqualExpr(leftExpr, rightExpr);

        Statement stmt = new IfStmt(sceneObject, condition, truePC, falsePC, new LinkedHashSet<>());

            ProgramState input = testState.clone();

            Collection<ProgramState> res = stmt.computeSuccessors(input);

            assertEquals("test Graph changed", hash, testGraph.hashCode());
            assertEquals("result should have size 1", 1, res.size());

            for (ProgramState resProgramState : res) {

                ProgramState resState = resProgramState;

                assertFalse("condition should evaluate to true, but got false", resState.getProgramCounter() == falsePC);
                assertTrue("condition should evaluate to true", resState.getProgramCounter() == truePC);

                assertTrue("Heap after evaluating condition should not change",
                        testState.getHeap().equals(resState.getHeap()));
            }
    }

}
