package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.JimpleUtil;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullPointerDereferenceException;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.iterator.TIntIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * ReturnValue models statements like return x;
 * 
 * @author Hannah Arndt
 *
 */
public class ReturnValueStmt extends Statement {

	private static final Logger logger = LogManager.getLogger( "ReturnValue" );

	/**
	 * the expression for the value that will be returned
	 */
	private final Value returnValue;

	/**
	 * The return type.
	 */
	private final Type expectedType;

	public ReturnValueStmt( Value returnValue, Type type ){
		super();
		this.returnValue = returnValue;
		this.expectedType = type;
	}

	@Override
	public Set<ProgramState> computeSuccessors( ProgramState programState )
			throws NotSufficientlyMaterializedException{
		
		JimpleProgramState jimpleProgramState = JimpleUtil.deepCopy( (JimpleProgramState) programState);

		ConcreteValue concreteReturn;
		try {
			concreteReturn = returnValue.evaluateOn( jimpleProgramState );
		} catch (NullPointerDereferenceException e) {
			logger.error(e.getErrorMessage(this));
			concreteReturn = jimpleProgramState.getUndefined();
		}
		if( !( concreteReturn.type().equals( expectedType ) ) ){
			logger.debug( "type mismatch. Expected: " + expectedType + " got: " + concreteReturn.type() );
		}

		if( concreteReturn.isUndefined() ){
			logger.warn( "return value evaluated to undefined. No return will be attached" );
		}else{
			jimpleProgramState.setIntermediate( "@return", concreteReturn );
		}

	  	// -1 since this statement has no successor location
		int nextPC = -1;
		jimpleProgramState.setProgramCounter(nextPC);
		
		removeLocals( jimpleProgramState );
		return SingleElementUtil.createSet( jimpleProgramState );
	}

	@Override
	public boolean needsMaterialization( ProgramState programState ){
		return returnValue.needsMaterialization( (JimpleProgramState) programState );
	}

	public String toString(){
		return "return " + returnValue + ";";
	}

	@Override
	public boolean hasUniqueSuccessor() {
		
		return true;
	}
	
	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return returnValue.getPotentialViolationPoints();
	}
	
	@Override
	public Set<Integer> getSuccessorPCs() {
		
		return new HashSet<>();
	}

    /**
     * Removes local variables from the current block.
     * @param programState The programState whose local variables should be removed.
     */
	private void removeLocals( JimpleProgramState programState ){
		int scope = programState.getScopeDepth();
		HeapConfiguration heap = programState.getHeap();
		
		TIntIterator iter = heap.variableEdges().iterator();
		
		String prefix = scope + "-";
		
		while(iter.hasNext()) {
			int var = iter.next();
			String name = heap.nameOf(var);
			if(name.startsWith(prefix)) {
				heap.builder().removeVariableEdge(var).build();
			}
		}
	}

}
