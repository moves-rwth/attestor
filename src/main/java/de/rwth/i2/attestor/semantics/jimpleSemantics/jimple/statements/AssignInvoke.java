package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeCleanup;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullPointerDereferenceException;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.SettableValue;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsOptions;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import de.rwth.i2.attestor.util.SingleElementUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * AssignInvoke models statements of the form x = foo(); or x = bar(3, name);
 * @author Hannah Arndt
 *
 */
public class AssignInvoke extends Statement implements InvokeCleanup {

	private static final Logger logger = LogManager.getLogger( "AssignInvoke" );

	/**
	 * the value to which the result will be assigned
	 */
	private final SettableValue lhs;
	/**
	 * the abstract translation of the method that is called
	 */
	private final AbstractMethod method;
	/**
	 * handles arguments, and if applicable the this-reference.
	 * Also manages the variable scope.
	 */
	private final InvokeHelper invokePrepare;
	/**
	 * the program counter of the successor statement
	 */
	private final int nextPC;

	public AssignInvoke( SettableValue lhs, AbstractMethod method, InvokeHelper invokePrepare,
			int nextPC ){
		super();
		this.lhs = lhs;
		this.method = method;
		this.invokePrepare = invokePrepare;
		this.nextPC = nextPC;
	}

	/**
	 * gets the fixpoint of the abstract method for the given input.
	 * For each of the resulting heaps, retrieves the return argument and 
	 * creates a new heap where it is assigned correctly.
	 * If a result is lacking a return, it is ignored.<br>
	 * 
	 * If any variable appearing in the arguments is not live at this point,
	 * it will be removed from the heap to enable abstraction. Furthermore,
	 * if lhs is a variable it will be removed before invoking the function,
	 * as it is clearly not live at this point.
	 */
	@Override
	public Set<ProgramState> computeSuccessors(ProgramState programState, SemanticsOptions options)
			throws NotSufficientlyMaterializedException, StateSpaceGenerationAbortedException {

		options.update(this, programState);

		programState = programState.clone();
		invokePrepare.prepareHeap( programState, options );
		
		if( lhs instanceof Local ){
			programState.leaveScope();
			programState.removeVariable( ((Local)lhs).getName() );
			programState.enterScope();
		}

		Set<ProgramState> methodResult = method.getResult(
				programState,
				options
		);

		Set<ProgramState> assignResult = new HashSet<>();
		for( ProgramState resState : methodResult ) {

			resState = getCleanedResultState(resState, options);
			ProgramState freshState = resState.clone();
			freshState.setProgramCounter(nextPC);
			assignResult.add( freshState );
		}
				
		return assignResult;
	}

	public ProgramState getCleanedResultState(ProgramState state, SemanticsOptions options)
			throws NotSufficientlyMaterializedException {

		ConcreteValue concreteRHS = state.removeIntermediate( "@return" );
		invokePrepare.cleanHeap( state, options );

		try {
			lhs.setValue(state, concreteRHS );
		} catch (NullPointerDereferenceException e) {
			logger.error(e.getErrorMessage(this));
		}

		return state;

	}

	@Override
	public boolean needsMaterialization( ProgramState programState ){
		return invokePrepare.needsMaterialization( programState );
	}

	public String toString(){
		String res = lhs.toString() + " = " + method.toString() + "(";
		res += invokePrepare.argumentString();
		res += ");";
		return res;
	}

	@Override
	public boolean hasUniqueSuccessor() {

		return false;
	}
	
	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return invokePrepare.getPotentialViolationPoints();
	}

	@Override
	public Set<Integer> getSuccessorPCs() {
		
		return SingleElementUtil.createSet(nextPC);
	}
	
}
