package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.LTLResultTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.modelChecking.Counterexample;
import de.rwth.i2.attestor.modelChecking.ProofStructure;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModelCheckingPhase extends AbstractPhase implements LTLResultTransformer {

    private Map<LTLFormula, Counterexample> formulaResults = new HashMap<>();
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
                formulaResults.put(formula, new Counterexample());
                logger.info("satisfied.");
            } else {
                allSatisfied = false;
                formulaResults.put(formula, proofStructure.getCounterexample());
                logger.warn("violated.");
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
        for(Map.Entry<LTLFormula, Counterexample> result : formulaResults.entrySet()) {
            boolean isSat = result.getValue().isEmpty();
            if(isSat) {
                logSum(String.format("| %-9s | %s", "satisfied", result.getKey().getFormulaString()));
            } else {
                logSum(String.format("| %-9s | %s", "violated", result.getKey().getFormulaString()));
                logSum(String.format(String.format("| %-9s | %s", "witness", result.getValue())));
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
    public Map<LTLFormula, Counterexample> getLTLResults() {

        return formulaResults;
    }

    @Override
    public boolean hasAllLTLSatisfied() {

        return allSatisfied;
    }


}
