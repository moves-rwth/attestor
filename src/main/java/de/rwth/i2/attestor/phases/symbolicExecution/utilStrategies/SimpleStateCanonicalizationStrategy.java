package de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateCanonicalizationStrategy;

import java.util.Collection;
import java.util.Collections;

public class SimpleStateCanonicalizationStrategy implements StateCanonicalizationStrategy {

    private final CanonicalizationStrategy canonicalizationStrategy;

    public SimpleStateCanonicalizationStrategy(CanonicalizationStrategy canonicalizationStrategy) {

        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    @Override
    public Collection<ProgramState> canonicalize(ProgramState state) {

        return Collections.singleton(
                state.shallowCopyWithUpdateHeap(
                        canonicalizationStrategy.canonicalize(
                                state.getHeap()
                        )
                )
        );
    }
}
