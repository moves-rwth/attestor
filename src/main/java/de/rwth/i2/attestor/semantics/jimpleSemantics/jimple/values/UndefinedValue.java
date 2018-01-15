package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;

/**
 * Undefined Values are placeholders for Values our simulation does not know,
 * e.g. most numbers. They always evaluate to undefined concrete values
 *
 * @author Hannah Arndt
 */
public class UndefinedValue implements Value {

    public UndefinedValue() {

    }

    @Override
    public ConcreteValue evaluateOn(ProgramState programState) {

        return programState.getUndefined();
    }

    public Type getType() {

        return Types.UNDEFINED;
    }

    @Override
    public boolean needsMaterialization(ProgramState programState) {

        return false;
    }


    public String toString() {

        return "undefined";
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return ViolationPoints.getEmptyViolationPoints();
    }

}
