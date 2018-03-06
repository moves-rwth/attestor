package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupCanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TraceBasedStateExplorationStrategyTest {

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

        CounterexampleTrace trace = new CounterexampleTrace() {

            @Override
            public ProgramState getInitialState() {
                return null;
            }

            @Override
            public ProgramState getFinalState() {
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


        StateSubsumptionStrategy equalityStrategy = new StateSubsumptionStrategy(new MockupCanonicalizationStrategy());
        TraceBasedStateExplorationStrategy strategy = new TraceBasedStateExplorationStrategy(trace, equalityStrategy);


        strategy.addUnexploredState(trivialState, false);
        assertFalse(strategy.hasUnexploredStates());
    }

    @Test
    public void testSequence() {

        CounterexampleTrace trace = getTrace();

        StateSubsumptionStrategy equalityStrategy = new StateSubsumptionStrategy(new MockupCanonicalizationStrategy());
        TraceBasedStateExplorationStrategy strategy = new TraceBasedStateExplorationStrategy(trace, equalityStrategy);

        ProgramState state = trivialState.clone();
        state.setProgramCounter(0);

        ProgramState finalState = trivialState.clone();
        finalState.setProgramCounter(23);

        strategy.addUnexploredState(state, false);
        assertTrue(strategy.hasUnexploredStates());
        strategy.getNextUnexploredState();

        strategy.addUnexploredState(state, false);
        assertFalse(strategy.hasUnexploredStates());

        strategy.addUnexploredState(finalState, false);
        assertTrue(strategy.hasUnexploredStates());
    }

    private CounterexampleTrace getTrace() {
        return new CounterexampleTrace() {

            @Override
            public ProgramState getInitialState() {
                return programStates.get(0);
            }

            @Override
            public ProgramState getFinalState() {
                return programStates.get(1);
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

}
