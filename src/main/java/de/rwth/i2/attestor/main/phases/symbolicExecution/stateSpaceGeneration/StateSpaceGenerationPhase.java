package de.rwth.i2.attestor.main.phases.symbolicExecution.stateSpaceGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.InputSettings;
import de.rwth.i2.attestor.main.phases.symbolicExecution.InternalContractGenerator;
import de.rwth.i2.attestor.main.phases.symbolicExecution.InternalPreconditionMatchingStrategy;
import de.rwth.i2.attestor.main.phases.symbolicExecution.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.main.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.MethodExecutor;
import de.rwth.i2.attestor.procedures.contracts.InternalContractCollection;
import de.rwth.i2.attestor.procedures.methodExecution.ContractBasedMethod;
import de.rwth.i2.attestor.procedures.methodExecution.PreconditionMatchingStrategy;
import de.rwth.i2.attestor.procedures.scopes.DefaultScopeExtractor;
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

            MethodExecutor executor = new ContractBasedMethod(
                    new DefaultScopeExtractor(this, method.getName()),
                    new InternalContractCollection(preconditionMatchingStrategy),
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
