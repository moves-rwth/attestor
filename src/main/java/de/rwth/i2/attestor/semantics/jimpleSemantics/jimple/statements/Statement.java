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

    protected Statement(SceneObject otherObject) {

        super(otherObject);
    }

}
