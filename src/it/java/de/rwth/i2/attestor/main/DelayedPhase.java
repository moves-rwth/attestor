package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.main.scene.Scene;

import java.io.IOException;

public class DelayedPhase extends AbstractPhase {

    private long delay;

    public DelayedPhase(Scene scene, long delay) {

        super(scene);
        this.delay = delay;
    }

    @Override
    public String getName() {
        return "Delay";
    }

    @Override
    public void executePhase() throws IOException {

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logSummary() {
    }

    @Override
    public boolean isVerificationPhase() {
        return false;
    }
}
