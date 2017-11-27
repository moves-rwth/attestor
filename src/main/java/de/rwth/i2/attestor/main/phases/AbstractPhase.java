package de.rwth.i2.attestor.main.phases;

import de.rwth.i2.attestor.main.settings.Settings;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractPhase {

    protected static final Logger logger = LogManager.getLogger("AbstractPhase");


    private int phaseId;
    private PhaseRegistry registry;

    private long startTime;
    private long finishTime;

    protected Settings settings;

    protected void register(int phaseId, PhaseRegistry registry, Settings settings) {
        this.phaseId = phaseId;
        this.registry = registry;
        this.settings = settings;
    }

    public abstract String getName();

    public double getElapsedTime() {

        return (finishTime - startTime) / 1e9;
    }

    protected <T> T getPhase(Class<T> phaseType) {
        return registry.getMostRecentPhase(phaseId, phaseType);
    }

    protected abstract void executePhase();

    public abstract void logSummary();

    public abstract boolean isVerificationPhase();

    public void run() {

        try {
            logStart();
            startTime = System.nanoTime();
            executePhase();
            finishTime = System.nanoTime();
            logSuccess();
        } catch(Exception e) {
            logFail(e);
        }
    }

    private void logStart() {
        logger.debug(getName() +  " started.");
    }


    private void logSuccess() {
        logger.debug(getName() + " finished.");
    }

    private void logFail(Exception e) {
        logger.fatal(getName() + " failed.");
        logger.fatal(e.getMessage());
        e.printStackTrace();
        System.exit(1);
    }

    protected void logSum(String message) {

        logger.log(Level.getLevel("REPORT"), message);
    }

    protected  void logHighlight(String message) {

        logger.log(Level.getLevel("HIGHLIGHT"), message);
    }
}
