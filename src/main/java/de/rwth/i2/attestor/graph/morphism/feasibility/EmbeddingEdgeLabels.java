package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import de.rwth.i2.attestor.util.ListUtil;
import gnu.trove.list.array.TIntArrayList;

/**
 * Checks whether the edge labels from and to the pattern candidate node are covered by the corresponding
 * edge labels from and to the target candidate node.
 * This is, for example, required to check whether a candidate pair may belong to an embedding of a pattern graph
 * in a target graph.
 *
 * @author Christoph
 */
public class EmbeddingEdgeLabels implements FeasibilityFunction {

    @Override
    public boolean eval(VF2State state, int p, int t) {

        VF2GraphData pattern = state.getPattern();
        VF2GraphData target = state.getTarget();

        Graph patternGraph = pattern.getGraph();
        Graph targetGraph = target.getGraph();


        TIntArrayList succsOfT = targetGraph.getSuccessorsOf(t);
        for(int i=0; i < succsOfT.size(); i++) {
            int succ = succsOfT.get(i);
            if(target.containsMatch(succ) && targetGraph.isEdgeBetweenMarkedNodes(t, succ)) {
                return false;
            }
        }

        TIntArrayList predsOfT = targetGraph.getPredecessorsOf(t);
        for(int i=0; i < predsOfT.size(); i++) {
            int pred= predsOfT.get(i);
            if(target.containsMatch(pred) && targetGraph.isEdgeBetweenMarkedNodes(pred, t)) {
                return false;
            }
        }

        TIntArrayList succsOfP = patternGraph.getSuccessorsOf(p);
        for (int i = 0; i < succsOfP.size(); i++) {

            int succ = succsOfP.get(i);
            if (pattern.containsMatch(succ)) {
                int match = pattern.getMatch(succ);


                if (patternGraph.isExternal(p) && patternGraph.isExternal(succ)) {

                    if (!ListUtil.isSubsetAsMultiset(
                            patternGraph.getEdgeLabel(p, succ),
                            targetGraph.getEdgeLabel(t, match))
                            ) {
                        return false;
                    }
                } else {

                    if (!ListUtil.isEqualAsMultiset(
                            patternGraph.getEdgeLabel(p, succ),
                            targetGraph.getEdgeLabel(t, match))
                            ) {
                        return false;
                    }
                }
            }
        }

        TIntArrayList predsOfP = patternGraph.getPredecessorsOf(p);
        for (int i = 0; i < predsOfP.size(); i++) {

            int pred = predsOfP.get(i);
            if (pattern.containsMatch(pred)) {
                int match = pattern.getMatch(pred);

                if (patternGraph.isExternal(p) && patternGraph.isExternal(pred)) {

                    if (!ListUtil.isSubsetAsMultiset(
                            patternGraph.getEdgeLabel(pred, p),
                            targetGraph.getEdgeLabel(match, t))
                            ) {
                        return false;
                    }
                } else {

                    if (!ListUtil.isEqualAsMultiset(
                            patternGraph.getEdgeLabel(pred, p),
                            targetGraph.getEdgeLabel(match, t))
                            ) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


}
