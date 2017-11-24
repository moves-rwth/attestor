package de.rwth.i2.attestor.stateSpaceGeneration.impl;

import de.rwth.i2.attestor.semantics.AggressiveTerminalStatement;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AggressivePostProcessingStrategy implements PostProcessingStrategy {

    @Override
    public void process(StateSpaceGenerator stateSpaceGenerator) {

        assert stateSpaceGenerator.getStateSpace().getClass() == InternalStateSpace.class;

        InternalStateSpace stateSpace = (InternalStateSpace) stateSpaceGenerator.getStateSpace();
        CanonicalizationStrategy canonicalizationStrategy = stateSpaceGenerator.getCanonizationStrategy();

        Set<ProgramState> finalStates = stateSpace.getFinalStates();
        AggressiveTerminalStatement statement = new AggressiveTerminalStatement();

        Map<ProgramState,ProgramState> abstractedStates = new HashMap<>();
        Map<Integer, Integer> idMap = new HashMap<>();

        for(ProgramState state : finalStates) {
            ProgramState absState = canonicalizationStrategy.canonicalize(statement,state);
            absState.setStateSpaceId(state.getStateSpaceId());
            ProgramState oldState = abstractedStates.put(absState,absState);
            if(oldState != null) {
                idMap.put(state.getStateSpaceId(), oldState.getStateSpaceId());
            } else {
                idMap.put(state.getStateSpaceId(), absState.getStateSpaceId());
            }
        }

        if(abstractedStates.size() < finalStates.size()) {
            stateSpace.updateFinalStates(abstractedStates.keySet(), idMap);
        }
    }
}
