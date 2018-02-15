package de.rwth.i2.attestor.markingGeneration.neighbourhood;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class NeighbourhoodMarkingCommandTest {

    private static final int NEXT_PC = 23;

    private SceneObject sceneObject;
    private Type type;
    private SelectorLabel selA;
    private SelectorLabel selB;

    private String initialMarkingName;
    private String markingName;
    private String markingSeparator;
    private Collection<String> availableSelectorNames;

    private NeighbourhoodMarkingCommand command;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        type = sceneObject.scene().getType("type");
        selA = sceneObject.scene().getSelectorLabel("selA");
        selB = sceneObject.scene().getSelectorLabel("selB");
        initialMarkingName = NeighbourhoodMarkingCommand.INITIAL_MARKING_NAME;
        markingName = NeighbourhoodMarkingCommand.MARKING_NAME;
        markingSeparator = "-";

        availableSelectorNames = new LinkedHashSet<>();
        availableSelectorNames.add("selA");
        availableSelectorNames.add("selB");

        command = new NeighbourhoodMarkingCommand(NEXT_PC, availableSelectorNames);
    }

    @Test
    public void testPotentialViolationPoints() {

        ViolationPoints violationPoints = command.getPotentialViolationPoints();
        Set<String> variables = violationPoints.getVariables();

        Set<String> expectedVariables = new LinkedHashSet<>();
        expectedVariables.add(markingName);
        expectedVariables.add(initialMarkingName);
        expectedVariables.add(markingName + markingSeparator + selA);
        expectedVariables.add(markingName + markingSeparator + selB);

        assertEquals(expectedVariables, variables);
    }

    @Test
    public void testCanonicalization() {

        assertTrue(command.needsCanonicalization());
    }

    @Test
    public void testSuccessorPCs() {

        assertEquals(Collections.singleton(NEXT_PC), command.getSuccessorPCs());
    }

    @Test
    public void testSimpleComputeSuccessors() {

        ProgramState baseState = sceneObject.scene().createProgramState();
        TIntArrayList nodes = new TIntArrayList();
        baseState.getHeap()
                .builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), selA, nodes.get(1))
                .addSelector(nodes.get(0), selB, nodes.get(2))
                .build();

        ProgramState inputState = baseState.clone();
        inputState.getHeap()
                .builder()
                .addVariableEdge(markingName, nodes.get(0))
                .addVariableEdge(markingName+markingSeparator+selA.getLabel(), nodes.get(1))
                .addVariableEdge(markingName+markingSeparator+selB.getLabel(), nodes.get(2))
                .build();

        Set<ProgramState> expected = new LinkedHashSet<>();

        ProgramState firstExpected = baseState.clone();
        firstExpected.getHeap()
                .builder()
                .addVariableEdge(markingName, nodes.get(1))
                .build();
        expected.add(firstExpected);

        ProgramState secondExpected = baseState.clone();
        secondExpected.getHeap()
                .builder()
                .addVariableEdge(markingName, nodes.get(2))
                .build();
        expected.add(secondExpected);

        Collection<ProgramState> resultStates = command.computeSuccessors(inputState);

        assertEquals(expected, resultStates);
    }

    @Test
    public void testFromInitialMarking() {

        ProgramState baseState = sceneObject.scene().createProgramState();
        TIntArrayList nodes = new TIntArrayList();
        baseState.getHeap()
                .builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), selA, nodes.get(1))
                .addSelector(nodes.get(0), selB, nodes.get(2))
                .build();

        ProgramState inputState = baseState.clone();
        inputState.getHeap()
                .builder()
                .addVariableEdge(initialMarkingName, nodes.get(0))
                .build();

        Set<ProgramState> expected = new LinkedHashSet<>();

        ProgramState firstExpected = baseState.clone();
        firstExpected.getHeap()
                .builder()
                .addVariableEdge(markingName, nodes.get(0))
                .addVariableEdge(markingName+markingSeparator+selA.getLabel(), nodes.get(1))
                .addVariableEdge(markingName+markingSeparator+selB.getLabel(), nodes.get(2))
                .build();
        expected.add(firstExpected);

        Collection<ProgramState> resultStates = command.computeSuccessors(inputState);

        assertEquals(expected, resultStates);

    }
}
