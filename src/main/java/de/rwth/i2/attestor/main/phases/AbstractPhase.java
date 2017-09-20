package de.rwth.i2.attestor.main.phases;

import de.rwth.i2.attestor.main.settings.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractPhase {

    protected static final Logger logger = LogManager.getLogger("AbstractPhase");
    protected static final String ANSI_GREEN = "\u001B[32m";
    protected static final String ANSI_RED = "\u001B[31m";
    protected static final String ANSI_RESET = "\u001B[0m";
    protected static final String ANSI_YELLOW = "\u001B[33m";


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
        logger.info(ANSI_YELLOW + "(started) " + ANSI_RESET + getName() +  "...");
    }


    private void logSuccess() {
        logger.info(ANSI_GREEN + "(finished) " + ANSI_RESET + getName());
    }

    private void logFail(Exception e) {
        e.printStackTrace();
        logger.fatal(e.getMessage());
        logger.info(ANSI_RED + "(failed) " + ANSI_RESET + getName());
        System.exit(1);
    }
}
