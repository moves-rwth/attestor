package de.rwth.i2.attestor.markingGeneration;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.*;

import java.util.Collection;
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

    public Collection<ProgramState> marked(ProgramState initialState) {

        List<ProgramState> initialStates = placeInitialMarkings(initialState);
        Program program = getProgram();


        StateSpaceGenerator generator = StateSpaceGenerator.builder()
                .setProgram(program)
                .addInitialStates(initialStates)
                .setAbortStrategy(abortStrategy)
                .setCanonizationStrategy(canonicalizationStrategy)
                .setExplorationStrategy(new ExploreAllStrategy())
                .setMaterializationStrategy(materializationStrategy)
                .setStateCounter(new NoStateCounter())
                .setStateLabelingStrategy(new NoStateLabelingStrategy())
                .setStateRefinementStrategy(new NoStateRefinementStrategy())
                .setBreadthFirstSearchEnabled(false)
                .setPostProcessingStrategy(new NoPostProcessingStrategy())
                .build();

        try {
            return generator.generate().getStates();
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Marking generation aborted. This is most likely caused by nontermination.");
        }
    }
}
