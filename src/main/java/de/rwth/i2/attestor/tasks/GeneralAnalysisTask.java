package de.rwth.i2.attestor.tasks;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AnalysisTask;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of most common functionality of analysis tasks.
 * The complete customization of an analysis task determining, for example how state spaces are exported,
 * has to be done by subclasses.
 *
 * @author  Christoph
 */
public abstract class GeneralAnalysisTask implements AnalysisTask {

    /**
     * The state space generator underlying this analysis task.
     * This object contains all strategies to customize the state
     * space generation.
     */
    private StateSpaceGenerator stateSpaceGenerator;

    /**
     * The state space generated after execution of this analysis task.
     */
    protected StateSpace stateSpace;

    /**
     * Initializes this analysis task.
     * @param stateSpaceGenerator A ready-to-execute state space generator.
     *                            The actual customization of the generator has to be performed
     *                            by subclasses.
     */
    protected GeneralAnalysisTask(StateSpaceGenerator stateSpaceGenerator) {

        if(stateSpaceGenerator == null) {
            throw new NullPointerException();
        }

        this.stateSpaceGenerator = stateSpaceGenerator;
        this.stateSpace = null;
    }

    @Override
    public StateSpace execute() {

        if(stateSpace == null) {
            stateSpace = stateSpaceGenerator.generate();
            Settings.getInstance().factory().addGeneratedStates(stateSpace.getStates().size());
        } else {
            throw new IllegalStateException("StateSpace has already been generated.");
        }

       return stateSpace;
    }

    @Override
    public List<HeapConfiguration> getInputs() {

        List<ProgramState> states = stateSpaceGenerator.getInitialStates();
        List<HeapConfiguration> res = new ArrayList<>(states.size());
        states.forEach(s -> res.add(s.getHeap()));
        return res;
    }

    @Override
    public AbortStrategy getAbortStrategy() {
        return stateSpaceGenerator.getAbortStrategy();
    }

    @Override
    public MaterializationStrategy getMaterializationStrategy() {
        return stateSpaceGenerator.getMaterializationStrategy();
    }

    @Override
    public CanonicalizationStrategy getCanonicalizationStrategy() {
        return stateSpaceGenerator.getCanonizationStrategy();
    }

    @Override
    public InclusionStrategy getInclusionStrategy() {
        return stateSpaceGenerator.getInclusionStrategy();
    }

    @Override
    public StateLabelingStrategy getStateLabelingStrategy() {
        return stateSpaceGenerator.getStateLabelingStrategy();
    }

    @Override
    public StateSpace getStateSpace() {
        if(stateSpace == null) {
           throw new IllegalStateException("No StateSpace has been generated yet.");
        }

        return stateSpace;
    }
}
