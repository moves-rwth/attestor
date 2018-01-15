package de.rwth.i2.attestor.phases.transformers;

import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategyBuilder;

public interface StateLabelingStrategyBuilderTransformer {

    AutomatonStateLabelingStrategyBuilder getStrategy();
}
