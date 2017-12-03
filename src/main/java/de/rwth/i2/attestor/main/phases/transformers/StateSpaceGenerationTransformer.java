package de.rwth.i2.attestor.main.phases.transformers;

import de.rwth.i2.attestor.stateSpaceGeneration.*;

public interface StateSpaceGenerationTransformer {

    AbortStrategy getAbortStrategy();

    CanonicalizationStrategy getCanonicalizationStrategy();

    CanonicalizationStrategy getAggressiveCanonicalizationStrategy();

    MaterializationStrategy getMaterializationStrategy();

    StateLabelingStrategy getStateLabelingStrategy();

    StateRefinementStrategy getStateRefinementStrategy();
}
