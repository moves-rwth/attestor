package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullPointerDereferenceException;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.semantics.util.DeadVariableEliminator;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Prepares the heap for the invoke of an instance method and cleans it afterwards.
 * <br><br>
 * Call {@link #prepareHeap(ProgramState) prepareHeap(input)} for the heap that initializes the method call
 * and {@link #cleanHeap(ProgramState) cleanHeap( result )} on heaps that result from the execution of the abstract Method.<br>
 * <br>
 * Handles the evaluation of parameter and this expressions
 * and stores them in the heap, by setting the corresponding intermediates.<br>
 * Cleans the heap after the execution of the method.
 *
 * @author Hannah Arndt
 */
public class InstanceInvokeHelper extends InvokeHelper {

    private static final Logger logger = LogManager.getLogger("InstanceInvokePrepare");

    /**
     * the value on which the method is called (i.e. "this")
     */
    private final Value baseValue;


    /**
     * creates a helper class for a specific invoke statement.
     *
     * @param baseValue      the value on which the method is called (i.e. "this")
     * @param argumentValues the values which form the arguments of the method in the
     *                       correct ordering
     */
    public InstanceInvokeHelper(SceneObject sceneObject, Value baseValue, List<Value> argumentValues) {

        super(sceneObject);
        this.baseValue = baseValue;
        this.argumentValues = argumentValues;

        precomputePotentialViolationPoints();
        getPotentialViolationPoints().addAll(baseValue.getPotentialViolationPoints());
    }

    /**
     * remove any intermediates that are still present in the heap. <br>
     * leave the scopes of the method.
     */
    @Override
    public void cleanHeap(ProgramState programState) {

        programState.removeIntermediate("@this:");
        removeParameters(programState);
        //removeLocals( programState );
        removeReturn(programState);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.
     * InvokePrepare#prepareHeap(de.rwth.i2.attestor.semantics.jimpleSemantics.
     * JimpleProgramState)
     */
    @Override
    public void prepareHeap(ProgramState programState) {

        ConcreteValue concreteBase;
        try {
            concreteBase = baseValue.evaluateOn(programState);
        } catch (NullPointerDereferenceException e) {
            logger.error(e.getErrorMessage(this));
            concreteBase = programState.getUndefined();
        }
        if (concreteBase.isUndefined()) {
            logger.debug("base evaluated to undefined and is therefore not attached. ");
        } else {
            // String type = " " + baseValue.getType().toString();
            String type = "";
            programState.setIntermediate("@this:" + type, concreteBase);
            if (scene().options().isRemoveDeadVariables()) {
                DeadVariableEliminator.removeDeadVariables(this, baseValue.toString(), programState, liveVariableNames);
            }
        }

        appendArguments(programState);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.
     * InvokePrepare#needsMaterialization(de.rwth.i2.attestor.symbolicExecution.
     * AbstractHeap)
     */
    @Override
    public boolean needsMaterialization(ProgramState programState) {

        return super.needsMaterialization(programState) || baseValue.needsMaterialization(programState);
    }

    @Override
    public String baseValueString() {

        return this.baseValue.toString() + ".";
    }


}
