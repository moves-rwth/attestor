package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import java.util.HashSet;
import java.util.Set;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleExecutable;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.JimpleUtil;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.iterator.TIntIterator;

/**
 * ReturnVoidStmt models the statement return;
 * 
 * @author Hannah Arndt
 *
 */
public class ReturnVoidStmt extends Statement {

	/**
	 * removes all locals of the current scope from the heap,
	 * and returns the resulting heap with exit location (-1)
	 */
	@Override
	public Set<ProgramState> computeSuccessors( ProgramState executable )
			throws NotSufficientlyMaterializedException{
		
		JimpleExecutable result = JimpleUtil.deepCopy( (JimpleExecutable) executable);

		// -1 since this statement has no successor location
		int nextPC = -1;
		result.setProgramCounter(nextPC);

		removeLocals( result );
		return SingleElementUtil.createSet( result );
	}

	@Override
	public boolean needsMaterialization( ProgramState executable ){
		return false;
	}

	public String toString(){
		return "return;";
	}

	@Override
	public boolean hasUniqueSuccessor() {
		
		return true;
	}
	
	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return ViolationPoints.getEmptyViolationPoints();
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
