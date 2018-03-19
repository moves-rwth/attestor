package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public abstract class AbstractPhase extends SceneObject {

    protected static final Logger logger = LogManager.getLogger("AbstractPhase");
    private int phaseId;
    private PhaseRegistry registry;
    private long startTime;
    private long finishTime;

    public AbstractPhase(Scene scene) {

        super(scene);
    }

    protected void register(int phaseId, PhaseRegistry registry) {

        this.phaseId = phaseId;
        this.registry = registry;
    }

    public abstract String getName();

    public double getElapsedTime() {

        return (finishTime - startTime) / 1e9;
    }

    protected <T> T getPhase(Class<T> phaseType) {

        return registry.getMostRecentPhase(phaseId, phaseType);
    }

    public abstract void executePhase() throws IOException;

    public abstract void logSummary();

    public abstract boolean isVerificationPhase();

    public void run() throws Exception {

        try {
            logStart();
            startTime = System.nanoTime();
            executePhase();
            finishTime = System.nanoTime();
            logSuccess();
        } catch (Exception e) {
            logFail(e);
        }
    }

    private void logStart() {

        logger.debug(getName() + " started.");
    }


    private void logSuccess() {

        logger.debug(getName() + " finished.");
    }

    private void logFail(Exception e) throws Exception {

        logger.fatal(getName() + " failed.");
        logger.fatal(e.getMessage());
        throw e;
    }

    protected void logSum(String message) {

        logger.log(Level.getLevel("REPORT"), message);
    }

    protected void logHighlight(String message) {

        logger.log(Level.getLevel("HIGHLIGHT"), message);
    }
}
