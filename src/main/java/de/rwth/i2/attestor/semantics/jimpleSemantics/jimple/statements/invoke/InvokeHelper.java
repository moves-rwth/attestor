package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import java.util.*;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.VariablesUtil;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * An instance of this class is a helper for a specific invoke statement. It can
 * be used to prepare the heap before the invoked method is executed, to add
 * variables referencing this and the arguments of the method. After the
 * execution of the method it can be used to remove all variables that should
 * only be visible inside the method, e.g. references which the method did not
 * remove and its local variables.
 * <br><br>
 * Call {@link #prepareHeap(ProgramState, SemanticsObserver) prepareHeap(input)} for the heap that initializes the method call
 * and {@link #cleanHeap(ProgramState, SemanticsObserver) cleanHeap( result )} on heaps that result from the execution of the abstract Method.<br>
 * 
 * @author Hannah Arndt
 *
 */
public abstract class InvokeHelper {

	private static final Logger logger = LogManager.getLogger( "InvokePrepare" );

	/**
	 * a list with the expressions for the arguments in the correct order
	 */
    List<Value> argumentValues;
	/**
	 * the names of all locals that occur in this method.
	 * Necessary to remove them when the scope is left.
	 */
    List<String> namesOfLocals;

    /**
     * All (variable,selector) pairs that might require materialization before this statement can be executed.
     */
	private final ViolationPoints potentialViolationPoints;

    /**
     * The live variables for this statement.
     */
	protected Set<String> liveVariableNames = new HashSet<>();

	/**
	 * Increases the scope
	 * 
	 * Attaches method parameters and if need be this to the programState for use
	 * in the method.
	 * 
	 * Exceptions may occur in the evaluation of the arguments or the calling
	 * Value cause them.
	 * 
	 * @param programState
	 *            : the programState which will be the input of the called method
	 * @param options Current configuration of the symbolic execution that uses this InvokeHelper.
	 * @exception NotSufficientlyMaterializedException if the argument or the base value
	 * can not be evaluated on the heap before it is materialized.
	 */
	public abstract void prepareHeap(ProgramState programState, SemanticsObserver options )
			throws NotSufficientlyMaterializedException;

	/**
	 * Decreases the scope
	 * 
	 * Removes unused intermediates (this, params, return) and local variables
	 * from the programState.
	 * 
	 * @param programState
	 *            after execution of method (potentially wiht unused
	 *            intermediates and local variables)
	 * @param options Current configuration of the symbolic execution that uses this InvokeHelper.
	 */
	public abstract void cleanHeap( ProgramState programState, SemanticsObserver options );
	
	InvokeHelper() {
		potentialViolationPoints = new ViolationPoints();
	}
	
	void precomputePotentialViolationPoints() {
		for( Value argument : argumentValues ){
			
			potentialViolationPoints.addAll(argument.getPotentialViolationPoints());
		}
	}

	public boolean needsMaterialization( ProgramState programState ){
		boolean res = false;
		for( Value argument : argumentValues ){
			res = res || argument.needsMaterialization(programState);
		}
		return res;
	}


	void appendArguments(ProgramState programState, SemanticsObserver options)
			throws NotSufficientlyMaterializedException {

		for( int i = 0; i < argumentValues.size(); i++ ){
			// String name = "@parameter"+i+": "+arguments.get(i).getType();
			String referenceName = "@parameter" + i + ":";
			
			ConcreteValue concreteArgument;
			try {
				concreteArgument = argumentValues.get( i ).evaluateOn( programState );
			} catch (NullPointerDereferenceException e) {
				logger.error(e.getErrorMessage(this));
				concreteArgument = programState.getUndefined();
			}
			if( concreteArgument.isUndefined() ){
				logger.warn( "param " + i + " evaluated to undefined and is therefore not attached. " );
			}else{
				programState.setIntermediate( referenceName, concreteArgument );
			}

			if(options.isDeadVariableEliminationEnabled()) {
				VariablesUtil.removeDeadVariables( argumentValues.get( i ).toString(), programState, liveVariableNames );
			}
		}
	}

	/**
	 * removes all locals from the scope of the method from the programState
	 * @param programState the programState at the end of the method
	 */
    void removeLocals(ProgramState programState){
		for( String varName : namesOfLocals ){
			programState.removeVariable( varName );
		}
	}

	/**
	 * removes the parameters (intermediates) from the programState
	 * @param programState the programState at the end of the method
	 */
    void removeParameters(ProgramState programState){
		for( int i = 0; i < this.argumentValues.size(); i++ ){
			programState.removeIntermediate( "@parameter" + i + ":" );
		}
	}

	/**
	 * removes the return intermediate in case it was not used by an AssignInvokeStmt
	 * @param programState the heap from which the parameters are removed
	 */
    void removeReturn(ProgramState programState){
		programState.removeIntermediate( "@return" );
	}

	/**
	 * separates the arguments by ,
	 * @return arg1,arg2,...,argN
	 */
	public String argumentString(){
		StringBuilder res = new StringBuilder();
		for( Value arg : argumentValues ){
			res.append(arg.toString()).append(",");
		}
		if( res.length() > 0 ){
			res = new StringBuilder(res.substring(0, res.length() - 1));
		}
		return res.toString();
	}

	/**
	 * @return The potential (variable,selector) fields that might require materialization before executing
	 * 		   this statement.
	 */
	public ViolationPoints getPotentialViolationPoints() {
		
		return potentialViolationPoints;
	}

    /**
     * Specifies the live variables for this program location.
     * @param liveVariableNames Set of live variable names.
     */
	public void setLiveVariableNames( Set<String> liveVariableNames ){
		this.liveVariableNames = liveVariableNames;
	}

}