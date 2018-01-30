package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import gnu.trove.list.array.TIntArrayList;

public abstract class AbstractCompatibleNeighbours implements FeasibilityFunction {

    /**
     * True if and only the procedure should check whether pattern and target node have the same
     * already matched successor nodes. Otherwise, it suffices that all already matched successors of the pattern
     * node have matching successors of the target node.
     */
    private final boolean checkEqualityOnExternal;

    public AbstractCompatibleNeighbours(boolean checkEqualityOnExternal) {

        this.checkEqualityOnExternal = checkEqualityOnExternal;
    }

    protected abstract TIntArrayList getAdjacent(Graph graph, int node);


    @Override
    public boolean eval(VF2State state, int p, int t) {

        VF2GraphData pattern = state.getPattern();
        Graph patternGraph = pattern.getGraph();
        VF2GraphData target = state.getTarget();
        Graph targetGraph = target.getGraph();

        boolean checkEquality = checkEqualityOnExternal || !patternGraph.isExternal(p);

        TIntArrayList adjacentNodesOfP = getAdjacent(patternGraph, p);
        TIntArrayList adjacentNodesOfT = getAdjacent(targetGraph, t);

        TIntArrayList targetMatches = new TIntArrayList(adjacentNodesOfP.size());
        int countSelfLoops = 0;


        for (int i = 0; i < adjacentNodesOfP.size(); i++) {

            int adjP = adjacentNodesOfP.get(i);

            if (pattern.containsMatch(adjP)) {

                int match = pattern.getMatch(adjP);
                if (checkEquality && !adjacentNodesOfT.contains(match)) {
                    return false;
                }
                targetMatches.add(match);
            } else if (p == adjP) {
                ++countSelfLoops;
            }
        }

        if(countSelfLoops > 0) {
            int countTargetSelfLoops = 0;
            for(int i=0; i < adjacentNodesOfT.size(); i++) {
                if(adjacentNodesOfT.get(i) == t) {
                    ++countTargetSelfLoops;
                }
            }
            if(countSelfLoops > countTargetSelfLoops) {
                return false;
            }
        }

        for(int i=0; i < countSelfLoops; i++) {
        }

        for (int i = 0; i < adjacentNodesOfT.size(); i++) {
            int adjT = adjacentNodesOfT.get(i);
            if (checkEquality && target.containsMatch(adjT) && !targetMatches.contains(adjT)) {
                return false;
            }
        }

        return true;
    }
}
