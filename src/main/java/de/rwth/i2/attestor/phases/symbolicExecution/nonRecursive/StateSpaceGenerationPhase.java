package de.rwth.i2.attestor.phases.symbolicExecution.nonRecursive;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.*;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes.DefaultScopeExtractor;
import de.rwth.i2.attestor.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.MethodExecutor;
import de.rwth.i2.attestor.procedures.PreconditionMatchingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.ArrayList;
import java.util.List;

public class StateSpaceGenerationPhase extends AbstractPhase implements StateSpaceTransformer {

    private StateSpace stateSpace;
    private StateSpaceGeneratorFactory factory;

    public StateSpaceGenerationPhase(Scene scene) {

        super(scene);
        factory = new StateSpaceGeneratorFactory(scene);
    }

    @Override
    public String getName() {

        return "State space generation";
    }

    @Override
    protected void executePhase() {

        Program program = getPhase(ProgramTransformer.class).getProgram();
        List<HeapConfiguration> inputs = getPhase(InputTransformer.class).getInputs();

        initializeProcedures();

        List<ProgramState> initialStates = new ArrayList<>(inputs.size());
        for(HeapConfiguration hc : inputs) {
            initialStates.add(scene().createProgramState(hc));
        }
        StateSpaceGenerator stateSpaceGenerator = factory.create(program, initialStates);

        printAnalyzedMethod();

        try {
            stateSpace = stateSpaceGenerator.generate();
            logger.info("State space generation finished. #states: "
                    + scene().getNumberOfGeneratedStates());
        } catch (StateSpaceGenerationAbortedException e) {
            logger.error("State space generation has been aborted prematurely.");
            stateSpace = stateSpaceGenerator.getStateSpace();
        }
    }

    private void initializeProcedures() {

        PreconditionMatchingStrategy preconditionMatchingStrategy = new InternalPreconditionMatchingStrategy();

        for(Method method : scene ().getRegisteredMethods()) {

            ContractCollection contractCollection = new InternalContractCollection(preconditionMatchingStrategy);
            MethodExecutor executor = new NonRecursiveMethodExecutor(
                    new DefaultScopeExtractor(this, method.getName()),
                    contractCollection,
                    new InternalContractGenerator(factory, method.getBody())
            );
            method.setMethodExecution(executor);
        }
    }

    private void printAnalyzedMethod() {

        InputSettings inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();

        logger.info("Analyzing '"
                + inputSettings.getClasspath()
                + "/"
                + inputSettings.getClassName()
                + "."
                + inputSettings.getMethodName()
                + "'..."
        );
    }

    @Override
    public void logSummary() {

        logSum("+-------------------------+------------------+");
        logHighlight("| Generated states        | Number of states |");
        logSum("+-------------------------+------------------+");
        logSum(String.format("| w/ procedure calls      | %16d |",
                scene().getNumberOfGeneratedStates()));
        logSum(String.format("| w/o procedure calls     | %16d |",
                stateSpace.getStates().size()));
        logSum(String.format("| final states            | %16d |",
                stateSpace.getFinalStateIds().size()));
        logSum("+-------------------------+------------------+");
    }

    @Override
    public boolean isVerificationPhase() {

        return true;
    }

    @Override
    public StateSpace getStateSpace() {

        return stateSpace;
    }
}
