package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.counterexampleGeneration.CounterexampleGenerator;
import de.rwth.i2.attestor.counterexampleGeneration.Trace;
import de.rwth.i2.attestor.grammar.concretization.Concretizer;
import de.rwth.i2.attestor.grammar.concretization.NaiveConcretizer;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.CounterexampleTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ModelCheckingResultsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceGenerationTransformer;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CounterexampleGenerationPhase extends AbstractPhase implements CounterexampleTransformer {

    private final Map<LTLFormula, HeapConfiguration> counterexamples = new HashMap<>();
    private ModelCheckingResultsTransformer modelCheckingResults;

    public CounterexampleGenerationPhase(Scene scene) {

        super(scene);
    }
    private boolean allCounterexamplesDetected = true;

    @Override
    public String getName() {

        return "Counterexample generation";
    }

    @Override
    protected void executePhase() {

        modelCheckingResults = getPhase(ModelCheckingResultsTransformer.class);
        for (Map.Entry<LTLFormula, Boolean> result : modelCheckingResults.getLTLResults().entrySet()) {
            if (!result.getValue()) {
                LTLFormula formula = result.getKey();
                Trace trace = modelCheckingResults.getTraceOf(formula);
                if (trace == null) {
                    continue;
                }

                try {
                    checkCounterexample(formula, trace);
                } catch(Exception e) {
                    allCounterexamplesDetected = false;
                    logger.error("Could not construct a non-spurious counterexample for formula:");
                    logger.error(formula);
                }
            }
        }
    }

    private void checkCounterexample(LTLFormula formula, Trace trace) {

        Program program = getPhase(ProgramTransformer.class).getProgram();

        StateSpaceGenerationTransformer transformer = getPhase(StateSpaceGenerationTransformer.class);

        StateRefinementStrategy stateRefinementStrategy = transformer.getStateRefinementStrategy();
        MaterializationStrategy materializationStrategy = transformer.getMaterializationStrategy();
        CanonicalizationStrategy canonicalizationStrategy = transformer.getAggressiveCanonicalizationStrategy();

        CounterexampleGenerator generator = CounterexampleGenerator.builder(this)
                .setProgram(program)
                .setTrace(trace)
                .setDeadVariableEliminationEnabled(scene().options().isRemoveDeadVariables())
                .setStateRefinementStrategy(stateRefinementStrategy)
                .setMaterializationStrategy(materializationStrategy)
                .setCanonicalizationStrategy(canonicalizationStrategy)
                .build();

        ProgramState badInput = generator.generate();
        badInput = determineConcreteInput(badInput);
        counterexamples.put(formula, badInput);
        logger.info("detected concrete counterexample.");
    }

    private ProgramState determineConcreteInput(ProgramState badInput) {

        Concretizer concretizer = new NaiveConcretizer(settings.grammar().getGrammar());
        List<HeapConfiguration> concreteBadInput = concretizer.concretize(badInput.getHeap(), 1);

        if(concreteBadInput.isEmpty()) {
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

        if(allCounterexamplesDetected) {
            logHighlight("Detected counterexamples for all violated LTL formulae.");
        } else {
            logHighlight("Some counterexamples might be spurious.");
        }
        for(Map.Entry<LTLFormula, ProgramState> result : counterexamples.entrySet()) {
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
        if(counterexamples.containsKey(formula)) {
            return counterexamples.get(formula);
        }
        throw new IllegalArgumentException("No counterexample input for given formula exists.");
    }
}
