package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;


import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;

/**
 * Statements are {@link SemanticsCommand Sementics}
 * with {@link de.rwth.i2.attestor.stateSpaceGeneration.ProgramState ProgramState}
 * as heaps.
 *
 * @author Hannah Arndt
 */
public abstract class Statement extends SceneObject implements SemanticsCommand {

    /**
     * True if and only if canonicalization should be performed
     * immediately after executing this statement.
     */
    private boolean isCanonicalizationPermitted = true;

    protected Statement(SceneObject otherObject) {

        super(otherObject);
    }

    /**
     * @return True if and only if canonicalization should be performed
     * immediately after executing this statement.
     */
    public boolean permitsCanonicalization() {

        return isCanonicalizationPermitted;
    }

    /**
     * @param permitted True if and only if canonicalization should be performed
     *                  immediately after executing this statement.
     */
    public void setPermitCanonicalization(boolean permitted) {

        this.isCanonicalizationPermitted = permitted;
    }
}
