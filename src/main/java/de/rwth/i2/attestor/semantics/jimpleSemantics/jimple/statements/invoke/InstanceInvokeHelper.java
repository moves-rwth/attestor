package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullPointerDereferenceException;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.util.DebugMode;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Prepares the heap for the invoke of an instance method and cleans it afterwards.
 * <br><br>
 * Call {@link #prepareHeap(JimpleProgramState) prepareHeap(input)} for the heap that initializes the method call
 * and {@link #cleanHeap(JimpleProgramState) cleanHeap( result )} on heaps that result from the execution of the abstract Method.<br>
 * <br>
 * Handles the evaluation of parameter and this expressions
 * and stores them in the heap, by setting the corresponding intermediates.<br>
 * Also manages the variable scope of the method and cleans the heap after the execution
 * of the method.
 *  
 * @author Hannah Arndt
 *
 */
public class InstanceInvokeHelper extends InvokeHelper {

	private static final Logger logger = LogManager.getLogger( "InstanceInvokePrepare" );

	/**
	 * the value on which the method is called (i.e. "this")
	 */
	private final Value baseValue;

	/**
	 * creates a helper class for a specific invoke statement.
	 * 
	 * @see InvokeHelper
	 * 
	 * @param baseValue
	 *            the value on which the method is called (i.e. "this")
	 * @param argumentValues
	 *            the values which form the arguments of the method in the
	 *            correct ordering
	 * @param namesOfLocals
	 *            the names of all locals which occur within the method (so they
	 *            can be removed afterwards).
	 */
	public InstanceInvokeHelper( Value baseValue, List<Value> argumentValues,
								 List<String> namesOfLocals, boolean removeDeadVariables ){

		super(removeDeadVariables);
		this.baseValue = baseValue;
		this.argumentValues = argumentValues;
		this.namesOfLocals = namesOfLocals;
		
		precomputePotentialViolationPoints();
		getPotentialViolationPoints().addAll(baseValue.getPotentialViolationPoints());
	}

	/**
	 * remove any intermediates that are still present in the heap. <br>
	 * remove all local variables from the scope of this method from the heap <br>
	 * leave the scope of the method.
	 */
	@Override
	public void cleanHeap( JimpleProgramState programState ){

		programState.removeIntermediate( "@this:" );
		removeParameters( programState );
		removeLocals( programState );
		removeReturn( programState );

		programState.leaveScope();
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
	public void prepareHeap( JimpleProgramState programState ) throws NotSufficientlyMaterializedException{

		ConcreteValue concreteBase;
		try {
			concreteBase = baseValue.evaluateOn( programState );
		} catch (NullPointerDereferenceException e) {
			logger.error(e.getErrorMessage(this));
			concreteBase = programState.getUndefined();
		}
		if( concreteBase.isUndefined() ){
			if( DebugMode.ENABLED ){
				logger.warn( "base evaluated to undefined and is therefore not attached. " );
			}
		}else{
			// String type = " " + baseValue.getType().toString();
			String type = "";
			programState.setIntermediate( "@this:" + type, concreteBase );
		}

		appendArguments( programState );

		programState.enterScope();

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
	public boolean needsMaterialization( JimpleProgramState programState ){
		return super.needsMaterialization(programState) || baseValue.needsMaterialization( programState );
	}


}
