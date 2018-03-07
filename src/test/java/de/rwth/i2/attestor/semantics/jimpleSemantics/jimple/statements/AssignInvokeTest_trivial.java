package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalContractCollection;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalPreconditionMatchingStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes.DefaultScopeExtractor;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.NonRecursiveMethodExecutor;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.ProgramImpl;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.ProcedureRegistryStub;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.StaticInvokeHelper;
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

public class AssignInvokeTest_trivial {

    private AssignInvoke stmt;
    private HeapConfiguration inputGraph;
    private ProgramState inputState;

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);

        Type type = hcFactory.scene().getType("node");
        Local var
                = new Local(type, "x");


        Method method = sceneObject.scene().getOrCreateMethod("method");
        List<SemanticsCommand> defaultControlFlow = new ArrayList<>();
        defaultControlFlow.add(new Skip(sceneObject, -1));
        method.setBody(new ProgramImpl(defaultControlFlow));
        method.setMethodExecution( new NonRecursiveMethodExecutor(   method, 
        															new DefaultScopeExtractor(sceneObject, method.getName()),
        															new InternalContractCollection( new InternalPreconditionMatchingStrategy()), 
        															new ProcedureRegistryStub(sceneObject) ) );
        InvokeHelper invokePrepare = new StaticInvokeHelper(sceneObject, new ArrayList<>());

        stmt = new AssignInvoke(sceneObject, var, method, invokePrepare, 1);

        inputGraph = hcFactory.getListAndConstants();
        inputState = new DefaultProgramState(inputGraph);
    }

    @Test
    public void testComputeSuccessors() {

            Collection<ProgramState> resStates = stmt.computeSuccessors(inputState);
            assertEquals(1, resStates.size());
            DefaultProgramState resState = (DefaultProgramState) resStates.iterator().next();
            assertNotSame(resState, inputState);
            assertNotSame(inputGraph, resState.getHeap());
            assertSame(inputGraph, inputState.getHeap());
            assertEquals(hcFactory.getListAndConstants(), inputGraph);
            assertEquals(inputGraph, resState.getHeap());
    }


    @Test
    public void testNeedsMaterialization() {

        assertFalse(stmt.needsMaterialization(inputState));
    }

    @Test
    public void testToString() {

        assertEquals("x = method();", stmt.toString());
    }
}
