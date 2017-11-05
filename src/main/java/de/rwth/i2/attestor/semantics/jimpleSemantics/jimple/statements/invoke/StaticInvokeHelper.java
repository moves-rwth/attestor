package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsOptions;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Prepares the heap for the invoke of a static method and cleans it afterwards.
 * <br><br>
 * Call {@link #prepareHeap(ProgramState,SemanticsOptions) prepareHeap(input)} for the heap that initializes the method call
 * and {@link #cleanHeap(ProgramState,SemanticsOptions) cleanHeap( result )} on heaps that result from the execution of the abstract Method.<br>
 * <br>
 * Handles the evaluation of parameter expressions
 * and stores them in the heap, by setting the corresponding intermediates.<br>
 * Also manages the variable scope of the method and cleans the heap after the execution
 * of the method.
 *  
 * @author Hannah Arndt
 *
 */
public class StaticInvokeHelper extends InvokeHelper {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger( "StaticInvokeHelper" );

	/**
	 * creates a helper class for a specific invoke statement.
	 * 
	 * @see InvokeHelper
	 * 
	 * @param argumentValues
	 *            The values which form the arguments of the method in the
	 *            correct ordering
	 * @param namesOfLocals
	 *            The names of all locals which occur within the method (so they
	 *            can be removed afterwards).
	 */
	public StaticInvokeHelper( List<Value> argumentValues, List<String> namesOfLocals){

		super();
		this.argumentValues = argumentValues;
		this.namesOfLocals = namesOfLocals;
		
		precomputePotentialViolationPoints();

	}

	/**
	 * evaluates the expressions for the arguments and appends them to the heap.
	 * sets the current scope the the method's scope.
	 */
	@Override
	public void prepareHeap(ProgramState programState, SemanticsOptions options)
			throws NotSufficientlyMaterializedException{

		appendArguments(programState, options);

		programState.enterScope();
	}

	/**
	 * removes all remaining intermediates and local variables.
	 * leaves the method's scope.
	 */
	@Override
	public void cleanHeap(ProgramState programState, SemanticsOptions options){

		removeParameters(programState);
		removeLocals(programState);
		removeReturn(programState);

		programState.leaveScope();
	}

}
