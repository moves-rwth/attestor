package de.rwth.i2.attestor.main;

import java.io.FileNotFoundException;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

/**
 * A builder class to comfortably create and customize a program analysis task from files or already created objects.
 *
 * @author Christoph
 */
public interface AnalysisTaskBuilder {

    /**
     * Checks validity of the specified analysis task.
     * If the specification is sufficient to execute the task, it creates the task.
     * This builder is not usable afterwards anymore.
     *
     * @return The AnalysisTask derived from the specification contained in the builder.
     */
    AnalysisTask build();

    /**
     * Load the initial state of the program analysis from a file.
     * @param filename The file containing the initial state.
     * @return The builder.
     * @throws FileNotFoundException Thrown if the provided filename could not be found.
     */
    AnalysisTaskBuilder loadInput(String filename) throws FileNotFoundException;

    /**
     * Determines the initial state of the program analysis.
     * @param heapConfiguration The initial state.
     * @return The builder.
     */
    AnalysisTaskBuilder setInput(HeapConfiguration heapConfiguration);

    /**
     * Load a program together with the initial method to analyze from a file.
     * @param classpath The classpath leading to the file.
     * @param filename The name of the source code file.
     * @param entryPoint The name of the method from which the analysis should be started.
     * @return The builder.
     */
    AnalysisTaskBuilder loadProgram(String classpath, String filename, String entryPoint);

    /**
     * Determines the program that should be analyzed by the analysis.
     * @param program The program to analyze.
     * @return The builder.
     */
    AnalysisTaskBuilder setProgram(Program program);

    /**
     * Sets the initial scope depth of the analysis.
     * @param depth The scope depth (=&#62; 0).
     * @return The builder.
     */
    AnalysisTaskBuilder setScopeDepth(int depth);

    /**
     * Sets the strategy that determines when the analysis should be aborted.
     * @param abortStrategy The strategy.
     * @return The builder.
     */
    AnalysisTaskBuilder setAbortStrategy(AbortStrategy abortStrategy);

    /**
     * Sets the strategy that determines how to materialize states.
     * @param materializationStrategy The strategy.
     * @return The builder.
     */
    AnalysisTaskBuilder setMaterializationStrategy(MaterializationStrategy materializationStrategy);

    /**
     * Sets the strategy that determines how states are abstracted.
     * @param canonicalizationStrategy The strategy.
     * @return The builder.
     */
    AnalysisTaskBuilder setCanonicalizationStrategy(CanonicalizationStrategy canonicalizationStrategy);

    /**
     * Sets the strategy that determines how to discharge whether a state subsumes another one.
     * @param inclusionStrategy The strategy.
     * @return The builder.
     */
    AnalysisTaskBuilder setInclusionStrategy(InclusionStrategy inclusionStrategy);


    /**
     * Sets the strategy that determines how atomic propositions are assigned to states.
     * @param stateLabelingStrategy The strategy.
     * @return The builder.
     */
    AnalysisTaskBuilder setStateLabelingStrategy(StateLabelingStrategy stateLabelingStrategy);

}
