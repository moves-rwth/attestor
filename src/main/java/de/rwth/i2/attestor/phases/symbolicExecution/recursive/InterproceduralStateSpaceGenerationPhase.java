package de.rwth.i2.attestor.phases.symbolicExecution.recursive;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.*;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes.DefaultScopeExtractor;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.*;
import de.rwth.i2.attestor.phases.transformers.*;
import de.rwth.i2.attestor.procedures.*;
import de.rwth.i2.attestor.stateSpaceGeneration.*;


public class InterproceduralStateSpaceGenerationPhase extends AbstractPhase implements StateSpaceTransformer {

    private final StateSpaceGeneratorFactory stateSpaceGeneratorFactory;

    private InterproceduralAnalysis interproceduralAnalysis;
    private List<ProgramState> initialStates;
    private Method mainMethod;
    private StateSpace mainStateSpace = null;

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

        interproceduralAnalysis = new InterproceduralAnalysis();
        loadInitialStates();
        loadMainMethod();
        initializeMethodExecutors();
        startPartialStateSpaceGeneration();
        registerMainProcedureCalls();
        interproceduralAnalysis.run();

        if(mainStateSpace.getFinalStateIds().isEmpty()) {
            logger.error("Computed state space contains no final states.");
        }
    }

    private void loadInitialStates() {

        List<HeapConfiguration> inputs = getPhase(InputTransformer.class).getInputs();
        initialStates = new ArrayList<>(inputs.size());
        for(HeapConfiguration hc : inputs) {
            initialStates.add(scene().createProgramState(hc));
        }
    }

    private void loadMainMethod() {

        InputSettings inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();
        mainMethod = scene().getMethod(inputSettings.getMethodName());
    }

    private void initializeMethodExecutors() {

        InternalProcedureRegistry procedureRegistry = new InternalProcedureRegistry(
                interproceduralAnalysis,
                stateSpaceGeneratorFactory
        );

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

    private void startPartialStateSpaceGeneration() {

        try {
            mainStateSpace = stateSpaceGeneratorFactory.create(mainMethod.getBody(), initialStates).generate();
        } catch (StateSpaceGenerationAbortedException e) {
            e.printStackTrace();
        }
    }

    private void registerMainProcedureCalls() {

        for(ProgramState iState : initialStates) {
            PartialStateSpace mainStateSpace = new InternalPartialStateSpace(iState, stateSpaceGeneratorFactory);
            ProcedureCall mainCall = new InternalProcedureCall(mainMethod, iState, stateSpaceGeneratorFactory);
            interproceduralAnalysis.addMainProcedureCall(mainStateSpace, mainCall);
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
