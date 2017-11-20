package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.counterexampleGeneration.Trace;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.LTLResultTransformer;
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
    private final Map<LTLFormula, Trace> counterexampleTraces = new HashMap<>();
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
                formulaResults.put(formula, false);

                if(settings.options().isIndexedMode()) {
                    logger.warn("Counterexample generation for indexed grammars is not supported yet.");
                } else {
                    FailureTrace failureTrace = proofStructure.getFailureTrace();
                    counterexampleTraces.put(formula, failureTrace) ;
                }
            }
        }
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
    public Trace getTraceOf(LTLFormula formula) {
        if(counterexampleTraces.containsKey(formula)) {
            return counterexampleTraces.get(formula);
        }
        throw new IllegalArgumentException("Unknown LTL formula.");
    }

    @Override
    public boolean hasAllLTLSatisfied() {

        return allSatisfied;
    }
}
