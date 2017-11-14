package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;

/**
 * Checks whether the labels of the nodes in the candidate pair coincide.
 *
 * @author Christoph
 */
public class CompatibleNodeTypes implements FeasibilityFunction {

	private static final Type nullType = GeneralType.getType("NULL");

	@Override
	public boolean eval(VF2State state, int p, int t) {

		Graph patternGraph = state.getPattern().getGraph();
		Graph targetGraph = state.getTarget().getGraph();

		NodeLabel patternLabel = patternGraph.getNodeLabel(p);
		NodeLabel targetLabel = targetGraph.getNodeLabel(t);

		if(patternLabel.getClass() == GeneralType.class && targetLabel.getClass() == GeneralType.class){
			GeneralType patternType = (GeneralType) patternLabel;
			GeneralType targetType = (GeneralType) targetLabel;
			return patternType.typeEquals(targetType)
					|| ( targetType.typeEquals( nullType ) && !isConstantType(patternType) );
		}else {
			return patternLabel.matches(targetLabel);
		}
	}
	
	private boolean isConstantType( NodeLabel type ) {
		return type.equals( GeneralType.getType("int") )
				|| type.equals( GeneralType.getType("int_0"))
				|| type.equals( GeneralType.getType("int_1"))
				|| type.equals( GeneralType.getType("int_-1"));
	}

}
