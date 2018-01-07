package de.rwth.i2.attestor.main.phases.symbolicExecution.interprocedural;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.PhaseRegistry;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class InterproceduralAnalysisTest {

    private SceneObject sceneObject;
    private SelectorLabel next;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        next = sceneObject.scene().getSelectorLabel("next");
    }

    @Test
    public void testNonRecursive() {

        Type type = sceneObject.scene().getType("List");
        String paramName = "@this";
        int startPos = 0;

        HeapConfiguration input = exampleList(type, paramName, startPos);

        ExampleRecursiveProgram examplePrograms = new ExampleRecursiveProgram(sceneObject, type, paramName, next);

        InterproceduralAnalysisPhase ipaPhase = new InterproceduralAnalysisPhase(sceneObject.scene());
        PhaseRegistry registry = new PhaseRegistry()
            .addPhase(
                new MockupPhase(sceneObject, examplePrograms.getCallNextProgram(), Collections.singletonList(input))
            ).addPhase(ipaPhase);
        registry.execute();

        StateSpace stateSpace = ipaPhase.getStateSpace();
        assertNotNull(stateSpace);

        Collection<ProgramState> result = stateSpace.getFinalStates();

        final HeapConfiguration expectedHeap = exampleList(type,"@return",startPos+1);
        final ProgramState expectedState = sceneObject.scene().createProgramState(expectedHeap);

        assertEquals( 1, result.size() );
        assertEquals(expectedState.getHeap(), result.iterator().next().getHeap());
    }

    @Ignore
    @Test
    public void testRecursive() {
        Type type = sceneObject.scene().getType("List");
        final String paramName = "@this:";
        final int startPos = 0;
        HeapConfiguration input = exampleList(type, paramName, startPos);

        ExampleRecursiveProgram examplePrograms = new ExampleRecursiveProgram(sceneObject, type, paramName, next);

        Method traverseMethod = sceneObject.scene().getMethod("traverse");
        traverseMethod.setRecursive(true);
        traverseMethod.setBody(examplePrograms.getRecursiveProgram(traverseMethod) );


        InterproceduralAnalysisPhase ipaPhase = new InterproceduralAnalysisPhase(sceneObject.scene());
        PhaseRegistry registry = new PhaseRegistry()
                .addPhase(
                        new MockupPhase(sceneObject, traverseMethod.getBody(), Collections.singletonList(input))
                ).addPhase(ipaPhase);
        registry.execute();

        StateSpace stateSpace = ipaPhase.getStateSpace();
        assertNotNull(stateSpace);

        Collection<ProgramState> result = stateSpace.getFinalStates();
        final HeapConfiguration expectedHeap = exampleList(type,"@return",2);
        final ProgramState expectedState = sceneObject.scene().createProgramState(expectedHeap);

        assertEquals( 1, result.size() );
        assertEquals(expectedState.getHeap(), result.iterator().next().getHeap());
    }


    private HeapConfiguration exampleList(Type type, String varName, int varPos){
        Type nullType = sceneObject.scene().getType("NULL");
        HeapConfiguration hc = sceneObject.scene().createHeapConfiguration();

        type.addSelectorLabel(next, Constants.NULL);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder()
                .addNodes(type, 3, nodes)
                .addNodes(nullType, 1, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(1), next, nodes.get(2))
                .addSelector(nodes.get(2), next, nodes.get(3))
                .addVariableEdge("null", nodes.get(3))
                .addVariableEdge(varName, nodes.get(varPos))
                .build();

    }
}
