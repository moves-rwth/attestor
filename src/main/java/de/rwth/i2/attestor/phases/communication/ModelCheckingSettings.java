package de.rwth.i2.attestor.phases.communication;

import de.rwth.i2.attestor.LTLFormula;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * The collection of the model checking related communication including the
 * formulae.
 *
 * @author christina
 */
public class ModelCheckingSettings {

    private final Set<String> requiredAtomicPropositions = new LinkedHashSet<>();
    // Contains all LTL formulae model checking should be performed for.
    private final Set<LTLFormula> formulae;
    // Indicates whether model checking is conducted.
    private boolean modelCheckingEnabled = false;

    public ModelCheckingSettings() {

        this.formulae = new LinkedHashSet<>();
    }

    public boolean isModelCheckingEnabled() {

        return this.modelCheckingEnabled;
    }

    public void setModelCheckingEnabled(boolean enabled) {

        this.modelCheckingEnabled = enabled;
    }

    public Set<LTLFormula> getFormulae() {

        return this.formulae;
    }

    public void addFormula(LTLFormula formula) {

        this.formulae.add(formula);
        for (String ap : formula.getApList()) {
            requiredAtomicPropositions.add(extractAP(ap));
        }
    }

    private String extractAP(String apString) {

        String[] apContents = apString.split("[\\{\\}]");
        if (apContents.length < 2) {
            return null;
        }
        return apContents[1].trim();
    }


    public Set<String> getRequiredAtomicPropositions() {

        return requiredAtomicPropositions;
    }


}
