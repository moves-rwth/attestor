package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleExecutable;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.JimpleUtil;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.*;
import gnu.trove.iterator.TIntIterator;

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

	/**
	 * evaluates the return expression and attaches an intermediate "return" to
	 * the result element (i.e. node).
	 * Then, removes all locals of the current scope from the heap,
	 * and returns the resulting heap with exit location (-1)
	 */
	
	@Override
	public Set<ProgramState> computeSuccessors( ProgramState state )
			throws NotSufficientlyMaterializedException{
		
		JimpleExecutable executable = JimpleUtil.deepCopy( (JimpleExecutable) state);

		ConcreteValue concreteReturn;
		try {
			concreteReturn = returnValue.evaluateOn( executable );
		} catch (NullPointerDereferenceException e) {
			logger.error(e.getErrorMessage(this));
			concreteReturn = executable.getUndefined();
		}
		if( DebugMode.ENABLED && !( concreteReturn.type().equals( expectedType ) ) ){
			logger.warn( "type missmatch. Expected: " + expectedType + " got: " + concreteReturn.type() );
		}

		if( concreteReturn.isUndefined() ){
			if( DebugMode.ENABLED ){
				logger.warn( "return value evaluated to undefined. No return will be attached" );
			}
		}else{
			executable.setIntermediate( "@return", concreteReturn );
		}

	  	// -1 since this statement has no successor location
		int nextPC = -1;
		executable.setProgramCounter(nextPC);
		
		removeLocals( executable );
		return SingleElementUtil.createSet( executable );
	}

	@Override
	public boolean needsMaterialization( ProgramState executable ){
		return returnValue.needsMaterialization( (JimpleExecutable) executable );
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
     * @param state The state whose local variables should be removed.
     */
	private void removeLocals( JimpleExecutable state ){
		int scope = state.getScopeDepth();
		HeapConfiguration heap = state.getHeap();
		
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
