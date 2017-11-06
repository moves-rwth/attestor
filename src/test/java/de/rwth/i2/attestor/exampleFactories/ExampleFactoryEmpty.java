package de.rwth.i2.attestor.exampleFactories;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.MockupSemanticsOptions;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.Collections;

public class ExampleFactoryEmpty {

    public StateRefinementStrategy getStateRefinement() {
        return s -> s;
    }

    public MaterializationStrategy getMaterialization() {
        return (state, vio) -> Collections.emptyList();
    }

    public CanonicalizationStrategy getCanonicalization() {
        return (sem, state) -> state;
    }

    public StateLabelingStrategy getStateLabeling() {
        return s -> {};
    }

    public AbortStrategy getAbort() {
        return s -> {};
    }

    public SemanticsOptionsSupplier getSemanticsOptionsSupplier() {
        return s -> new MockupSemanticsOptions();
    }

    public StateSpaceSupplier getStateSpaceSupplier() {
        return () -> new InternalStateSpace(100);
    }

}
