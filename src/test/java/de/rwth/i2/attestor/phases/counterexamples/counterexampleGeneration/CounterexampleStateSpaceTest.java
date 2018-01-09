package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

import static junit.framework.TestCase.*;

public class CounterexampleStateSpaceTest {

    private SceneObject sceneObject;
    private MockupProgramStateFactory programStateFactory;
    private CounterexampleStateSpace stateSpace;
    private Predicate<ProgramState> requiredFinalStatesPredicate;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        programStateFactory = new MockupProgramStateFactory(sceneObject);
        requiredFinalStatesPredicate = new MockupFinalStatePredicate(
                Collections.singleton(programStateFactory.getRequiredFinalState())
        );
        stateSpace = new CounterexampleStateSpace(requiredFinalStatesPredicate);
    }

    @Test
    public void testAddStates() {

        assertEquals(0, stateSpace.size());
        assertEquals(0, stateSpace.getStates().size());

        ProgramState initialState = programStateFactory.getInitialState();
        stateSpace.addInitialState(initialState);

        assertEquals(1, stateSpace.size());
        assertEquals(1, stateSpace.getInitialStates().size());
        assertEquals(0, stateSpace.getFinalStates().size());
        assertEquals(1, stateSpace.getStates().size());
        assertEquals(initialState, stateSpace.getStates().iterator().next());
        assertEquals(1, stateSpace.getInitialStateIds().size());
        assertEquals(0, stateSpace.getInitialStateIds().iterator().next());

        try {
           stateSpace.addInitialState(programStateFactory.getNormalState());
           fail("At most one initial state is permitted");
        } catch(Exception e) {
            //expected: at most one intial state is permitted
        }

        boolean addStateResult = stateSpace.addState(programStateFactory.getNormalState());
        assertTrue(addStateResult);
        assertEquals(1, stateSpace.size());
        assertEquals(1, stateSpace.getInitialStates().size());
        assertEquals(0, stateSpace.getFinalStates().size());
        assertEquals(1, stateSpace.getStates().size());
        assertEquals(initialState, stateSpace.getStates().iterator().next());

        addStateResult = stateSpace.addStateIfAbsent(programStateFactory.getNormalState());
        assertTrue(addStateResult);
        assertEquals(1, stateSpace.size());

        ProgramState requiredFinalState = programStateFactory.getRequiredFinalState();
        stateSpace.setFinal(requiredFinalState);
        assertEquals(2, stateSpace.size());
        assertEquals(1, stateSpace.getInitialStates().size());
        assertEquals(1, stateSpace.getFinalStates().size());
        assertEquals(2, stateSpace.getStates().size());
        Iterator<ProgramState> iterator = stateSpace.getStates().iterator();
        assertEquals(initialState, iterator.next());
        assertEquals(requiredFinalState, iterator.next());
        assertEquals(1, stateSpace.getFinalStateIds().size());
        assertEquals(1, stateSpace.getFinalStateIds().iterator().next());
        assertEquals(2, stateSpace.getMaximalStateSize());

        ProgramState irrlevantFinalState = programStateFactory.getIrrelevantFinalState();
        stateSpace.setFinal(irrlevantFinalState);
        assertEquals(2, stateSpace.size());
        assertEquals(2, stateSpace.getStates().size());
        assertEquals(1, stateSpace.getInitialStates().size());
        assertEquals(1, stateSpace.getFinalStates().size());

        assertEquals(initialState, stateSpace.getState(0));
        assertEquals(requiredFinalState, stateSpace.getState(1));

        try {
            stateSpace.getState(23);
            fail("Illegal ID should trigger an exception.");
        } catch (IllegalArgumentException e) {
            // expected
        }

    }

    @Test
    public void testSuccessors() {

        ProgramState initialState = programStateFactory.getInitialState();
        ProgramState finalState = programStateFactory.getRequiredFinalState();

        stateSpace.addInitialState(initialState);
        stateSpace.setFinal(finalState);

        TIntArrayList initialSuccessors = new TIntArrayList();
        TIntArrayList finalSuccessors = new TIntArrayList();
        finalSuccessors.add(1);

        assertEquals(initialSuccessors, stateSpace.getArtificialInfPathsSuccessorsIdsOf(0));
        assertEquals(finalSuccessors, stateSpace.getArtificialInfPathsSuccessorsIdsOf(1));

        assertEquals(
                Collections.emptySet(),
                stateSpace.getArtificialInfPathsSuccessorsOf(initialState)
        );

        assertEquals(
                Collections.singleton(finalState),
                stateSpace.getArtificialInfPathsSuccessorsOf(finalState)
        );

        assertEquals(
                new TIntArrayList(),
                stateSpace.getMaterializationSuccessorsIdsOf(0)
        );

        assertEquals(
                Collections.emptySet(),
                stateSpace.getMaterializationSuccessorsOf(initialState)
        );

        assertEquals(
                Collections.singleton(finalState),
                stateSpace.getControlFlowSuccessorsOf(initialState)
        );

        assertEquals(
                Collections.emptySet(),
                stateSpace.getControlFlowSuccessorsOf(finalState)
        );

        assertEquals(
                finalSuccessors,
                stateSpace.getControlFlowSuccessorsIdsOf(0)
        );

        assertEquals(
                initialSuccessors,
                stateSpace.getControlFlowSuccessorsIdsOf(1)
        );
    }

    @Test
    public void testAPs() {

        String satisfiedAP = "testAP";
        String violatedAP = "violatedAP";
        ProgramState initialState = programStateFactory.getInitialState();
        initialState.addAP(satisfiedAP);

        stateSpace.addInitialState(initialState);
        assertTrue(stateSpace.satisfiesAP(0, satisfiedAP));
        assertFalse(stateSpace.satisfiesAP(0, violatedAP));
    }

    @Test
    public void testInit() {

        try {
            new CounterexampleStateSpace(null);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }

    }
}
