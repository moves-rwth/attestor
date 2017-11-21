package de.rwth.i2.attestor.refinement.garbageCollection;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.util.ReachabilityChecker;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GarbageCollector implements StateRefinementStrategy {

    private final static Logger logger = LogManager.getLogger("GarbageCollector");

    @Override
    public ProgramState refine(Semantics semantics, ProgramState state) {

        HeapConfiguration heap = state.getHeap();
        ReachabilityChecker checker = new ReachabilityChecker(
                state.getHeap(),
                getVariableTargetNodes(heap)
        );

        TIntSet unreachableNodes = checker.getUnreachableNodes();
        TIntIterator unreachableIterator = unreachableNodes.iterator();
        HeapConfigurationBuilder builder = heap.builder();
        while(unreachableIterator.hasNext()) {
            int node = unreachableIterator.next();
            builder.removeNode(node);
        }

        logger.debug("removed " + unreachableNodes.size() + " unreachable nodes.");
        logger.trace(unreachableNodes);

        return state;

    }

    TIntSet getVariableTargetNodes(HeapConfiguration heap) {

        TIntSet variableTargets = new TIntHashSet(heap.countVariableEdges());
        TIntIterator variableIterator = heap.variableEdges().iterator();
        while(variableIterator.hasNext()) {
            int varEdge = variableIterator.next();
            variableTargets.add(heap.targetOf(varEdge));
        }
        return variableTargets;
    }
}
