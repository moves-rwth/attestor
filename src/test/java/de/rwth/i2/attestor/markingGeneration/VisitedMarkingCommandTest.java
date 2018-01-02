package de.rwth.i2.attestor.markingGeneration;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.materialization.ViolationPoints;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class VisitedMarkingCommandTest {

    private static final int NEXT_PC = 23;

    private SceneObject sceneObject;
    private Type type;
    private SelectorLabel selA;
    private SelectorLabel selB;


    private Collection<String> availableSelectorNames;
    private VisitedMarkingCommand command;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        type = sceneObject.scene().getType("type");
        selA = sceneObject.scene().getSelectorLabel("selA");
        selB = sceneObject.scene().getSelectorLabel("selB");

        availableSelectorNames = new LinkedHashSet<>();
        availableSelectorNames.add("selA");
        availableSelectorNames.add("selB");

        command = new VisitedMarkingCommand(availableSelectorNames, NEXT_PC);

    }

    @Test
    public void testPotentialViolationPoints() {

        ViolationPoints violationPoints = command.getPotentialViolationPoints();

        assertEquals(1, violationPoints.getVariables().size());
        String variableName = violationPoints.getVariables().iterator().next();
        assertEquals(VisitedMarkingCommand.MARKING_NAME, variableName);
        assertEquals(availableSelectorNames, violationPoints.getSelectorsOf(variableName));
    }

    @Test
    public void testNeedsCanonicalization() {

        assertTrue(command.needsCanonicalization());
    }

    @Test
    public void testSuccessorPCs() {

        assertEquals(Collections.singleton(NEXT_PC), command.getSuccessorPCs());
    }

    @Test
    public void testComputeSuccessors() {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration hc = sceneObject.scene()
                .createHeapConfiguration()
                .builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), selA, nodes.get(1))
                .addSelector(nodes.get(0), selB, nodes.get(2))
                .addVariableEdge(VisitedMarkingCommand.MARKING_NAME, nodes.get(0))
                .build();

        ProgramState initialState = sceneObject.scene().createProgramState(hc);

        ProgramState expectedA = initialState.clone();
        expectedA.removeVariable(VisitedMarkingCommand.MARKING_NAME);
        expectedA.getHeap()
                .builder()
                .addVariableEdge(VisitedMarkingCommand.MARKING_NAME, nodes.get(1))
                .build();

        ProgramState expectedB = initialState.clone();
        expectedB.removeVariable(VisitedMarkingCommand.MARKING_NAME);
        expectedB.getHeap()
                .builder()
                .addVariableEdge(VisitedMarkingCommand.MARKING_NAME, nodes.get(2))
                .build();

        Collection<ProgramState> successorStates = command.computeSuccessors(initialState);
        assertEquals(2, successorStates.size());
        assertTrue(successorStates.contains(expectedA));
        assertTrue(successorStates.contains(expectedB));
    }

    @Test
    public void testNoMarkingPlaced() {

        ProgramState initialState = sceneObject.scene().createProgramState(
                sceneObject.scene().createHeapConfiguration()
        );

        try {
            command.computeSuccessors(initialState);
            fail("No marking has been placed on the initial heap");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testUnknownSelector() {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration hc = sceneObject.scene()
                .createHeapConfiguration()
                .builder()
                .addNodes(type, 2, nodes)
                .addSelector(nodes.get(0), sceneObject.scene().getSelectorLabel("BADSELECTOR"), nodes.get(1))
                .addVariableEdge(VisitedMarkingCommand.MARKING_NAME, nodes.get(0))
                .build();

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        try {
            command.computeSuccessors(initialState);
            fail("Encountered unknown selector label");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testIgnoreConstants() {

        ProgramState initialState = sceneObject.scene().createProgramState(
                sceneObject.scene().createHeapConfiguration()
        );
        TIntArrayList nodes = new TIntArrayList();
        initialState.getHeap()
                .builder()
                .addNodes(type, 1, nodes)
                .addSelector(nodes.get(0), selA, initialState.getHeap().variableTargetOf(Constants.NULL))
                .addVariableEdge(VisitedMarkingCommand.MARKING_NAME, nodes.get(0))
                .build();

        Collection<ProgramState> successorStates = command.computeSuccessors(initialState);
        assertTrue("Constant nodes should never be marked", successorStates.isEmpty());
    }
}
