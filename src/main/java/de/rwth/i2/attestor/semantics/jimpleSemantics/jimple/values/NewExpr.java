package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;

/**
 * represents expressions of form new List() or new List( args ),
 * but only generates the new element without calling the constructor.
 * This is separated by jimple.
 *
 * @author Hannah Arndt
 */
public class NewExpr implements Value {

    private final Type type;

    public NewExpr(Type type) {

        this.type = type;
    }

    /**
     * inserts a new element of the expected type into the programState.
     *
     * @return the newly inserted element
     */
    @Override
    public ConcreteValue evaluateOn(ProgramState programState) {

        return programState.insertNewElement(type);
    }

    @Override
    public Type getType() {

        return this.type;
    }

    @Override
    public boolean needsMaterialization(ProgramState programState) {

        return false;
    }

    /**
     * @return "new type()"
     */
    public String toString() {

        return "new " + type + "()";
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return ViolationPoints.getEmptyViolationPoints();
    }

}
