package de.rwth.i2.attestor.procedures.methodExecution;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.phases.symbolicExecution.util.InternalPreconditionMatchingStrategy;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.procedures.MethodExecutor;
import de.rwth.i2.attestor.procedures.contracts.InternalContract;
import de.rwth.i2.attestor.procedures.contracts.InternalContractCollection;
import de.rwth.i2.attestor.procedures.scopes.DefaultScopeExtractor;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class NonRecursiveMethodExecutorTest {

    private final SceneObject sceneObject = new MockupSceneObject();
    private final Type type = sceneObject.scene().getType("someType");
    private final SelectorLabel sel = sceneObject.scene().getSelectorLabel("someLabel");

    @Test
    public void test() {

        String methodName = "testMethod";

        ContractCollection contractCollection = new InternalContractCollection(new InternalPreconditionMatchingStrategy());

        HeapConfiguration precondition = createPreCondition();
        HeapConfiguration postcondition = createPostcondition();

        contractCollection.addContract(new InternalContract(precondition, Collections.singleton(postcondition)));


        MethodExecutor executor = new NonRecursiveMethodExecutor(
                new DefaultScopeExtractor(sceneObject, methodName),
                contractCollection,
                initialState -> { throw new IllegalStateException("Should not attempt to generate new contract"); }
        );

        ProgramState input = createInput();
        ProgramState expected = createExpected();

        assertThat(executor.getResultStates(null, input), contains(expected));
    }

    private ProgramState createInput() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        hc = hc.builder()
                .addNodes(type, 3, nodes)
                .addVariableEdge("@parameter0:", nodes.get(0))
                .addVariableEdge("@parameter1:", nodes.get(1))
                .addVariableEdge("x", nodes.get(0))
                .addVariableEdge("y", nodes.get(1))
                .addSelector(nodes.get(1), sel, nodes.get(2))
                .build();
        return new DefaultProgramState(hc);

    }

    private HeapConfiguration createPreCondition() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        hc = hc.builder()
                .addNodes(type, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addVariableEdge("@parameter0:", nodes.get(0))
                .addVariableEdge("@parameter1:", nodes.get(1))
                .addSelector(nodes.get(1), sel, nodes.get(2))
                .build();

        return hc;
    }

    private HeapConfiguration createPostcondition() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        hc = hc.builder()
                .addNodes(type, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(1), sel, nodes.get(2))
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();

        return hc;
    }

    private ProgramState createExpected() {

        HeapConfiguration hc = sceneObject.scene().createHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        hc = hc.builder()
                .addNodes(type, 3, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addVariableEdge("y", nodes.get(1))
                .addSelector(nodes.get(1), sel, nodes.get(2))
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();

        return new DefaultProgramState(hc);
    }

}
