package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Global configuration of state space generation.
 *
 * @author Christoph
 */
public class StateSpaceGenerationSettings {

    private AbortStrategy abortStrategy;

    private CanonicalizationStrategy canonicalizationStrategy;

    private CanonicalizationStrategy aggressiveCanonicalizationStrategy;

    private MaterializationStrategy materializationStrategy;

    private StateLabelingStrategy stateLabelingStrategy;

    private StateRefinementStrategy stateRefinementStrategy;

    public AbortStrategy getAbortStrategy() {

        return abortStrategy;
    }

    public void setAbortStrategy(AbortStrategy abortStrategy) {

        this.abortStrategy = abortStrategy;
    }

    public CanonicalizationStrategy getCanonicalizationStrategy() {

        return canonicalizationStrategy;
    }

    public CanonicalizationStrategy getAggressiveCanonicalizationStrategy() {
        return aggressiveCanonicalizationStrategy;
    }

    public void setAggressiveCanonicalizationStrategy(CanonicalizationStrategy canonicalizationStrategy) {
        aggressiveCanonicalizationStrategy = canonicalizationStrategy;
    }

    public void setCanonicalizationStrategy(CanonicalizationStrategy canonicalizationStrategy) {

        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    public MaterializationStrategy getMaterializationStrategy() {

        return materializationStrategy;
    }

    public void setMaterializationStrategy(MaterializationStrategy materializationStrategy) {

        this.materializationStrategy = materializationStrategy;
    }

    public StateLabelingStrategy getStateLabelingStrategy() {

        return stateLabelingStrategy;
    }

    public void setStateLabelingStrategy(StateLabelingStrategy stateLabelingStrategy) {

        this.stateLabelingStrategy = stateLabelingStrategy;
    }

    public StateRefinementStrategy getStateRefinementStrategy() {

        return stateRefinementStrategy;
    }

    public void setStateRefinementStrategy(StateRefinementStrategy stateRefinementStrategy) {

        this.stateRefinementStrategy = stateRefinementStrategy;
    }
}
