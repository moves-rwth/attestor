package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.JimpleUtil;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsOptions;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.strategies.VariableScopes;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.iterator.TIntIterator;

import java.util.HashSet;
import java.util.Set;

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
	public Set<ProgramState> computeSuccessors(ProgramState programState, SemanticsOptions options)
			throws NotSufficientlyMaterializedException{
		
		JimpleProgramState result = JimpleUtil.deepCopy( (JimpleProgramState) programState);

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
	 * @param programState The programState whose local variables should be removed.
	 */
	private void removeLocals( JimpleProgramState programState ){
		int scope = programState.getScopeDepth();
		HeapConfiguration heap = programState.getHeap();
		HeapConfigurationBuilder builder = heap.builder();
		
		TIntIterator iter = heap.variableEdges().iterator();
		
		while(iter.hasNext()) {
			int var = iter.next();
			String name = heap.nameOf(var);
			if(VariableScopes.hasScope(name, scope)) {
				builder.removeVariableEdge(var);
			}
		}
		builder.build();
	}

}
