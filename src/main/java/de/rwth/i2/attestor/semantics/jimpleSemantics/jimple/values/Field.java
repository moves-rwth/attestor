package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * fields model selectors of objects, e.g. curr.next or x.left.right
 * or in general expr.field, where expr specifies the origin of the selector
 * and field its name. <br>
 * In jimple expr must be a local variable, i.e. x.left.right is not possible.
 * It is however supported by this class.
 *
 * @author Hannah Arndt
 */
public class Field implements SettableValue {

    private static final Logger logger = LogManager.getLogger("Field");

    /**
     * the element to which the field belongs
     */
    private final Value originValue;
    /**
     * the field / selector
     */
    private final SelectorLabel selectorLabel;
    /**
     * the expected type of this expression
     */
    private final Type type;

    private final ViolationPoints potentialViolationPoints;

    public Field(Type type, Value originValue, SelectorLabel selectorLabel) {

        this.type = type;
        this.originValue = originValue;
        this.selectorLabel = selectorLabel;

        potentialViolationPoints = new ViolationPoints(originValue.toString(), selectorLabel.getLabel());
    }

    @Override
    public Type getType() {

        return this.type;
    }

    /**
     * evaluates the expression defining the origin and gets the element referenced by
     * the field starting at this origin element. <br>
     *
     * @return undefined if the origin evaluates to undefined or if the corresponding
     * selector is missing at the origin element. The correct element otherwise.
     */
    @Override
    public ConcreteValue evaluateOn(ProgramState programState)
            throws NullPointerDereferenceException {

        ConcreteValue concreteOrigin = originValue.evaluateOn(programState);

        if (concreteOrigin.isUndefined()) {

            logger.trace("The origin evaluated to undefined. Returning undefined.");
            return programState.getUndefined();
        } else {

            if (programState.getConstant(Constants.NULL).equals(concreteOrigin)) {
                throw new NullPointerDereferenceException(originValue);
            }

            return programState.getSelectorTarget(concreteOrigin, selectorLabel);
        }
    }

    /**
     * Sets the value of the field in programState to concreteTarget. <br>
     * In this case the heap is not changed by the expression.<br>
     * Also logs a warning if the type of concreteTarget does not match the
     * expected type for the field.
     *
     * @param programState   the heap on which the assignment is performed
     * @param concreteTarget the value that is assigned to the heap
     * @throws NullPointerDereferenceException if the evaluation of the originValue
     *                                         results in a null pointer dereference.
     */
    @Override
    public void setValue(ProgramState programState, ConcreteValue concreteTarget)
            throws NullPointerDereferenceException {

        ConcreteValue concreteOrigin = originValue.evaluateOn(programState);
        if (concreteOrigin.isUndefined()) {
            logger.debug("Origin evaluated to undefined. Field is not reassigned");
        } else {
            programState.setSelector(concreteOrigin, selectorLabel, concreteTarget);
        }
    }

    @Override
    public boolean needsMaterialization(ProgramState programState) {

        return true;
    }


    /**
     * "origin.field"
     */
    public String toString() {

        return originValue + "." + selectorLabel.getLabel();
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return potentialViolationPoints;
    }
}
