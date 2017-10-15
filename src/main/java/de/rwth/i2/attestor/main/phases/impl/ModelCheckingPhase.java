package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.modelChecking.ProofStructure;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModelCheckingPhase extends AbstractPhase {

    private Map<LTLFormula, Boolean> formulaResults = new HashMap<>();

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
                formulaResults.put(formula, false);
                logger.warn("violated.");
            }
        }

    }

    @Override
    public void logSummary() {

        if(formulaResults.isEmpty()) {
            return;
        }

        boolean allSatisfied = true;
        logSum("Model checking results:");
        logSum("+-----------+-------------------------------------------------------+");
        for(Map.Entry<LTLFormula, Boolean> result : formulaResults.entrySet()) {
            logSum(String.format("| %-11s | %s", result.getValue(), result.getKey().getFormulaString()));
            allSatisfied &= result.getValue();
        }
        logSum("+-----------+-------------------------------------------------------+");

        if(allSatisfied) {
            logger.log(Level.getLevel("LTL-SAT"), "All provided LTL formulae are satisfied.");
        } else {
            logger.log(Level.getLevel("LTL-UNSAT"), "Some provided LTL formulae are violated.");
        }
    }
}
