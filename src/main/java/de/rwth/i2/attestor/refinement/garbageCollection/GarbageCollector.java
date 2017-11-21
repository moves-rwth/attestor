package de.rwth.i2.attestor.refinement.garbageCollection;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.util.ReachabilityChecker;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.AssignInvoke;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.AssignStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.InvokeStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class GarbageCollector implements StateRefinementStrategy {

    private final static Logger logger = LogManager.getLogger("GarbageCollector");

    private final static Set<Class<?>> semanticsTriggeringGarbageCollector = new HashSet<>();

    static {
        semanticsTriggeringGarbageCollector.add(InvokeStmt.class);
        semanticsTriggeringGarbageCollector.add(AssignStmt.class);
        semanticsTriggeringGarbageCollector.add(AssignInvoke.class);
    }

    @Override
    public ProgramState refine(Semantics semantics, ProgramState state) {

        // If the previously executed program statement cannot alter the heap
        // there is no reason to invoke the garbage collection
        if(!semanticsTriggeringGarbageCollector.contains(semantics.getClass())) {
           return state;
        }

        HeapConfiguration heap = state.getHeap();
        ReachabilityChecker checker = new ReachabilityChecker(
                state.getHeap(),
                getVariableTargetNodes(heap)
        );

        TIntSet unreachableNodes = checker.getUnreachableNodes();

        if(unreachableNodes.isEmpty()) {
            return state;
        }

        TIntIterator unreachableIterator = unreachableNodes.iterator();
        HeapConfigurationBuilder builder = heap.builder();
        while(unreachableIterator.hasNext()) {
            int node = unreachableIterator.next();
            builder.removeNode(node);
        }
        builder.build();

        state.addAP("{ garbage collected }");
        logger.debug("removed " + unreachableNodes.size() + " unreachable nodes.");
        logger.trace(unreachableNodes);

        return state;

    }

    private TIntSet getVariableTargetNodes(HeapConfiguration heap) {

        TIntSet variableTargets = new TIntHashSet(heap.countVariableEdges());
        TIntIterator variableIterator = heap.variableEdges().iterator();
        while(variableIterator.hasNext()) {
            int varEdge = variableIterator.next();
            variableTargets.add(heap.targetOf(varEdge));
        }
        return variableTargets;
    }
}
