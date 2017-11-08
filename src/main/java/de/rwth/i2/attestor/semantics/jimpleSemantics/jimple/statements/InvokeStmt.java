package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeCleanup;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeHelper;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsOptions;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import de.rwth.i2.attestor.util.SingleElementUtil;

import java.util.Set;

/**
 * InvokeStmt models statements like foo(); or bar(1,2);
 * 
 * @author Hannah Arndt
 *
 */
public class InvokeStmt extends Statement implements InvokeCleanup {

	/**
	 * the abstract representation of the called method
	 */
	private final AbstractMethod method;
	/**
	 * handles arguments, and if applicable the this-reference. Also manages the
	 * variable scope.
	 */
	private final InvokeHelper invokePrepare;
	/**
	 * the program location of the successor state
	 */
	private final int nextPC;

	public InvokeStmt( AbstractMethod method, InvokeHelper invokePrepare, int nextPC ){
		super();
		this.method = method;
		this.invokePrepare = invokePrepare;
		this.nextPC = nextPC;
	}

	/**
	 * gets the fixpoint from the
	 * {@link de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.SimpleAbstractMethod
	 * AbstractMethod} for the input heap and returns it for the successor
	 * location.<br>
	 * 
	 * If any variable appearing in the arguments is not live at this point,
	 * it will be removed from the heap to enable abstraction.
	 */
	@Override
	public Set<ProgramState> computeSuccessors(ProgramState programState, SemanticsOptions options)
			throws NotSufficientlyMaterializedException, StateSpaceGenerationAbortedException {

		options.update(this, programState);

		programState = programState.clone();

		invokePrepare.prepareHeap( programState, options );

		Set<ProgramState> methodResult = method.getResult(
				programState,
				options
		);

		methodResult.forEach( x -> invokePrepare.cleanHeap(x, options ) );
		methodResult.forEach( x -> x.clone() );
		methodResult.forEach( x -> x.setProgramCounter(nextPC) );
		
		return methodResult;
	}

	public ProgramState getCleanedResultState(ProgramState state, SemanticsOptions options)  {
		invokePrepare.cleanHeap(state, options);
		return state;
	}

	@Override
	public boolean needsMaterialization( ProgramState programState ){
		return invokePrepare.needsMaterialization( programState );
	}


	public String toString(){
		return method.toString() + "(" + invokePrepare.argumentString() + ");";
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
