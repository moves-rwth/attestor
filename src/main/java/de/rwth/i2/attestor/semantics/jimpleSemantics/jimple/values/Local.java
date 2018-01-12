package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Locals represent local variables
 *
 * @author Hannah Arndt
 */
public class Local implements SettableValue {

    private static final Logger logger = LogManager.getLogger("Local");

    /**
     * the expected type
     */
    private final Type type;
    /**
     * the name of the local variable
     */
    private final String name;

    public Local(Type type, String name) {

        this.type = type;
        this.name = name;

    }

    public String getName() {

        return this.name;
    }

    public Type getType() {

        return this.type;
    }

    @Override
    public ConcreteValue evaluateOn(ProgramState programState) {

        return programState.getVariableTarget(this.getName());
    }

    /**
     * sets the variable in programState to concreteTarget
     */
    @Override
    public void setValue(ProgramState programState, ConcreteValue concreteTarget) {

        programState.setVariable(this.getName(), concreteTarget);
    }

    @Override
    public boolean needsMaterialization(ProgramState programState) {

        return false;
    }


    public String toString() {

        return name;
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return ViolationPoints.getEmptyViolationPoints();
    }

}
