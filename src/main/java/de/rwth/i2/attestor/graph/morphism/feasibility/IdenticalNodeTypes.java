package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import de.rwth.i2.attestor.types.GeneralType;

/**
 * Checks whether the labels of the nodes in the candidate pair coincide.
 *
 * @author Christoph
 */
public class IdenticalNodeTypes implements FeasibilityFunction {

    @Override
    public boolean eval(VF2State state, int p, int t) {

        Graph patternGraph = state.getPattern().getGraph();
        Graph targetGraph = state.getTarget().getGraph();

        if (patternGraph.getNodeLabel(p).getClass() == GeneralType.class) {
            GeneralType nodeType = (GeneralType) patternGraph.getNodeLabel(p);
            return nodeType.typeEquals(targetGraph.getNodeLabel(t));
        } else {
            return patternGraph.getNodeLabel(p)
                    .equals(
                            targetGraph.getNodeLabel(t)
                    );
        }
    }

}