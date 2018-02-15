package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;


import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullPointerDereferenceException;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.SettableValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.semantics.util.DeadVariableEliminator;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.util.SingleElementUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Set;

/**
 * AssignStmts model assignments of locals or fields to values e.g. x.y = z
 *
 * @author hannah
 */
public class AssignStmt extends Statement {

    private static final Logger logger = LogManager.getLogger("AssignStmt");
    /**
     * the element to which something will be assigned (e.g. variable or field)
     */
    private final SettableValue lhs;
    /**
     * The expression that will be assigned
     */
    private final Value rhs;
    /**
     * the program counter of the successor state
     */
    private final int nextPC;

    private final ViolationPoints potentialViolationPoints;

    private final Set<String> liveVariableNames;

    public AssignStmt(SceneObject sceneObject, SettableValue lhs, Value rhs, int nextPC, Set<String> liveVariableNames) {

        super(sceneObject);
        this.rhs = rhs;
        this.lhs = lhs;
        this.nextPC = nextPC;
        this.liveVariableNames = liveVariableNames;

        potentialViolationPoints = new ViolationPoints();
        potentialViolationPoints.addAll(lhs.getPotentialViolationPoints());
        potentialViolationPoints.addAll(rhs.getPotentialViolationPoints());


    }

    /**
     * evaluates the rhs and assigns it to the left hand side. In case the rhs
     * evaluates to undefined, the variable will be removed from the heap (It
     * will not point to its old value). <br>
     * If the types of the lhs and the rhs do not match, there will be a
     * warning, but the assignment will still be realized.<br>
     * <p>
     * If the variable in rhs is not live in this statement, it will be removed from the heap
     * to enable abstraction at this point.
     */
    @Override
    public Collection<ProgramState> computeSuccessors(ProgramState programState) {

        programState = programState.clone();
        ConcreteValue concreteRHS;

        try {
            concreteRHS = rhs.evaluateOn(programState);
        } catch (NullPointerDereferenceException e) {
            logger.error(e.getErrorMessage(this));
            concreteRHS = programState.getUndefined();
        }

        try {
            lhs.evaluateOn(programState); // enforce materialization if necessary
            lhs.setValue(programState, concreteRHS);
        } catch (NullPointerDereferenceException e) {
            logger.error(e.getErrorMessage(this));
        }

        if (scene().options().isRemoveDeadVariables()) {
            DeadVariableEliminator.removeDeadVariables(this, rhs.toString(),
                    programState, liveVariableNames);

            DeadVariableEliminator.removeDeadVariables(this, lhs.toString(),
                    programState, liveVariableNames);
        }

        ProgramState result = programState.clone();
        result.setProgramCounter(nextPC);

        return SingleElementUtil.createSet(result);
    }


    public String toString() {

        return lhs.toString() + " = " + rhs.toString() + ";";
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return potentialViolationPoints;
    }

    @Override
    public Set<Integer> getSuccessorPCs() {

        return SingleElementUtil.createSet(nextPC);
    }

    @Override
    public boolean needsCanonicalization() {
        return false;
    }

}
