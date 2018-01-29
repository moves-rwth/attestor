package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import gnu.trove.iterator.TIntIterator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ReturnVoidStmt models the statement return;
 *
 * @author Hannah Arndt
 */
public class ReturnVoidStmt extends Statement {

    public ReturnVoidStmt(SceneObject sceneObject) {

        super(sceneObject);
    }

    /**
     * Returns the resulting heap with exit location (-1)
     */
    @Override
    public Collection<ProgramState> computeSuccessors(ProgramState programState) {

        programState = programState.clone();

        // -1 since this statement has no successor location
        int nextPC = -1;
        programState.setProgramCounter(nextPC);

        removeLocals(programState);
        return Collections.singleton(programState);
    }

    public String toString() {

        return "return;";
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return ViolationPoints.getEmptyViolationPoints();
    }

    @Override
    public Set<Integer> getSuccessorPCs() {

        return new LinkedHashSet<>();
    }

    @Override
    public boolean needsCanonicalization() {
        return true;
    }

    /**
     * Removes local variables from the current block.
     *
     * @param programState The programState whose local variables should be removed.
     */
    private void removeLocals(ProgramState programState) {

        HeapConfiguration heap = programState.getHeap();
        HeapConfigurationBuilder builder = heap.builder();

        TIntIterator iter = heap.variableEdges().iterator();

        while (iter.hasNext()) {
            int var = iter.next();
            String name = heap.nameOf(var);
            if (!(Markings.isMarking(name) || Constants.isConstant(name) || name.startsWith("@return"))) {
                builder.removeVariableEdge(var);
            }
        }
        builder.build();
    }

}
