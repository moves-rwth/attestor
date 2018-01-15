package de.rwth.i2.attestor.phases.symbolicExecution;

import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.symbolicExecution.nonRecursive.StateSpaceGenerationPhase;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.RecursiveStateSpaceGenerationPhase;
import de.rwth.i2.attestor.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.io.IOException;

public class SymbolicExecutionPhase extends AbstractPhase implements StateSpaceTransformer {

    private AbstractPhase activePhase = null;

    public SymbolicExecutionPhase(Scene scene) {
        super(scene);
    }

    @Override
    public String getName() {

        return "State space generation";
    }

    @Override
    public void executePhase() throws IOException {

        if(isRecursiveAnalysis()) {
            activePhase = new RecursiveStateSpaceGenerationPhase(scene());
        } else {
            activePhase = new StateSpaceGenerationPhase(scene());
        }
        activePhase.executePhase();
    }

    private boolean isRecursiveAnalysis() {

        for(Method method : scene ().getRegisteredMethods()) {
            if(method.isRecursive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void logSummary() {

        activePhase.logSummary();
    }

    @Override
    public boolean isVerificationPhase() {

        return true;
    }

    @Override
    public StateSpace getStateSpace() {
        return ((StateSpaceTransformer) activePhase).getStateSpace();
    }
}
