package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.ProgramImpl;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupMethodExecutor;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InstanceInvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import de.rwth.i2.attestor.types.Type;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class InvokeStmtTest {

    private InvokeStmt stmt;
    private HeapConfiguration inputGraph;
    private ProgramState inputState;

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);

        Type type = sceneObject.scene().getType("node");
        Local var = new Local(type, "x");

        Method method = sceneObject.scene().getOrCreateMethod("method");
        List<SemanticsCommand> defaultControlFlow = new ArrayList<>();
        defaultControlFlow.add(new Skip(sceneObject, -1));
        method.setBody(new ProgramImpl(defaultControlFlow));
        method.setMethodExecution(new MockupMethodExecutor(sceneObject, method));
        InvokeHelper invokePrepare
                = new InstanceInvokeHelper(sceneObject, var, new ArrayList<>());

        stmt = new InvokeStmt(sceneObject, method, invokePrepare, 1);
        inputState = sceneObject.scene().createProgramState(hcFactory.getListAndConstants());
        inputState.prepareHeap();
        inputGraph = inputState.getHeap();
    }

    @Test
    public void testComputeSuccessors() {

            Collection<ProgramState> res = stmt.computeSuccessors(inputState);
            assertEquals(1, res.size());
            ProgramState resState = res.iterator().next();
            assertNotSame("ensure clone on state level", resState, inputState);
            assertNotSame("ensure clone on graph level", inputGraph, resState.getHeap());
            assertSame("ensure inputGraph still in inputState", inputGraph, inputState.getHeap());
            ProgramState tmp = sceneObject.scene().createProgramState(hcFactory.getListAndConstants());
            tmp.prepareHeap();
            HeapConfiguration expectedGraph = tmp.getHeap();
            assertEquals("ensure inputGraph didn't change", expectedGraph, inputGraph);
            assertEquals("ensure heap is clean again", inputGraph, resState.getHeap());
    }

    @Test
    public void testNeedsMaterialization() {

        assertFalse(stmt.needsMaterialization(inputState));
    }

    @Test
    public void testToString() {

        assertEquals("x.method();", stmt.toString());
    }
}
