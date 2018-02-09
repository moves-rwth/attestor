package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.IfStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
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

public class PrepareHeapTest {
    //private static final Logger logger = LogManager.getLogger( "PrepareHeapTest.java" );

    private SceneObject sceneObject = new MockupSceneObject();

    private HeapConfiguration testGraph;
    private int truePC;
    private int falsePC;
    private Type listType;

    @Before
    public void setUp() throws Exception {

        ExampleHcImplFactory hcFactory = new ExampleHcImplFactory(sceneObject);
        testGraph = hcFactory.getList();
        listType = sceneObject.scene().getType("List");

        truePC = 5;
        falsePC = 7;
    }

    @Test
    public void testWithLocal() {

        Value leftExpr = new Local(listType, "x");
        Value rightExpr = new NullConstant();
        Value condition = new EqualExpr(leftExpr, rightExpr);
        Statement stmt = new IfStmt(sceneObject, condition, truePC, falsePC, new LinkedHashSet<>());

            DefaultProgramState input = new DefaultProgramState(testGraph);
            input.prepareHeap();
            Collection<ProgramState> res = stmt.computeSuccessors(input);

            assertEquals("result should have size 1", 1, res.size());


            for (ProgramState s : res) {
                assertTrue("condition should evaluate to false", s.getProgramCounter() == falsePC);
                assertFalse("condition has evaluated to true", s.getProgramCounter() == truePC);
                assertNotNull("resHeap null", s.getHeap());
            }
    }

    @Test
    public void testWithField() {

        SelectorLabel next = sceneObject.scene().getSelectorLabel("next");

        Value origin = new Local(listType, "x");
        Value leftExpr = new Field(listType, origin, next);
        Value rightExpr = new NullConstant();
        Value condition = new EqualExpr(leftExpr, rightExpr);
        Statement stmt = new IfStmt(sceneObject, condition, truePC, falsePC, new LinkedHashSet<>());

            DefaultProgramState input = new DefaultProgramState(testGraph);
            input.prepareHeap();
            Collection<ProgramState> res = stmt.computeSuccessors(input);

            assertEquals("result should have size 1", 1, res.size());

            for (ProgramState s : res) {
                assertTrue("condition should evaluate to false", s.getProgramCounter() == falsePC);
                assertFalse("condition has evaluated to true", s.getProgramCounter() == truePC);
                assertNotNull("resHeap null", s.getHeap());
            }
    }

    @Test
    public void testToTrue() {

        SelectorLabel next = sceneObject.scene().getSelectorLabel("next");

        Value origin1 = new Local(listType, "x");
        Value origin2 = new Field(listType, origin1, next);
        Value origin3 = new Field(listType, origin2, next);
        Value leftExpr = new Field(listType, origin3, next);
        Value rightExpr = new NullConstant();
        Value condition = new EqualExpr(leftExpr, rightExpr);
        Statement stmt = new IfStmt(sceneObject, condition, truePC, falsePC, new LinkedHashSet<>());

            DefaultProgramState input = new DefaultProgramState(testGraph);
            input.prepareHeap();

            Collection<ProgramState> res = stmt.computeSuccessors(input);

            assertEquals("result should have size 1", 1, res.size());

            for (ProgramState s : res) {
                assertFalse("condition should evaluate to true, but got false", s.getProgramCounter() == falsePC);
                assertTrue("condition should evaluate to true", s.getProgramCounter() == truePC);
                assertNotNull("resHeap null", s.getHeap());
            }
    }

}
