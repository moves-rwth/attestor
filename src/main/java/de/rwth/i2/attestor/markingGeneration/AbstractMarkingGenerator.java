package de.rwth.i2.attestor.markingGeneration;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.MaterializationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.*;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public abstract  class AbstractMarkingGenerator {

    private final Collection<String> availableSelectorLabelNames;
    private final AbortStrategy abortStrategy;
    private final MaterializationStrategy materializationStrategy;
    private final CanonicalizationStrategy canonicalizationStrategy;

    public AbstractMarkingGenerator(Collection<String> availableSelectorLabelNames,
                                    AbortStrategy abortStrategy,
                                    MaterializationStrategy materializationStrategy,
                                    CanonicalizationStrategy canonicalizationStrategy) {

        this.availableSelectorLabelNames = availableSelectorLabelNames;
        this.abortStrategy = abortStrategy;
        this.materializationStrategy = materializationStrategy;
        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    protected abstract List<ProgramState> placeInitialMarkings(ProgramState initialState);
    protected abstract Program getProgram();

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
                .setCanonizationStrategy(canonicalizationStrategy)
                .setStateExplorationStrategy(new DepthFirstStateExplorationStrategy())
                .setMaterializationStrategy(materializationStrategy)
                .setStateCounter(new NoStateCounter())
                .setStateSpaceSupplier(() -> new InternalStateSpace(100000))
                .setStateLabelingStrategy(new NoStateLabelingStrategy())
                .setStateRefinementStrategy(new NoStateRefinementStrategy())
                .setPostProcessingStrategy(new NoPostProcessingStrategy())
                .build();

        try {

            Collection<HeapConfiguration> result = new LinkedHashSet<>();

            StateSpace stateSpace = generator.generate();
            stateSpace.getStates().forEach(
                    state -> result.add(canonicalizationStrategy.canonicalize(state.getHeap()))
            );
            return result;
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Marking generation aborted. This is most likely caused by non-termination.");
        }
    }
}
