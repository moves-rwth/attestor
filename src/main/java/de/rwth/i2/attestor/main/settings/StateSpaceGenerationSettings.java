package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.stateSpaceGeneration.*;

/**
 * Global configuration of state space generation.
 *
 * @author Christoph
 */
public class StateSpaceGenerationSettings {

    private AbortStrategy abortStrategy = null;

    private CanonicalizationStrategy canonicalizationStrategy = null;

    private InclusionStrategy inclusionStrategy = null;

    private MaterializationStrategy materializationStrategy = null;

    private StateLabelingStrategy stateLabelingStrategy = null;

    private StateRefinementStrategy stateRefinementStrategy = null;

    public AbortStrategy getAbortStrategy() {

        return abortStrategy;
    }

    public void setAbortStrategy(AbortStrategy abortStrategy) {

        this.abortStrategy = abortStrategy;
    }

    public CanonicalizationStrategy getCanonicalizationStrategy() {

        return canonicalizationStrategy;
    }

    public void setCanonicalizationStrategy(CanonicalizationStrategy canonicalizationStrategy) {

        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    public InclusionStrategy getInclusionStrategy() {

        return inclusionStrategy;
    }

    public void setInclusionStrategy(InclusionStrategy inclusionStrategy) {

        this.inclusionStrategy = inclusionStrategy;
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
