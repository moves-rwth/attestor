package de.rwth.i2.attestor.main;

import java.io.IOException;

public class DelayedPhase extends AbstractPhase {

    private AbstractPhase phase;
    private long delay;

    public DelayedPhase(AbstractPhase phase, long delay) {

        super(phase.scene());
        this.phase = phase;
        this.delay = delay;
    }

    @Override
    public String getName() {
        return phase.getName();
    }

    @Override
    public void executePhase() throws IOException {

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        phase.executePhase();
    }

    @Override
    public void logSummary() {

        phase.logSummary();
    }

    @Override
    public boolean isVerificationPhase() {
        return phase.isVerificationPhase();
    }
}
