package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * Values model all kinds of expressions. They can be evaluated on
 * JimpleExecutables to obtain their concrete value.
 *
 * @author Hannah Arndt
 */
public interface Value {

    ConcreteValue evaluateOn(ProgramState programState)
            throws NotSufficientlyMaterializedException, NullPointerDereferenceException;

    boolean needsMaterialization(ProgramState programState);

    Type getType();

    /**
     * @return The (variable,selector) pairs that might require materialization before this expression
     * can be evaluated.
     */
    ViolationPoints getPotentialViolationPoints();
}
