package de.rwth.i2.attestor.stateSpaceGeneration.impl;

import de.rwth.i2.attestor.grammar.languageInclusion.LanguageInclusionStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class FinalStateSubsumptionPostProcessingStrategy implements PostProcessingStrategy {

    private CanonicalizationStrategy canonicalizationStrategy;
    private LanguageInclusionStrategy languageInclusionStrategy;
    private int minAbstractionDistance;

    public FinalStateSubsumptionPostProcessingStrategy(CanonicalizationStrategy canonicalizationStrategy,
                                                       LanguageInclusionStrategy languageInclusionStrategy,
                                                       int minAbstractionDistance) {

        this.canonicalizationStrategy = canonicalizationStrategy;
        this.languageInclusionStrategy = languageInclusionStrategy;
        this.minAbstractionDistance = minAbstractionDistance;
    }

    @Override
    public void process(StateSpace originalStateSpace) {

        assert originalStateSpace.getClass() == InternalStateSpace.class;

        if (minAbstractionDistance == 0) {
            return;
        }

        InternalStateSpace stateSpace = (InternalStateSpace) originalStateSpace;

        if (stateSpace.getFinalStateIds().size() == 1) {
            return;
        }

        Set<ProgramState> finalStates = stateSpace.getFinalStates();

        Set<ProgramState> fullyAbstractStates = new LinkedHashSet<>();
        Map<Integer, Integer> idMap = new LinkedHashMap<>();

        for (ProgramState state : finalStates) {
            ProgramState absState = state.shallowCopyWithUpdateHeap(canonicalizationStrategy.canonicalize(state.getHeap()));
            absState.setStateSpaceId(state.getStateSpaceId());
            ProgramState oldState = addIfAbsent(absState, fullyAbstractStates);

            if (oldState != null) {
                idMap.put(state.getStateSpaceId(), oldState.getStateSpaceId());
            } else {
                idMap.put(state.getStateSpaceId(), absState.getStateSpaceId());
            }
        }

        if (fullyAbstractStates.size() < finalStates.size()) {
            stateSpace.updateFinalStates(fullyAbstractStates, idMap);
        }
    }

    private ProgramState addIfAbsent(ProgramState absState, Set<ProgramState> abstractedStates) {

        for (ProgramState state : abstractedStates) {
            if (absState.isSubsumedBy(state)) {
                return state;
            }
        }

        abstractedStates.add(absState);
        return null;
    }
}
