package de.rwth.i2.attestor.phases.counterexamples;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.concretization.DefaultSingleStepConcretizationStrategy;
import de.rwth.i2.attestor.grammar.concretization.FullConcretizationStrategy;
import de.rwth.i2.attestor.grammar.concretization.FullConcretizationStrategyImpl;
import de.rwth.i2.attestor.grammar.concretization.SingleStepConcretizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration.CounterexampleGenerator;
import de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration.CounterexampleTrace;
import de.rwth.i2.attestor.phases.modelChecking.modelChecker.ModelCheckingResult;
import de.rwth.i2.attestor.phases.modelChecking.modelChecker.ModelCheckingTrace;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes.DefaultScopeExtractor;
import de.rwth.i2.attestor.phases.transformers.CounterexampleTransformer;
import de.rwth.i2.attestor.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.phases.transformers.ModelCheckingResultsTransformer;
import de.rwth.i2.attestor.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRectificationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CounterexampleGenerationPhase extends AbstractPhase implements CounterexampleTransformer {

    private final Map<LTLFormula, ProgramState> counterexamples = new LinkedHashMap<>();
    private ModelCheckingResultsTransformer modelCheckingResults;
    private Grammar grammar;
    private boolean allCounterexamplesDetected = true;

    public CounterexampleGenerationPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Counterexample generation";
    }

    @Override
    public void executePhase() {

        boolean canonicalEnabled = scene().options().isCanonicalEnabled();

        modelCheckingResults = getPhase(ModelCheckingResultsTransformer.class);
        grammar = getPhase(GrammarTransformer.class).getGrammar();
        for (Map.Entry<LTLFormula, ModelCheckingResult> result : modelCheckingResults.getLTLResults().entrySet()) {
            if (result.getValue() == ModelCheckingResult.UNSATISFIED) {
                LTLFormula formula = result.getKey();
                ModelCheckingTrace trace = modelCheckingResults.getTraceOf(formula);
                if (trace == null) {
                    continue;
                }

                if(!canonicalEnabled) {
                    counterexamples.put(formula, trace.getInitialState());
                    logger.warn("Verification of potentially spurious counterexamples is disabled.");
                    logger.warn("It is advised to rerun with option '--canonical'.");
                    continue;
                }

                try {

                    checkCounterexample(formula, trace);
                } catch (Exception e) {
                    allCounterexamplesDetected = false;
                    logger.error("Could not construct a non-spurious counterexample for formula:");
                    logger.error(formula);
                    logger.error("Cause: " + e.getMessage());
                }
            }
        }
    }

    private void checkCounterexample(LTLFormula formula, CounterexampleTrace trace) {

        Program program = getPhase(ProgramTransformer.class).getProgram();

        CanonicalizationStrategy canonicalizationStrategy = scene().strategies().getCanonicalizationStrategy();
        MaterializationStrategy materializationStrategy = scene().strategies().getMaterializationStrategy();
        StateRefinementStrategy stateRefinementStrategy = scene().strategies().getStateRefinementStrategy();
        StateRectificationStrategy stateRectificationStrategy = scene().strategies().getStateRectificationStrategy();

        CounterexampleGenerator generator = CounterexampleGenerator.builder()
                .setAvailableMethods(scene().getRegisteredMethods())
                .setCanonicalizationStrategy(canonicalizationStrategy)
                .setRectificationStrategy(stateRectificationStrategy)
                .setMaterializationStrategy(materializationStrategy)
                .setStateRefinementStrategy(stateRefinementStrategy)
                .setProgram(program)
                .setTrace(trace)
                .setScopeExtractorFactory(method -> new DefaultScopeExtractor(this, method.getName()))
                .build();

        ProgramState badInput = generator.generate();
        badInput = determineConcreteInput(badInput);
        counterexamples.put(formula, badInput);
        logger.info("detected concrete counterexample.");
    }

    private ProgramState determineConcreteInput(ProgramState badInput) {

        SingleStepConcretizationStrategy singleStepConcretizationStrategy = new DefaultSingleStepConcretizationStrategy(grammar);
        FullConcretizationStrategy fullConcretizationStrategy = new FullConcretizationStrategyImpl(singleStepConcretizationStrategy);
        List<HeapConfiguration> concreteBadInput = fullConcretizationStrategy.concretize(badInput.getHeap(), 1);

        if (concreteBadInput.isEmpty()) {
            throw new IllegalStateException("Could not generate a concrete program state corresponding to abstract counterexample input state.");
        }

        HeapConfiguration concretizedBadHeapConfiguration = concreteBadInput.get(0);
        return badInput.shallowCopyWithUpdateHeap(concretizedBadHeapConfiguration);
    }

    @Override
    public void logSummary() {

        if (counterexamples.isEmpty()) {
            return;
        }

        if(!scene().options().isCanonicalEnabled()) {
            logHighlight("Detected counterexamples are not verified.");
        } else if (allCounterexamplesDetected) {
            logHighlight("Detected a non-spurious counterexample for all violated LTL formulae.");
        } else {
            logHighlight("Some counterexampleGeneration might be spurious.");
        }
        for (Map.Entry<LTLFormula, ProgramState> result : counterexamples.entrySet()) {
            logSum(result.getKey().getFormulaString());
            logSum("      Counterexample trace: " + modelCheckingResults.getTraceOf(result.getKey()).getStateIdTrace());
        }
    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }

    @Override
    public Set<LTLFormula> getFormulasWithCounterexamples() {

        return counterexamples.keySet();
    }

    @Override
    public ProgramState getInputOf(LTLFormula formula) {

        if (counterexamples.containsKey(formula)) {
            return counterexamples.get(formula);
        }
        throw new IllegalArgumentException("No counterexample input for given formula exists.");
    }
}
