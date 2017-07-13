package de.rwth.i2.attestor.tasks.indexedTask;

import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.main.AnalysisTaskBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;
import de.rwth.i2.attestor.tasks.GeneralAnalysisTask;

/**
 * An analysis task for performing an analysis based on indexed hyperedge replacement grammars.
 *
 * @author Christoph
 */
public class IndexedAnalysisTask extends GeneralAnalysisTask {

    /**
     * @return A builder to create and customize an IndexedAnalysisTask.
     */
    public static AnalysisTaskBuilder builder() {
        return new IndexedAnalysisTaskBuilder();
    }

    /**
     * Initializes the task. This method should only be used by IndexedAnalysisTaskBuilder.
     * @param generator A customized state space generator that includes all strategies.
     */
    IndexedAnalysisTask(StateSpaceGenerator generator) {
        super(generator);
    }

    @Override
    public void exportAllStates() {
        Settings.getInstance().factory()
                .getStateSpaceExporter(
                        Settings.getInstance().output().getLocationForStateSpace()
                )
                .export("stateSpace", getStateSpace());
    }

    @Override
    public void exportTerminalStates() {
        HeapConfigurationExporter exporter =
                Settings.getInstance().factory().getHeapConfigurationExporter(
                        Settings.getInstance().output().getLocationForTerminalStates()
                );

        int id = 0;
        for( ProgramState state : stateSpace.getFinalStates() ) {
            exporter.export( "terminal_" + id , state.getHeap() );
            id++;
        }
    }
}
