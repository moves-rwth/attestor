package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.List;

/**
 * An AnalysisTask comprises everything that is required to generate and export a state space.
 * The actual implementation is dependent on the actual analysis approach.
 *
 * An analysis task is usually constructed and configured through a corresponding implementation
 * of {@link AnalysisTaskBuilder}.
 *
 * @author Christoph
 */
public interface AnalysisTask {

    /**
     * Executes this analysis task to generate a state space.
     *
     * @return The generated state space.
     */
    StateSpace execute();

    /**
     * @return The initial states passed to the state space generation.
     */
    List<HeapConfiguration> getInputs();

    /**
     * @return The strategy that determines when the state space generation is aborted.
     */
    AbortStrategy getAbortStrategy();

    /**
     * @return The strategy employed to materialize program states.
     */
    MaterializationStrategy getMaterializationStrategy();

    /**
     * @return The strategy employed to abstract program states.
     */
    CanonicalizationStrategy getCanonicalizationStrategy();

    /**
     * @return The strategy to discharge whether an abstract program state is subsumed by another state.
     */
    InclusionStrategy getInclusionStrategy();

    /**
     * @return The strategy that assigns atomic propositions to each generated state.
     */
    StateLabelingStrategy getStateLabelingStrategy();

    /**
     * @return
     */
    StateRefinementStrategy getStateRefinementStrategy();

    /**
     * @return Before calling {@link AnalysisTask#execute()} this method returns null.
     *         Otherwise, it returns the state space that has been generated.
     */
    StateSpace getStateSpace();

    /**
     * Exports the full generated state space.
     */
    void exportAllStates();

    /**
     * Export only the final states of the generated state space.
     */
    void exportTerminalStates();
}
