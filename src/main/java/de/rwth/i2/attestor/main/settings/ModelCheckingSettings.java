package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.LTLFormula;

import java.util.HashSet;
import java.util.Set;


/**
 * The collection of the model checking related settings including the
 * formulae.
 *
 * @author christina
 */
public class ModelCheckingSettings {

    // Indicates whether model checking is conducted.
    private boolean modelCheckingEnabled = false;

    // Contains all LTL formulae model checking should be performed for.
    private Set<LTLFormula> formulae;

    public ModelCheckingSettings(){
        this.formulae = new HashSet<>();
    }

    public boolean isModelCheckingEnabled(){
        return this.modelCheckingEnabled;
    }

    public void setModelCheckingEnabled(boolean enabled){
        this.modelCheckingEnabled = enabled;
    }

    public Set<LTLFormula> getFormulae(){
        return this.formulae;
    }

    public void addFormula(LTLFormula formula){
        this.formulae.add(formula);
    }



}
