package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;

/**
 * Checks whether the labels of the nodes in the candidate pair coincide.
 *
 * @author Christoph
 */
public class CompatibleNodeTypes implements FeasibilityFunction {

    private static final Type nullType = Types.NULL;

    @Override
    public boolean eval(VF2State state, int p, int t) {

        Graph patternGraph = state.getPattern().getGraph();
        Graph targetGraph = state.getTarget().getGraph();

        NodeLabel patternLabel = patternGraph.getNodeLabel(p);
        NodeLabel targetLabel = targetGraph.getNodeLabel(t);

        if (patternLabel.getClass() == nullType.getClass() && targetLabel.getClass() == nullType.getClass()) {
            Type patternType = (Type) patternLabel;
            Type targetType = (Type) targetLabel;
            return patternType.equals(targetType)
                    || (targetType.equals(nullType) && !Types.isConstantType(patternType));
        } else {
            return patternLabel.matches(targetLabel);
        }
    }


}
