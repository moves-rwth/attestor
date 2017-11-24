package de.rwth.i2.attestor.stateSpaceGeneration.impl;

import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.AggressiveTerminalStatement;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FinalStateSubsumptionPostProcessingStrategy implements PostProcessingStrategy {

    @Override
    public void process(StateSpaceGenerator stateSpaceGenerator) {

        assert stateSpaceGenerator.getStateSpace().getClass() == InternalStateSpace.class;
        assert Settings.getInstance().options().getAbstractionDistance() == 0;

        InternalStateSpace stateSpace = (InternalStateSpace) stateSpaceGenerator.getStateSpace();

        if(stateSpace.getFinalStateIds().size() == 1) {
            return;
        }

        CanonicalizationStrategy canonicalizationStrategy = stateSpaceGenerator.getCanonizationStrategy();

        Set<ProgramState> finalStates = stateSpace.getFinalStates();
        AggressiveTerminalStatement statement = new AggressiveTerminalStatement();

        Set<ProgramState> fullyAbstractStates = new HashSet<>();
        Map<Integer, Integer> idMap = new HashMap<>();

        for(ProgramState state : finalStates) {
            ProgramState absState = canonicalizationStrategy.canonicalize(statement,state);
            absState.setStateSpaceId(state.getStateSpaceId());
            ProgramState oldState = addIfAbsent(absState, fullyAbstractStates);

            if(oldState != null) {
                idMap.put(state.getStateSpaceId(), oldState.getStateSpaceId());
            } else {
                idMap.put(state.getStateSpaceId(), absState.getStateSpaceId());
            }
        }

        if(fullyAbstractStates.size() < finalStates.size()) {
            stateSpace.updateFinalStates(fullyAbstractStates, idMap);
        }
    }

    private ProgramState addIfAbsent(ProgramState absState, Set<ProgramState> abstractedStates) {

        for(ProgramState state : abstractedStates) {
            if(absState.isSubsumedBy(state)) {
                return state;
            }
        }

        abstractedStates.add(absState);
        return null;
    }
}
