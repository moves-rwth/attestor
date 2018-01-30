package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.*;
import gnu.trove.list.array.TIntArrayList;

/**
 * Determines the whether the current candidate pair cannot belong to a graph morphism due to a mismatch
 * in the lookahead sets in the successors or predecessors of one of the candidate nodes.
 * This set corresponds to the number of nodes in the predecessors or successors of a candidate node
 * that have not been matched yet.
 * A mismatch may either consist of less ingoing edges in the target than in the pattern (checkEqualityOnExternal=false)
 * or an unequal number (checkEqualityOnExternal=true).
 *
 * @author Christoph
 */
public class TwoStepLookahead implements FeasibilityFunction {


    /**
     * Determines whether the lookahead sets of pattern and target
     * have to be equal or target sets are allowed to be larger.
     */
    private final boolean checkEqualityOnExternal;

    /**
     * @param checkEqualityOnExternal Determines whether equal lookahead sets are required.
     */
    public TwoStepLookahead(boolean checkEqualityOnExternal) {

        this.checkEqualityOnExternal = checkEqualityOnExternal;
    }

    @Override
    public boolean eval(VF2State state, int p, int t) {

        VF2GraphData pattern = state.getPattern();
        Graph patternGraph = pattern.getGraph();
        VF2GraphData target = state.getTarget();
        Graph targetGraph = target.getGraph();

        boolean checkEquality = checkEqualityOnExternal || !patternGraph.isExternal(p);

        int patternPred = computeLookahead(
                patternGraph.getPredecessorsOf(p),
                pattern
        );

        int targetPred = computeLookahead(
                targetGraph.getPredecessorsOf(t),
                target
        );

        if (checkEquality) {

            if (patternPred != targetPred) {

                return false;
            }
        } else {

            if (targetPred < patternPred) {
                return false;
            }
        }

        int patternSucc = computeLookahead(
                patternGraph.getSuccessorsOf(p),
                pattern
        );

        int targetSucc = computeLookahead(
                targetGraph.getSuccessorsOf(t),
                target
        );

        if (checkEquality) {
            return patternSucc == targetSucc;
        } else {
            return (patternSucc <= targetSucc);
        }
    }


    /**
     * Computes the lookahead set for the given set of nodes connected to the considered candidate node.
     *
     * @param nodes The nodes connected to the considered node.
     * @param data  Matching data stored for the graph corresponding to the nodes in neighbors.
     * @return The number of nodes in neighbors that have not been matched yet.
     */
    private int computeLookahead(TIntArrayList nodes, AbstractVF2GraphData data) {

        int lookahead = 0;
        for (int i = 0; i < nodes.size(); i++) {
            int next = nodes.get(i);

            //The original algorithm proposes if(!data.containsNeighbor(next) && !data.containsMatch(next)) {
            // but we are a bit relaxed here due to external nodes
            if (!data.containsMatch(next)) {
                ++lookahead;
            }

        }

        return lookahead;
    }

}
