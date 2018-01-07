package de.rwth.i2.attestor.stateSpaceGeneration.impl;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

/**
 * The options passed to every Semantics object by a state space generator to configure the symbolic execution.
 *
 * @author Christoph
 */
public class DefaultSymbolicExecutionObserver implements SymbolicExecutionObserver {

    /**
     * The state space generator that calls Semantics objects during the symbolic execution.
     */
    private final StateSpaceGenerator stateSpaceGenerator;

    public DefaultSymbolicExecutionObserver(StateSpaceGenerator stateSpaceGenerator) {

        this.stateSpaceGenerator = stateSpaceGenerator;
    }

    @Override
    public void update(Object handler, ProgramState input) {

    }

    @Override
    public StateSpace generateStateSpace(Program program, ProgramState input)
            throws StateSpaceGenerationAbortedException {

        HeapConfiguration heap = input.getHeap();

        ProgramState initialState = new DefaultProgramState(heap); // TODO  stateSpaceGenerator.scene().createProgramState(heap);
        return StateSpaceGenerator.builder(stateSpaceGenerator)
                .setProgram(program)
                .addInitialState(initialState)
                .build()
                .generate();
    }
    
    @Override
    public StateSpace continueStateSpace( StateSpace stateSpace, Program program, ProgramState continuationPoint ) 
    		throws StateSpaceGenerationAbortedException{
       
        return StateSpaceGenerator.builder(stateSpaceGenerator)
                .setInitialStateSpace(stateSpace)
                .addInitialState(continuationPoint)
                .setProgram(program)
                .build()
                .generate();
    }

    @Override
    public boolean isDeadVariableEliminationEnabled() {

        return true; // TODO stateSpaceGenerator.isDeadVariableEliminationEnabled();
    }
}
