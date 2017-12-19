package de.rwth.i2.attestor.ipa;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.MockupSymbolicExecutionObserver;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class IpaAbstractMethod_getResult {


    private final SceneObject sceneObject = new MockupSceneObject();
    private final IpaAbstractMethod ipa = new IpaAbstractMethod(sceneObject, "testMethod");
    private final Type type = sceneObject.scene().getType("someType");
    private final SelectorLabel sel = sceneObject.scene().getSelectorLabel("someLabel");


    @Test
    public void test() throws StateSpaceGenerationAbortedException {

        HeapConfiguration precondition = createPreCondition();
        HeapConfiguration postcondition = createPostcondition();

        ipa.addContracts(precondition, SingleElementUtil.createList(postcondition));

        ProgramState input = createInput();
        HeapConfiguration expected = createExpected();

        FragmentedHeapConfiguration fragmentedHeapConfiguration = new FragmentedHeapConfiguration(
                sceneObject, input.getHeap(), "testMethod"
        );

        assertThat(
                ipa.getIPAResult(input, new MockupSymbolicExecutionObserver(sceneObject), fragmentedHeapConfiguration),
                contains(expected)
        );
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
        return new DefaultProgramState(sceneObject, hc);

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

    private HeapConfiguration createExpected() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        hc = hc.builder()
                .addNodes(type, 3, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addVariableEdge("y", nodes.get(1))
                .addSelector(nodes.get(1), sel, nodes.get(2))
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();

        return hc;
    }

}
