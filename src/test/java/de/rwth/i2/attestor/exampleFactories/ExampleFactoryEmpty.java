package de.rwth.i2.attestor.exampleFactories;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.MockupSymbolicExecutionObserver;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
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
        return s -> {};
    }

    public AbortStrategy getAbort() {
        return s -> {};
    }

    public SemanticsObserverSupplier getSemanticsOptionsSupplier() {
        return s -> new MockupSymbolicExecutionObserver();
    }

    public StateSpaceSupplier getStateSpaceSupplier() {
        return () -> new InternalStateSpace(100);
    }

}