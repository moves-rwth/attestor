package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TraceBasedExplorationStrategyTest {

    private SceneObject sceneObject;
    private ProgramState trivialState;

    @Before
    public void setUp() {

        this.sceneObject = new MockupSceneObject();

        HeapConfiguration emptyHc = sceneObject.scene().createHeapConfiguration();
        trivialState = sceneObject.scene().createProgramState(emptyHc);
    }

    @Test
    public void testEmptyTrace() {

        Trace trace = new Trace() {

            @Override
            public ProgramState getInitialState() {
                return null;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public ProgramState next() {
                return null;
            }
        };


        StateSubsumptionStrategy equalityStrategy = (subsumed, subsuming) -> subsumed.equals(subsuming);
        TraceBasedExplorationStrategy strategy = new TraceBasedExplorationStrategy(trace, equalityStrategy);
        assertFalse(strategy.check(trivialState, null));
    }

    @Test
    public void testSequence() {

        Trace trace = getTrace();

        StateSubsumptionStrategy equalityStrategy = (subsumed, subsuming) -> subsumed.equals(subsuming);
        TraceBasedExplorationStrategy strategy = new TraceBasedExplorationStrategy(trace, equalityStrategy);

        ProgramState state = trivialState.clone();
        state.setProgramCounter(0);

        ProgramState finalState = trivialState.clone();
        finalState.setProgramCounter(23);

        assertTrue(strategy.check(state, null));
        assertFalse(strategy.check(state, null));
        assertTrue(strategy.check(finalState, null));
    }

    private Trace getTrace() {
        return new Trace() {

            @Override
            public ProgramState getInitialState() {
                return programStates.get(0);
            }

            List<ProgramState> programStates = new ArrayList<>();
                int position = 0;

                {
                    ProgramState state = trivialState.clone();
                    state.setProgramCounter(0);
                    programStates.add(state);

                    state = trivialState.clone();
                    state.setProgramCounter(23);
                    programStates.add(state);
                }

                @Override
                public boolean hasNext() {
                    return position < programStates.size();
                }

                @Override
                public ProgramState next() {
                    ProgramState result = programStates.get(position);
                    ++position;
                    return result;
                }
            };
    }

    @Test
    public void testInclusion() {

        Trace trace = getTrace();

        StateSubsumptionStrategy subsumptionStrategy =
                (subsumed, subsuming) ->
                        subsumed.equals(subsuming)
                        || (
                        subsumed.getProgramCounter() > 0 &&
                        subsumed.getProgramCounter() <= subsuming.getProgramCounter()
                        );

        TraceBasedExplorationStrategy strategy = new TraceBasedExplorationStrategy(trace, subsumptionStrategy);

        ProgramState state = trivialState.clone();
        state.setProgramCounter(0);

        ProgramState finalState = trivialState.clone();
        finalState.setProgramCounter(19);

        assertTrue(strategy.check(state, null));
        assertFalse(strategy.check(state, null));
        assertTrue(strategy.check(finalState, null));
    }
}
