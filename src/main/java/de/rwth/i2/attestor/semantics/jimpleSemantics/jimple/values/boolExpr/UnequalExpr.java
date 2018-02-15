package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.boolExpr;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullPointerDereferenceException;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents expressions of the form x != y
 *
 * @author Hannah Arndt
 */
public class UnequalExpr implements Value {

    private static final Logger logger = LogManager.getLogger("UnequalExpr");

    /**
     * x in x != y
     */
    private final Value leftExpr;
    /**
     * y in x != y
     */
    private final Value rightExpr;

    private final ViolationPoints potentialViolationPoints;

    public UnequalExpr(Value leftExpr, Value rightExpr) {

        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        this.potentialViolationPoints = new ViolationPoints();
        this.potentialViolationPoints.addAll(leftExpr.getPotentialViolationPoints());
        this.potentialViolationPoints.addAll(rightExpr.getPotentialViolationPoints());
    }

    /**
     * evaluates both expressions on the executable and returns the element representing
     * true if they don't result in the same element (otherwise false). undefined if one of the expressions
     * evaluates to undefined.
     *
     * @return the heap element representing true/false or undefined.
     */
    @Override
    public ConcreteValue evaluateOn(ProgramState programState) {

        ConcreteValue leftRes;
        try {
            leftRes = leftExpr.evaluateOn(programState);
        } catch (NullPointerDereferenceException e) {
            logger.error(e.getErrorMessage(this));
            return programState.getUndefined();
        }
        if (leftRes.isUndefined()) {
            logger.debug("leftExpr evaluated to undefined. Returning undefined.");
            return programState.getUndefined();
        }
        ConcreteValue rightRes;
        try {
            rightRes = rightExpr.evaluateOn(programState);
        } catch (NullPointerDereferenceException e) {
            logger.error("Null pointer dereference detected in " + rightExpr);
            return programState.getUndefined();
        }
        if (rightRes.isUndefined()) {
            //logger.debug( "rightExpr evaluated to undefined. Returning undefined." );
            return programState.getUndefined();
        }

        return programState.getConstant("" + (!leftRes.equals(rightRes)));
    }

    @Override
    public boolean needsMaterialization(ProgramState programState) {

        return rightExpr.needsMaterialization(programState) || leftExpr.needsMaterialization(programState);
    }


    @Override
    public Type getType() {

        return Types.INT;
    }

    public String toString() {

        return leftExpr + " != " + rightExpr;
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return potentialViolationPoints;
    }

}
