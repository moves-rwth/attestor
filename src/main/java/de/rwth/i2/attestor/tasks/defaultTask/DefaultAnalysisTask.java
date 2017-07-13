package de.rwth.i2.attestor.tasks.defaultTask;

import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.main.AnalysisTaskBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;
import de.rwth.i2.attestor.tasks.GeneralAnalysisTask;

/**
 * A simple analysis task based on hyperedge replacement grammars.
 *
 * @author Christoph
 */
public class DefaultAnalysisTask extends GeneralAnalysisTask {

   /**
    * @return A builder to create and customize a DefaultAnalysisTask.
    */
   public static AnalysisTaskBuilder builder() {
       return new DefaultAnalysisTaskBuilder();
   }

   DefaultAnalysisTask(StateSpaceGenerator generator) {
       super(generator);
   }

   /**
    * @return The path to the file to which terminal states are exported.
    */
   private String getPathForTerminalStates() {
       return Settings.getInstance().output().getLocationForTerminalStates();
   }

   /**
    * @return The path to the file to which the state space is exported.
    */
   private String getPathForStateSpace() {
       return Settings.getInstance().output().getLocationForStateSpace();
   }

    @Override
    public void exportAllStates() {
        Settings.getInstance().factory()
                .getStateSpaceExporter(getPathForStateSpace())
                .export("stateSpace", getStateSpace());
    }

    @Override
    public void exportTerminalStates() {
        HeapConfigurationExporter exporter =
                Settings.getInstance().factory().getHeapConfigurationExporter(
                        getPathForTerminalStates()
                );

        int id = 0;
        for( ProgramState state : stateSpace.getFinalStates() ){
            exporter.export( "terminal_" + id , state.getHeap() );
            id++;
        }
    }

}
