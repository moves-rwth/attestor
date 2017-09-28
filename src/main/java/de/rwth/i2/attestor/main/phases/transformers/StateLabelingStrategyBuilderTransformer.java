package de.rwth.i2.attestor.main.phases.transformers;

import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategyBuilder;

public interface StateLabelingStrategyBuilderTransformer {

    AutomatonStateLabelingStrategyBuilder getStrategy();
}
