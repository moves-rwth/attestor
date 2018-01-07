package de.rwth.i2.attestor.main.phases.symbolicExecution.interprocedural;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.interprocedural.InterproceduralAnalysis;
import de.rwth.i2.attestor.interprocedural.ProcedureRegistry;
import de.rwth.i2.attestor.interprocedural.RecursiveMethodExecutor;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.symbolicExecution.InternalContractGenerator;
import de.rwth.i2.attestor.main.phases.symbolicExecution.InternalPreconditionMatchingStrategy;
import de.rwth.i2.attestor.main.phases.symbolicExecution.StateSpaceGeneratorFactory;
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
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.ArrayList;
import java.util.List;

public class InterproceduralAnalysisPhase extends AbstractPhase implements StateSpaceTransformer {

    private StateSpace mainStateSpace = null;
    private final StateSpaceGeneratorFactory stateSpaceGeneratorFactory;

    public InterproceduralAnalysisPhase(Scene scene) {

        super(scene);
        stateSpaceGeneratorFactory = new StateSpaceGeneratorFactory(scene);
    }

    @Override
    public String getName() {

        return "Interprocedural Analysis";
    }

    @Override
    protected void executePhase() {

        InterproceduralAnalysis interproceduralAnalysis = new InterproceduralAnalysis();
        InternalProcedureRegistry procedureRegistry = new InternalProcedureRegistry(interproceduralAnalysis,
                stateSpaceGeneratorFactory);
        initializeProcedures(procedureRegistry);

        MainProcedureCall mainProcedure = createMainProcedure();
        interproceduralAnalysis.setMainProcedure(mainProcedure);
        interproceduralAnalysis.run();

        mainStateSpace = mainProcedure.getStateSpace();

        if(mainStateSpace.getFinalStateIds().isEmpty()) {
            logger.error("Computed state space contains no final states.");
        }
    }

    private MainProcedureCall createMainProcedure() {

        Program program = getPhase(ProgramTransformer.class).getProgram();
        List<HeapConfiguration> inputs = getPhase(InputTransformer.class).getInputs();

        List<ProgramState> initialStates = new ArrayList<>(inputs.size());
        for(HeapConfiguration hc : inputs) {
            initialStates.add(scene().createProgramState(hc));
        }
        return new MainProcedureCall(program, initialStates, stateSpaceGeneratorFactory);

    }

    private void initializeProcedures(ProcedureRegistry procedureRegistry) {

        PreconditionMatchingStrategy preconditionMatchingStrategy = new InternalPreconditionMatchingStrategy();
        for(Method method : scene ().getRegisteredMethods()) {
            MethodExecutor executor;
            if(method.isRecursive()) {
                executor = new RecursiveMethodExecutor(
                        method,
                        new DefaultScopeExtractor(this, method.getName()),
                        new InternalContractCollection(preconditionMatchingStrategy),
                        procedureRegistry
                );
            } else {
                executor = new ContractBasedMethod(
                        new DefaultScopeExtractor(this, method.getName()),
                        new InternalContractCollection(preconditionMatchingStrategy),
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
