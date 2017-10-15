package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;

import java.util.List;

public class StateSpaceGenerationPhase extends AbstractPhase implements StateSpaceTransformer {

    private StateSpace stateSpace;

    @Override
    public String getName() {

        return "State space generation";
    }

    @Override
    protected void executePhase() {

        settings.factory().resetTotalNumberOfStates();

        Program program = getPhase(ProgramTransformer.class).getProgram();
        List<HeapConfiguration> inputs = getPhase(InputTransformer.class).getInputs();

        StateSpaceGenerator stateSpaceGenerator = settings
                .factory()
                .createStateSpaceGenerator(
                        program,
                        inputs,
                        0
                );

        printAnalyzedMethod();

        try {
            stateSpace = stateSpaceGenerator.generate();
            logger.info("State space generation finished. #states: "
                    + settings.factory().getTotalNumberOfStates());
        } catch(StateSpaceGenerationAbortedException e) {
            logger.error("State space generation has been aborted prematurely.");
        }
    }

    private void printAnalyzedMethod() {

        logger.info("Analyzing '"
                + settings.input().getClasspath()
                + "/"
                + settings.input().getClassName()
                + "."
                + settings.input().getMethodName()
                + "'..."
        );
    }

    @Override
    public void logSummary() {

        logSum("+----------------------------------+--------------------------------+");
        logSum(String.format("| # states w/ procedure calls      | %30d |",
                settings.factory().getTotalNumberOfStates()));
        logSum(String.format("| # states w/o procedure calls     | %30d |",
                stateSpace.getStates().size()));
        logSum(String.format("| # final states                   | %30d |",
                stateSpace.getFinalStateIds().size()));
        logSum("+-----------+----------------------+--------------------------------+");
    }

    @Override
    public StateSpace getStateSpace() {

        return stateSpace;
    }
}
