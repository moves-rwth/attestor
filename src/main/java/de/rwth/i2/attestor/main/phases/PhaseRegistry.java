package de.rwth.i2.attestor.main.phases;

import de.rwth.i2.attestor.main.settings.Settings;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PhaseRegistry {

   private static final Logger logger = LogManager.getLogger("PhaseRegistry");

   private Settings settings;

   private List<AbstractPhase> phases;

   public PhaseRegistry(Settings settings) {

      this.settings = settings;
      phases = new ArrayList<>();
   }

   public PhaseRegistry addPhase(AbstractPhase phase) {

      int size = phases.size();
      phase.register(size, this, settings);
      phases.add(phase);
      return this;
   }

   protected  <T> T getMostRecentPhase(int currentPhase, Class<T> phaseType) {

      for(int i=currentPhase-1; i >= 0; i--) {
         AbstractPhase phase = phases.get(i);
         if(phaseType.isInstance(phase)) {
            return phaseType.cast(phase);
         }
      }
      return null;
   }

   public <T> T getMostRecentPhase(Class<T> phaseType) {
      return getMostRecentPhase(phases.size(), phaseType);
   }

   protected Settings getSettings() {
      return settings;
   }

   public void execute() {

      try {
         for (AbstractPhase p : phases) {
            p.run();
         }
      } catch(Exception e) {
      }
   }

   public void logExecutionTimes() {

      Level REPORT = Level.getLevel("REPORT");

      double elapsedVerify = 0;
      double elapsedTotal = 0;
      logger.log(REPORT,"+----------------------------------+--------------------------------+");
      for(AbstractPhase p : phases) {
         double elapsed = p.getElapsedTime();
         elapsedTotal += elapsed;
         logger.log(REPORT, String.format("| %-32s | %28.3f s |", p.getName(), elapsed));

         if(p.isVerificationPhase()) {
            elapsedVerify += elapsed;
         }
      }
      logger.log(REPORT,"+----------------------------------+--------------------------------+");
      logger.log(REPORT, String.format("| Total runtime                    | %28.3f s |", elapsedTotal));
      logger.log(REPORT, String.format("| Total verification time          | %28.3f s |", elapsedVerify));
      logger.log(REPORT, "+----------------------------------+--------------------------------+");
   }

   public void logExecutionSummary() {

      for(AbstractPhase p : phases) {
         p.logSummary();
      }
   }

}

