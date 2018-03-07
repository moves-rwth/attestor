package de.rwth.i2.attestor.exampleFactories;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoCanonicalizationStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoRectificationStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoStateRefinementStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.Collections;

public class ExampleFactoryEmpty {

    public StateRefinementStrategy getStateRefinement() {

        return new NoStateRefinementStrategy();
    }

    public MaterializationStrategy getMaterialization() {

        return (state, vio) -> Collections.emptyList();
    }

    public CanonicalizationStrategy getCanonicalization() {

        return new NoCanonicalizationStrategy();
    }

    public StateRectificationStrategy getRectification() {
        return new NoRectificationStrategy();
    }

    public StateLabelingStrategy getStateLabeling() {

        return s -> {
        };
    }

    public AbortStrategy getAbort() {

        return s -> {
        };
    }

    public StateSpaceSupplier getStateSpaceSupplier() {

        return () -> new InternalStateSpace(100);
    }

}
