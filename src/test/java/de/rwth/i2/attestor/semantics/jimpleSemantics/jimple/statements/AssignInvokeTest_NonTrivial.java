package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.StaticInvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NewExpr;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.ProgramImpl;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class AssignInvokeTest_NonTrivial {

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;

    private AssignInvoke stmt;
    private HeapConfiguration inputGraph;
    private DefaultProgramState inputState;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);

        Type type = sceneObject.scene().getType("AssignInvokeTestNonTrivial");

        SelectorLabel next = sceneObject.scene().getSelectorLabel("next");
        SelectorLabel prev = sceneObject.scene().getSelectorLabel("prev");
        type.addSelectorLabel(next, Constants.NULL);
        type.addSelectorLabel(prev, Constants.NULL);

        Local var = new Local(type, "x");

        AbstractMethod method = sceneObject.scene().getMethod("method");
        List<SemanticsCommand> defaultControlFlow = new ArrayList<>();
        defaultControlFlow.add(new AssignStmt(sceneObject, var, new NewExpr(type), 1, new LinkedHashSet<>()));
        defaultControlFlow.add(new ReturnValueStmt(sceneObject, var, type));
        method.setControlFlow(new ProgramImpl(defaultControlFlow));
        InvokeHelper invokePrepare = new StaticInvokeHelper(sceneObject, new ArrayList<>());

        stmt = new AssignInvoke(sceneObject, var, method, invokePrepare, 1);

        inputGraph = hcFactory.getListAndConstants();
        inputState = new DefaultProgramState(sceneObject, inputGraph);
    }

    @Test
    public void testComputeSuccessors() {

        try {
            Set<ProgramState> resStates = stmt.computeSuccessors(inputState, new MockupSymbolicExecutionObserver(sceneObject));
            assertEquals(1, resStates.size());
            DefaultProgramState resState = (DefaultProgramState) resStates.iterator().next();
            assertNotSame(resState, inputState);
            assertNotSame(inputGraph, resState.getHeap());
            assertSame(inputGraph, inputState.getHeap());
            assertEquals(hcFactory.getListAndConstants(), inputGraph);
            assertFalse(inputGraph.equals(resState.getHeap()));
            assertEquals(hcFactory.getExpectedResult_AssignInvokeNonTrivial(), resState.getHeap());
        } catch (NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e) {
            fail("unexpected exception: " + e.getMessage());
        }
    }


    @Test
    public void testNeedsMaterialization() {

        assertFalse(stmt.needsMaterialization(inputState));
    }
}
