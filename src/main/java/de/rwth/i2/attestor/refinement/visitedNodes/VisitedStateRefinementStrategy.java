package de.rwth.i2.attestor.refinement.visitedNodes;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.iterator.TIntIterator;

/**
 *
 * A state refinement strategy that marks every node in a heap configuration that is the target of a program
 * variable as "visited". Previously marked nodes remain marked.
 *
 * @author Christoph
 */
public class VisitedStateRefinementStrategy implements StateRefinementStrategy {

    @Override
    public ProgramState refine(ProgramState state) {

        HeapConfiguration heapConf = state.getHeap();
        TIntIterator varIter = heapConf.variableEdges().iterator();
        while(varIter.hasNext()) {
            int var = varIter.next();
            int node = heapConf.targetOf(var);
            Type type = heapConf.nodeTypeOf(node);
            if(!VisitedTypeHelper.isVisited(type)) {
                Type newType = VisitedTypeHelper.getVisitedType(type);
                heapConf.builder().replaceNodeType(node, newType).build();
            }
        }
        return state;
    }
}
