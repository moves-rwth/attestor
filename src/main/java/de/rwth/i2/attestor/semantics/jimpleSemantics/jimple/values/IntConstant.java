package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * IntConstants represent access to constants of type int
 *
 * @author Hannah Arndt
 */
public class IntConstant implements Value {

    private static final Logger logger = LogManager.getLogger("IntConstant");

    private final int intValue;

    public IntConstant(int value) {

        this.intValue = value;
    }

    @Override
    public ConcreteValue evaluateOn(ProgramState programState) {

        return programState.getConstant("" + intValue);
    }


    @Override
    public Type getType() {

        return Types.INT;
    }

    public String toString() {

        return "" + intValue;
    }

    @Override
    public boolean needsMaterialization(ProgramState programState) {

        return false;
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return ViolationPoints.getEmptyViolationPoints();
    }
}
