package de.rwth.i2.attestor.phases.symbolicExecution.recursive;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.*;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes.DefaultScopeExtractor;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.*;
import de.rwth.i2.attestor.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.MethodExecutor;
import de.rwth.i2.attestor.procedures.PreconditionMatchingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

import java.util.ArrayList;
import java.util.List;

public class InterproceduralStateSpaceGenerationPhase extends AbstractPhase implements StateSpaceTransformer {

    private StateSpace mainStateSpace = null;
    private final StateSpaceGeneratorFactory stateSpaceGeneratorFactory;

    public InterproceduralStateSpaceGenerationPhase(Scene scene) {

        super(scene);
        stateSpaceGeneratorFactory = new StateSpaceGeneratorFactory(scene);
    }

    @Override
    public String getName() {

        return "Interprocedural Analysis";
    }

    @Override
    protected void executePhase() {

        List<HeapConfiguration> inputs = getPhase(InputTransformer.class).getInputs();
        InputSettings inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();
        Method mainMethod = scene().getMethod(inputSettings.getMethodName());

        InterproceduralAnalysis interproceduralAnalysis = new InterproceduralAnalysis();
        InternalProcedureRegistry procedureRegistry = new InternalProcedureRegistry(interproceduralAnalysis,
                stateSpaceGeneratorFactory);
        initializeProcedures(procedureRegistry);

        createMainProcedure(mainMethod, inputs, interproceduralAnalysis);
        interproceduralAnalysis.run();

        if(mainStateSpace.getFinalStateIds().isEmpty()) {
            logger.error("Computed state space contains no final states.");
        }
    }

    private void createMainProcedure(Method method, List<HeapConfiguration> inputs, InterproceduralAnalysis interproceduralAnalysis) {

        List<ProgramState> initialStates = new ArrayList<>(inputs.size());
        for(HeapConfiguration hc : inputs) {
            initialStates.add(scene().createProgramState(hc));
        }

        try {
            mainStateSpace = stateSpaceGeneratorFactory.create(method.getBody(), initialStates).generate();
        } catch (StateSpaceGenerationAbortedException e) {
            e.printStackTrace();
        }

        for(ProgramState iState : initialStates) {
            PartialStateSpace mainStateSpace = new InternalPartialStateSpace(iState, stateSpaceGeneratorFactory);
            ProcedureCall mainCall = new InternalProcedureCall(method, iState, stateSpaceGeneratorFactory);
            interproceduralAnalysis.addMainProcedureCall(mainStateSpace, mainCall);
        }
    }

    private void initializeProcedures(ProcedureRegistry procedureRegistry) {

        PreconditionMatchingStrategy preconditionMatchingStrategy = new InternalPreconditionMatchingStrategy();

        for(Method method : scene ().getRegisteredMethods()) {
            MethodExecutor executor;
            ContractCollection contractCollection = new InternalContractCollection(preconditionMatchingStrategy);
            if(method.isRecursive()) {
                executor = new RecursiveMethodExecutor(
                        method,
                        new DefaultScopeExtractor(this, method.getName()),
                        contractCollection,
                        procedureRegistry
                );
            } else {
                executor = new NonRecursiveMethodExecutor(
                        new DefaultScopeExtractor(this, method.getName()),
                        contractCollection,
                        new InternalContractGenerator(stateSpaceGeneratorFactory, method.getBody())
                );
            }
            method.setMethodExecution(executor);
        }
    }

    @Override
    public void logSummary() {

        logSum("+-------------------------+------------------+");
        logHighlight("| Generated states        | Number of states |");
        logSum("+-------------------------+------------------+");
        logSum(String.format("| w/ procedure calls      | %16d |",
                scene().getNumberOfGeneratedStates()));
        logSum(String.format("| w/o procedure calls     | %16d |",
                mainStateSpace.getStates().size()));
        logSum(String.format("| final states            | %16d |",
                mainStateSpace.getFinalStateIds().size()));
        logSum("+-------------------------+------------------+");
    }

    @Override
    public boolean isVerificationPhase() {

        return true;
    }

    @Override
    public StateSpace getStateSpace() {

        return mainStateSpace;
    }
}
