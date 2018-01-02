package de.rwth.i2.attestor.exampleFactories;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.AbortStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceSupplier;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.InternalStateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoCanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoStateRefinementStrategy;

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
