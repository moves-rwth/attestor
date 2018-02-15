package de.rwth.i2.attestor.main;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PhaseRegistry {

    private static final Logger logger = LogManager.getLogger("PhaseRegistry");

    private final List<AbstractPhase> phases;

    public PhaseRegistry() {

        phases = new ArrayList<>();
    }

    public PhaseRegistry addPhase(AbstractPhase phase) {

        int size = phases.size();
        phase.register(size, this);
        phases.add(phase);
        return this;
    }

    protected <T> T getMostRecentPhase(int currentPhase, Class<T> phaseType) {

        for (int i = currentPhase - 1; i >= 0; i--) {
            AbstractPhase phase = phases.get(i);
            if (phaseType.isInstance(phase)) {
                return phaseType.cast(phase);
            }
        }
        throw new IllegalArgumentException("No suitable phase transformer could be found: " + phaseType);
    }

    public <T> T getMostRecentPhase(Class<T> phaseType) {

        return getMostRecentPhase(phases.size(), phaseType);
    }

    public void execute() throws Exception {

        for (AbstractPhase p : phases) {
            p.run();
        }
    }

    public void logExecutionTimes() {

        Level REPORT = Level.getLevel("REPORT");
        Level HIGHLIGHT = Level.getLevel("HIGHLIGHT");

        double elapsedVerify = 0;
        double elapsedTotal = 0;
        logger.log(REPORT, "+-----------------------------+--------------+");
        logger.log(HIGHLIGHT, "| Phase                       | Runtime      |");
        logger.log(REPORT, "+-----------------------------+--------------+");
        for (AbstractPhase p : phases) {
            double elapsed = p.getElapsedTime();
            elapsedTotal += elapsed;
            logger.log(REPORT, String.format("| %-27s | %10.3f s |", p.getName(), elapsed));

            if (p.isVerificationPhase()) {
                elapsedVerify += elapsed;
            }
        }
        logger.log(REPORT, "+-----------------------------+--------------+");
        logger.log(REPORT, String.format("| Total verification time     | %10.3f s |", elapsedVerify));
        logger.log(REPORT, String.format("| Total runtime               | %10.3f s |", elapsedTotal));
        logger.log(REPORT, "+-----------------------------+--------------+");
    }

    public void logExecutionSummary() {

        for (AbstractPhase p : phases) {
            p.logSummary();
        }
    }

    public List<AbstractPhase> getPhases() {
        return phases;
    }

}

