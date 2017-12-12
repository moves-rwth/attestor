package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.Graph;
import gnu.trove.list.array.TIntArrayList;

/**
 * Checks whether all already matched successors of the pattern candidate node are matched to predecessors
 * the the target candidate node.
 * Alternatively, this class can also check whether both nodes have the same successors if
 * the flag checkEqualityOnExternal is set.
 *
 * @author Christoph
 */
public class CompatibleSuccessors extends AbstractCompatibleNeighbours {

    /**
     * @param checkEqualityOnExternal True if and only if exactly the same successors are required.
     */
    public CompatibleSuccessors(boolean checkEqualityOnExternal) {

        super(checkEqualityOnExternal);
    }

    @Override
    protected TIntArrayList getAdjacent(Graph graph, int node) {

        return graph.getSuccessorsOf(node);
    }


}
