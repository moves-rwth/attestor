package de.rwth.i2.attestor.main.phases.modelChecking;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.main.phases.transformers.MCSettingsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ModelCheckingResultsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.modelChecking.FailureTrace;
import de.rwth.i2.attestor.modelChecking.ModelCheckingTrace;
import de.rwth.i2.attestor.modelChecking.ProofStructure;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import org.apache.logging.log4j.Level;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ModelCheckingPhase extends AbstractPhase implements ModelCheckingResultsTransformer {

    private final Map<LTLFormula, Boolean> formulaResults = new LinkedHashMap<>();
    private final Map<LTLFormula, ModelCheckingTrace> traces = new LinkedHashMap<>();
    private boolean allSatisfied = true;

    private int numberSatFormulae = 0;

    public ModelCheckingPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Model checking";
    }

    @Override
    protected void executePhase() {

        ModelCheckingSettings mcSettings = getPhase(MCSettingsTransformer.class).getMcSettings();
        Set<LTLFormula> formulae = mcSettings.getFormulae();
        if (formulae.isEmpty()) {
            logger.debug("No LTL formulae have been provided.");
            return;
        }

        StateSpace stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();

        for (LTLFormula formula : formulae) {

            String formulaString = formula.getFormulaString();
            logger.info("Checking formula: " + formulaString + "...");
            ProofStructure proofStructure = new ProofStructure();
            proofStructure.build(stateSpace, formula);
            if (proofStructure.isSuccessful()) {
                formulaResults.put(formula, true);
                logger.info("done. Formula is satisfied.");
                numberSatFormulae++;
            } else {
                logger.warn("Formula is violated: " + formulaString);
                allSatisfied = false;
                formulaResults.put(formula, false);

                if (scene().options().isIndexedMode()) {
                    logger.warn("Counterexample generation for indexed grammars is not supported yet.");
                } else {
                    FailureTrace failureTrace = proofStructure.getFailureTrace();
                    traces.put(formula, failureTrace);
                }
            }
        }
    }

    @Override
    public void logSummary() {

        if (formulaResults.isEmpty()) {
            return;
        }

        if (allSatisfied) {
            logHighlight("Model checking results: All provided LTL formulae are satisfied.");
        } else {
            logHighlight("Model checking results: Some provided LTL formulae are violated.");
        }

        for (Map.Entry<LTLFormula, Boolean> result : formulaResults.entrySet()) {
            if (result.getValue()) {
                logger.log(Level.getLevel("LTL-SAT"), result.getKey().getFormulaString());
            } else {
                logger.log(Level.getLevel("LTL-UNSAT"), result.getKey().getFormulaString());
            }
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
    public ModelCheckingTrace getTraceOf(LTLFormula formula) {

        if (traces.containsKey(formula)) {
            return traces.get(formula);
        }
        return null;
    }

    @Override
    public boolean hasAllLTLSatisfied() {

        return allSatisfied;
    }

    @Override
    public int getNumberSatFormulae() {
        return numberSatFormulae;
    }
}
