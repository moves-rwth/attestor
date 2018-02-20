package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.ProgramImpl;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupMethodExecutor;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.StaticInvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.SingleElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvokeStmtTest_WithEffect {


    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;

    private DefaultProgramState testInput;
    private HeapConfiguration expectedHeap;
    private InvokeStmt stmt;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();


        hcFactory = new ExampleHcImplFactory(sceneObject);

        testInput = new DefaultProgramState(hcFactory.getInput_InvokeWithEffect());
        testInput.prepareHeap();

        DefaultProgramState expectedState = new DefaultProgramState(hcFactory.getExpectedResult_InvokeWithEffect());
        expectedState.prepareHeap();
        expectedHeap = expectedState.getHeap();

        Type type = sceneObject.scene().getType("List");
        SelectorLabel next = sceneObject.scene().getSelectorLabel("next");

        type.addSelectorLabel(next, Constants.NULL);
        Local varX = new Local(type, "x");
        Local varY = new Local(type, "y");
        Field nextOfX = new Field(type, varX, next);
        Field nextOfY = new Field(type, varY, next);

        Method method = sceneObject.scene().getOrCreateMethod("method");

        List<SemanticsCommand> methodBody = new ArrayList<>();
        methodBody.add(new IdentityStmt(sceneObject, 1, varY, "@parameter0:"));

        HashSet<String> liveVariables = new LinkedHashSet<>();
        methodBody.add(new AssignStmt(sceneObject, nextOfY, varY, 2, liveVariables));
        methodBody.add(new ReturnValueStmt(sceneObject, varY, type));
        method.setBody(new ProgramImpl(methodBody));
        method.setMethodExecution(new MockupMethodExecutor(sceneObject, method));

        StaticInvokeHelper invokeHelper = new StaticInvokeHelper(sceneObject, SingleElementUtil.createList(nextOfX));
        stmt = new InvokeStmt(sceneObject, method, invokeHelper, 1);

    }

    @Test
    public void testComputeSuccessors() {

            Collection<ProgramState> resStates = stmt.computeSuccessors(testInput);
            assertEquals(1, resStates.size());
            assertEquals(expectedHeap, resStates.iterator().next().getHeap());
    }

    @Test
    public void testNeedsMaterialization() {

        assertTrue(stmt.needsMaterialization(testInput));
    }

}
