package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.*;
import gnu.trove.list.array.TIntArrayList;

/**
 * Determines the whether the current candidate pair cannot belong to a graph morphism due to a mismatch
 * in the ingoing lookahead sets in the successors or predecessors of one of the candidate nodes.
 * This set corresponds to the number of nodes in the predecessors or successors of a candidate node
 * that have not been matched yet, but that are reachable from the candidate node via a single ingoing edge.
 * A mismatch may either consist of less ingoing edges in the target than in the pattern (checkEqualityOnExternal=false)
 * or an unequal number (checkEqualityOnExternal=true).
 *
 * @author Christoph
 */
public class OneStepLookaheadIn implements FeasibilityFunction {

    /**
     * Determines whether the ingoing lookahead sets of pattern and target
     * have to be equal or target sets are allowed to be larger.
     */
    private final boolean checkEqualityOnExternal;

    /**
     * @param checkEqualityOnExternal Determines whether equal lookahead sets are required.
     */
    public OneStepLookaheadIn(boolean checkEqualityOnExternal) {

        this.checkEqualityOnExternal = checkEqualityOnExternal;
    }

    @Override
    public boolean eval(VF2State state, int p, int t) {

        VF2GraphData pattern = state.getPattern();
        Graph patternGraph = pattern.getGraph();
        VF2GraphData target = state.getTarget();
        Graph targetGraph = target.getGraph();

        int patternSucc = computeLookahead(
                patternGraph.getSuccessorsOf(p),
                pattern
        );

        int targetSucc = computeLookahead(
                targetGraph.getSuccessorsOf(t),
                target
        );

        if (checkEqualityOnExternal) {
            if (targetSucc != patternSucc) {
                return false;
            }
        } else {
            if (targetSucc < patternSucc) {
                return false;
            }
        }

        int patternPred = computeLookahead(
                patternGraph.getPredecessorsOf(p),
                pattern
        );

        int targetPred = computeLookahead(
                targetGraph.getPredecessorsOf(t),
                target
        );

        if (checkEqualityOnExternal) {
            return (targetPred == patternPred);
        } else {
            return (targetPred >= patternPred);
        }
    }


    /**
     * Computes the ingoing lookahead set for the given set of nodes connected to the considered candidate node.
     *
     * @param neighbors The nodes connected to the considered node.
     * @param data      Matching data stored for the graph corresponding to the nodes in neighbors.
     * @return The number of nodes in neighbors that have not been matched yet, but that are reachable via a single
     * ingoing edge from the candidate node.
     */
    private int computeLookahead(TIntArrayList neighbors, AbstractVF2GraphData data) {

        int lookaheadIn = 0;
        for (int i = 0; i < neighbors.size(); i++) {
            int next = neighbors.get(i);

            if (data.containsIngoingUnmatched(next)) {
                ++lookaheadIn;
            }
        }

        return lookaheadIn;
    }

}
