package de.rwth.i2.attestor.programState.indexedState.semantics;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.ExampleIndexedGraphFactory;
import de.rwth.i2.attestor.programState.indexedState.IndexedState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.AssignStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NewExpr;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

public class IndexedSemanticsCommandTest {

    private SceneObject sceneObject;
    private ExampleIndexedGraphFactory graphFactory;

    @Before
    public void init() {

        sceneObject = new MockupSceneObject();
        graphFactory = new ExampleIndexedGraphFactory(sceneObject);
    }

    @Test
    public void testFieldAssign() {

        IndexedState input = new IndexedState(graphFactory.getInput_FieldAccess());
        input.prepareHeap();
        IndexedState expected = new IndexedState(graphFactory.getExpected_FieldAccess());
        expected.prepareHeap();

        Type type = sceneObject.scene().getType("AVLTree");
        SelectorLabel left = sceneObject.scene().getSelectorLabel("left");
        type.addSelectorLabel(left, Constants.NULL);

        Local varX = new Local(type, "x");
        Field xLeft = new Field(type, varX, left);
        AssignStmt stmt = new AssignStmt(sceneObject, varX, xLeft, 0, new LinkedHashSet<>());
        Collection<ProgramState> result = stmt.computeSuccessors(input);
        assertEquals(1, result.size());
        assertEquals(expected, result.iterator().next());
    }

    @Test
    public void testNew() {

        IndexedState input = new IndexedState(graphFactory.getExpected_FieldAccess());
        input.prepareHeap();
        IndexedState expected = new IndexedState(graphFactory.getExpected_newNode());
        expected.prepareHeap();

        Type type = sceneObject.scene().getType("AVLTree");

        Local varTmp = new Local(type, "tmp");
        NewExpr expr = new NewExpr(type);
        AssignStmt stmt = new AssignStmt(sceneObject, varTmp, expr, 0, new LinkedHashSet<>());
        Collection<ProgramState> result = stmt.computeSuccessors(input);
        assertEquals(1, result.size());
        assertEquals(expected, result.iterator().next());
    }

    @Test
    public void testAssignField() {

        IndexedState input = new IndexedState(graphFactory.getExpected_newNode());
        input.prepareHeap();
        IndexedState expected = new IndexedState(graphFactory.getExpected_fieldAssign());
        expected.prepareHeap();

        Type type = sceneObject.scene().getType("AVLTree");
        SelectorLabel left = sceneObject.scene().getSelectorLabel("left");
        type.addSelectorLabel(left, Constants.NULL);

        Local varTmp = new Local(type, "tmp");
        Local varX = new Local(type, "x");
        Field xLeft = new Field(type, varX, left);
        AssignStmt stmt = new AssignStmt(sceneObject, xLeft, varTmp, 0, new LinkedHashSet<>());

        Collection<ProgramState> result = stmt.computeSuccessors(input);
        assertEquals(1, result.size());
        assertEquals(expected, result.iterator().next());
    }
}
