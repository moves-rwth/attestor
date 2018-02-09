package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullPointerDereferenceException;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.iterator.TIntIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ReturnValue models statements like return x;
 *
 * @author Hannah Arndt
 */
public class ReturnValueStmt extends Statement {

    private static final Logger logger = LogManager.getLogger("ReturnValue");

    /**
     * the expression for the value that will be returned
     */
    private final Value returnValue;

    /**
     * The return type.
     */
    private final Type expectedType;

    public ReturnValueStmt(SceneObject sceneObject, Value returnValue, Type type) {

        super(sceneObject);
        this.returnValue = returnValue;
        this.expectedType = type;
    }

    @Override
    public Collection<ProgramState> computeSuccessors(ProgramState programState) {

        programState = programState.clone();

        ConcreteValue concreteReturn;
        try {
            concreteReturn = returnValue.evaluateOn(programState);
        } catch (NullPointerDereferenceException e) {
            logger.error(e.getErrorMessage(this));
            concreteReturn = programState.getUndefined();
        }
        if (!(concreteReturn.type().equals(expectedType))) {
            logger.debug("type mismatch. Expected: " + expectedType + " got: " + concreteReturn.type());
        }

        if (concreteReturn.isUndefined()) {
            logger.warn("return value evaluated to undefined. No return will be attached");
        } else {
            programState.setIntermediate("@return", concreteReturn);
        }

        // -1 since this statement has no successor location
        int nextPC = -1;
        programState.setProgramCounter(nextPC);
        removeLocals(programState);
        return Collections.singleton(programState);
    }

    public boolean needsMaterialization(ProgramState programState) {

        return returnValue.needsMaterialization(programState);
    }

    public String toString() {

        return "return " + returnValue + ";";
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return returnValue.getPotentialViolationPoints();
    }

    @Override
    public Set<Integer> getSuccessorPCs() {

        return new LinkedHashSet<>();
    }

    @Override
    public boolean needsCanonicalization() {
        return true;
    }

    /**
     * Removes local variables from the current block.
     *
     * @param programState The programState whose local variables should be removed.
     */
    private void removeLocals(ProgramState programState) {

        HeapConfiguration heap = programState.getHeap();
        HeapConfigurationBuilder builder = heap.builder();

        TIntIterator iter = heap.variableEdges().iterator();

        while (iter.hasNext()) {
            int var = iter.next();
            String name = heap.nameOf(var);
            if (!(Markings.isMarking(name) || Constants.isConstant(name) || name.startsWith("@return"))) {
                builder.removeVariableEdge(var);
            }
        }

        builder.build();
    }

}
