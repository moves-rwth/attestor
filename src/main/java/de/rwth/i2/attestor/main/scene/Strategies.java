package de.rwth.i2.attestor.main.scene;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.AbortStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRectificationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;

public class Strategies {

    private MaterializationStrategy materializationStrategy;
    private CanonicalizationStrategy aggressiveCanonicalizationStrategy;
    private CanonicalizationStrategy canonicalizationStrategy;
    private AbortStrategy abortStrategy;
    private StateLabelingStrategy stateLabelingStrategy;
    private StateRefinementStrategy stateRefinementStrategy;
    private StateRectificationStrategy stateRectificationStrategy;

    private boolean alwaysCanonicalize;

    protected Strategies() {

    }

    public MaterializationStrategy getMaterializationStrategy() {

        return materializationStrategy;
    }

    public void setMaterializationStrategy(MaterializationStrategy materializationStrategy) {

        this.materializationStrategy = materializationStrategy;
    }

    public CanonicalizationStrategy getAggressiveCanonicalizationStrategy() {

        return aggressiveCanonicalizationStrategy;
    }

    public void setAggressiveCanonicalizationStrategy(CanonicalizationStrategy aggressiveCanonicalizationStrategy) {

        this.aggressiveCanonicalizationStrategy = aggressiveCanonicalizationStrategy;
    }

    public AbortStrategy getAbortStrategy() {

        return abortStrategy;
    }

    public void setAbortStrategy(AbortStrategy abortStrategy) {

        this.abortStrategy = abortStrategy;
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

    public CanonicalizationStrategy getCanonicalizationStrategy() {
        return canonicalizationStrategy;
    }

    public void setCanonicalizationStrategy(CanonicalizationStrategy canonicalizationStrategy) {
        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    public StateRectificationStrategy getStateRectificationStrategy() {
        return stateRectificationStrategy;
    }

    public void setStateRectificationStrategy(StateRectificationStrategy stateRectificationStrategy) {
        this.stateRectificationStrategy = stateRectificationStrategy;
    }

    public boolean isAlwaysCanonicalize() {
        return alwaysCanonicalize;
    }

    public void setAlwaysCanonicalize(boolean alwaysCanonicalize) {
        this.alwaysCanonicalize = alwaysCanonicalize;
    }
}
