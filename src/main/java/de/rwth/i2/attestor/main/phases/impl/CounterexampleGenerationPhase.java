package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.counterexampleGeneration.CounterexampleGenerator;
import de.rwth.i2.attestor.counterexampleGeneration.Trace;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.CounterexampleTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ModelCheckingResultsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CounterexampleGenerationPhase extends AbstractPhase implements CounterexampleTransformer {

    private ModelCheckingResultsTransformer modelCheckingResults;
    private final Map<LTLFormula, HeapConfiguration> counterexamples = new HashMap<>();

    @Override
    public String getName() {
        return "Counterexample generation";
    }

    @Override
    protected void executePhase() {

        modelCheckingResults = getPhase(ModelCheckingResultsTransformer.class);
        for(Map.Entry<LTLFormula, Boolean> result : modelCheckingResults.getLTLResults().entrySet()) {
            if(!result.getValue()) {
                LTLFormula formula = result.getKey();
                Trace trace = modelCheckingResults.getTraceOf(formula);

                try {
                    checkCounterexample(formula, trace);
                } catch(Exception e) {
                    logger.error("Could not construct a non-spurious counterexample for formula:");
                    logger.error(formula);
                }
            }
        }
    }

    private void checkCounterexample(LTLFormula formula, Trace trace) {

        Program program = getPhase(ProgramTransformer.class).getProgram();
        StateRefinementStrategy stateRefinementStrategy = settings.stateSpaceGeneration().getStateRefinementStrategy();
        MaterializationStrategy materializationStrategy = settings.stateSpaceGeneration().getMaterializationStrategy();
        CanonicalizationStrategy canonicalizationStrategy = settings.stateSpaceGeneration().getCanonicalizationStrategy();

        CounterexampleGenerator generator = CounterexampleGenerator.builder()
                .setProgram(program)
                .setTrace(trace)
                .setDeadVariableEliminationEnabled(settings.options().isRemoveDeadVariables())
                .setStateRefinementStrategy(stateRefinementStrategy)
                .setMaterializationStrategy(materializationStrategy)
                .setCanonicalizationStrategy(canonicalizationStrategy)
                .build();

        HeapConfiguration badInput = generator.generate();
        counterexamples.put(formula, badInput);
        logger.info("found counterexample.");
    }

    @Override
    public void logSummary() {

        logSum("Detected counterexamples for:");
        logSum("+-------------------------------------------------------------------+");
        for(Map.Entry<LTLFormula, HeapConfiguration> result : counterexamples.entrySet()) {
            logSum(String.format("|  %s", result.getKey().getFormulaString()));
            logSum("| Trace is " + modelCheckingResults.getTraceOf(result.getKey()).getStateIdTrace());
            logger.info(result.getValue());
        }
        logSum("+-------------------------------------------------------------------+");

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
    public HeapConfiguration getInputOf(LTLFormula formula) {
        if(counterexamples.containsKey(formula)) {
            return counterexamples.get(formula);
        }
        throw new IllegalArgumentException("No counterexample input for given formula exists.");
    }
}
