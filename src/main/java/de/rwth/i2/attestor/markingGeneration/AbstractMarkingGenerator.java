package de.rwth.i2.attestor.markingGeneration;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.*;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract  class AbstractMarkingGenerator {

    protected final Collection<String> availableSelectorLabelNames;
    protected final AbortStrategy abortStrategy;
    protected final MaterializationStrategy materializationStrategy;
    protected final CanonicalizationStrategy canonicalizationStrategy;
    protected final CanonicalizationStrategy aggressiveCanonicalizationStrategy;
    protected final StateRectificationStrategy stateRectificationStrategy;

    public AbstractMarkingGenerator(Collection<String> availableSelectorLabelNames,
                                    AbortStrategy abortStrategy,
                                    MaterializationStrategy materializationStrategy,
                                    CanonicalizationStrategy canonicalizationStrategy,
                                    CanonicalizationStrategy aggressiveCanonicalizationStrategy,
                                    StateRectificationStrategy stateRectificationStrategy) {

        this.availableSelectorLabelNames = availableSelectorLabelNames;
        this.abortStrategy = abortStrategy;
        this.materializationStrategy = materializationStrategy;
        this.canonicalizationStrategy = canonicalizationStrategy;
        this.aggressiveCanonicalizationStrategy = aggressiveCanonicalizationStrategy;
        this.stateRectificationStrategy = stateRectificationStrategy;
    }

    protected abstract List<ProgramState> placeInitialMarkings(ProgramState initialState);
    protected abstract Program getProgram();
    protected abstract Collection<HeapConfiguration> getResultingHeaps(StateSpace stateSpace);

    protected Collection<String> getAvailableSelectorLabelNames() {

        return availableSelectorLabelNames;
    }

    public Collection<HeapConfiguration> marked(ProgramState initialState) {

        List<ProgramState> initialStates = placeInitialMarkings(initialState);

        if(initialStates.isEmpty()) {
            return new ArrayList<>();
        }

        Program program = getProgram();


        StateSpaceGenerator generator = StateSpaceGenerator.builder()
                .setProgram(program)
                .addInitialStates(initialStates)
                .setAbortStrategy(abortStrategy)
                .setCanonizationStrategy(
                        new StateCanonicalizationStrategy(
                                canonicalizationStrategy
                        )
                )
                .setStateExplorationStrategy(new DepthFirstStateExplorationStrategy())
                .setMaterializationStrategy(materializationStrategy)
                .setStateRectificationStrategy(stateRectificationStrategy)
                .setStateCounter(new NoStateCounter())
                .setStateSpaceSupplier(() -> new InternalStateSpace(100000))
                .setStateLabelingStrategy(new NoStateLabelingStrategy())
                .setStateRefinementStrategy(new NoStateRefinementStrategy())
                .setPostProcessingStrategy(new NoPostProcessingStrategy())
                .setFinalStateStrategy(new TerminalStatementFinalStateStrategy())
                .build();

        try {
            StateSpace stateSpace = generator.generate();
            return getResultingHeaps(stateSpace);
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Marking generation aborted. This is most likely caused by non-termination.");
        }
    }
}
