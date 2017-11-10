package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.counterexampleGeneration.CounterexampleGenerator;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.LTLResultTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.modelChecking.FailureTrace;
import de.rwth.i2.attestor.modelChecking.ProofStructure;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModelCheckingPhase extends AbstractPhase implements LTLResultTransformer {

    private final Map<LTLFormula, Boolean> formulaResults = new HashMap<>();
    private final Map<LTLFormula, HeapConfiguration> counterexamples = new HashMap<>();
    private boolean allSatisfied = true;

    @Override
    public String getName() {

        return "Model checking";
    }

    @Override
    protected void executePhase() {

        Set<LTLFormula> formulae = settings.modelChecking().getFormulae();
        if(formulae.isEmpty()) {
            logger.debug("No LTL formulae have been provided.");
            return;
        }

        StateSpace stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();

        for(LTLFormula formula : formulae) {

            String formulaString = formula.getFormulaString();
            logger.info("Checking formula: " + formulaString + "...");
            ProofStructure proofStructure = new ProofStructure();
            proofStructure.build(stateSpace, formula);
            if(proofStructure.isSuccessful()) {
                formulaResults.put(formula, true);
                logger.info("satisfied.");
            } else {
                logger.warn("violated.");
                allSatisfied = false;
                FailureTrace failureTrace = proofStructure.getFailureTrace();
                formulaResults.put(formula, false);
                checkCounterexample(formula, failureTrace);
            }
        }
    }

    private void checkCounterexample(LTLFormula formula, FailureTrace failureTrace) {

        Program program = getPhase(ProgramTransformer.class).getProgram();
        StateRefinementStrategy stateRefinementStrategy = settings.stateSpaceGeneration().getStateRefinementStrategy();
        MaterializationStrategy materializationStrategy = settings.stateSpaceGeneration().getMaterializationStrategy();
        CanonicalizationStrategy canonicalizationStrategy = settings.stateSpaceGeneration().getCanonicalizationStrategy();

        CounterexampleGenerator generator = CounterexampleGenerator.builder()
                    .setProgram(program)
                    .setTrace(failureTrace)
                    .setDeadVariableEliminationEnabled(settings.options().isRemoveDeadVariables())
                    .setStateRefinementStrategy(stateRefinementStrategy)
                    .setMaterializationStrategy(materializationStrategy)
                    .setCanonicalizationStrategy(canonicalizationStrategy)
                    .build();

        HeapConfiguration badInput = generator.generate();
        counterexamples.put(formula, badInput);
        logger.info("Constructed counterexample.");
    }

    @Override
    public void logSummary() {

        if(formulaResults.isEmpty()) {
            return;
        }

        logSum("Model checking results:");
        logSum("+-----------+-------------------------------------------------------+");
        for(Map.Entry<LTLFormula, Boolean> result : formulaResults.entrySet()) {
            if(result.getValue()) {
                logSum(String.format("| %-9s | %s", "satisfied", result.getKey().getFormulaString()));
            } else {
                logSum(String.format("| %-9s | %s", "violated", result.getKey().getFormulaString()));

                if(counterexamples.containsKey(result.getKey())) {
                    logSum(String.format("| %-9s | %s", "",
                            "A counterexample has been found."));
                } else {
                    logSum(String.format("| %-9s | %s", "witness",
                            "All detected counterexamples might be spurious."));
                }

            }
        }
        logSum("+-----------+-------------------------------------------------------+");

        if(allSatisfied) {
            logger.log(Level.getLevel("LTL-SAT"), "All provided LTL formulae are satisfied.");
        } else {
            logger.log(Level.getLevel("LTL-UNSAT"), "Some provided LTL formulae are violated.");
        }
    }

    @Override
    public boolean isVerificationPhase() {

        return true;
    }

    @Override
    public Map<LTLFormula, Boolean> getLTLResults() {

        return formulaResults;
    }

    @Override
    public boolean hasAllLTLSatisfied() {

        return allSatisfied;
    }

    @Override
    public HeapConfiguration getCounterexampleInput(LTLFormula formula) {

        return counterexamples.get(formula);
    }


}
