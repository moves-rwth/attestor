package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.graph.morphism.MorphismOptions;

public class AbstractionOptions implements MorphismOptions {

    private boolean admissibleAbstraction = false;
    private boolean admissibleConstants = false;
    private boolean admissibleMarkings = false;

    @Override
    public boolean isAdmissibleAbstraction() {
        return admissibleAbstraction;
    }

    @Override
    public boolean isAdmissibleConstants() {
        return admissibleConstants;
    }

    @Override
    public boolean isAdmissibleMarkings() {
        return admissibleMarkings;
    }

    public AbstractionOptions setAdmissibleAbstraction(boolean admissibleAbstraction) {
        this.admissibleAbstraction = admissibleAbstraction;
        return this;
    }

    public AbstractionOptions setAdmissibleConstants(boolean admissibleConstants) {
        this.admissibleConstants = admissibleConstants;
        return this;
    }

    public AbstractionOptions setAdmissibleMarkings(boolean admissibleMarkings) {
        this.admissibleMarkings = admissibleMarkings;
        return this;
    }
}
