package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


/**
 * Prepares the heap for the invoke of a static method and cleans it afterwards.
 * <br><br>
 * Call {@link #prepareHeap(ProgramState) prepareHeap(input)} for the heap that initializes the method call
 * and {@link #cleanHeap(ProgramState) cleanHeap( result )} on heaps that result from the execution of the abstract Method.<br>
 * <br>
 * Handles the evaluation of parameter expressions
 * and stores them in the heap, by setting the corresponding intermediates.<br>
 * Cleans the heap after the execution of the method.
 *
 * @author Hannah Arndt
 */
public class StaticInvokeHelper extends InvokeHelper {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger("StaticInvokeHelper");

    /**
     * creates a helper class for a specific invoke statement.
     *
     * @param argumentValues The values which form the arguments of the method in the
     *                       correct ordering
     * @see InvokeHelper
     */
    public StaticInvokeHelper(SceneObject sceneObject, List<Value> argumentValues) {

        super(sceneObject);
        this.argumentValues = argumentValues;

        precomputePotentialViolationPoints();

    }

    /**
     * evaluates the expressions for the arguments and appends them to the heap.
     */
    @Override
    public void prepareHeap(ProgramState programState) {

        appendArguments(programState);
    }

    /**
     * removes all remaining intermediates and local variables.
     */
    @Override
    public void cleanHeap(ProgramState programState) {

        removeParameters(programState);
        //removeLocals(programState);
        removeReturn(programState);
    }

    @Override
    public String baseValueString() {

        return "";
    }

}
