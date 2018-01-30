package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import de.rwth.i2.attestor.util.ListUtil;
import gnu.trove.list.array.TIntArrayList;

/**
 * Checks whether the edge labels of the successor and predecessor nodes
 * of the given candidate pair are equal.
 *
 * @author Christoph
 */
public class CompatibleEdgeLabels implements FeasibilityFunction {

    @Override
    public boolean eval(VF2State state, int p, int t) {

        VF2GraphData pattern = state.getPattern();
        VF2GraphData target = state.getTarget();

        Graph patternGraph = pattern.getGraph();
        Graph targetGraph = target.getGraph();

        TIntArrayList succsOfP = patternGraph.getSuccessorsOf(p);
        for (int i = 0; i < succsOfP.size(); i++) {

            int succ = succsOfP.get(i);
            if (pattern.containsMatch(succ)) {
                int match = pattern.getMatch(succ);

                if (!ListUtil.isEqualAsMultiset(
                        patternGraph.getEdgeLabel(p, succ),
                        targetGraph.getEdgeLabel(t, match))
                        ) {
                    return false;
                }
            }
        }

        TIntArrayList predsOfP = patternGraph.getPredecessorsOf(p);
        for (int i = 0; i < predsOfP.size(); i++) {

            int pred = predsOfP.get(i);
            if (pattern.containsMatch(pred)) {
                int match = pattern.getMatch(pred);

                if (!ListUtil.isEqualAsMultiset(
                        patternGraph.getEdgeLabel(pred, p),
                        targetGraph.getEdgeLabel(match, t))
                        ) {
                    return false;
                }
            }
        }

        return true;
    }


}
