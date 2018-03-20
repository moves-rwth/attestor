package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.heap.Variable;
import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.MorphismOptions;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

/**
 * Restricts the considered morphisms to ones in which the distance from variables does not prevent admissibility.
 * If ignoreConstants is set to true, variables that model constants, such as null, are ignored.
 *
 * @author Christoph
 */
public class AdmissibleAbstraction implements FeasibilityFunction {

    private final boolean ignoreConstants;

    private final boolean admissibleMarkings;

    /**
     * @param options A collection of options guiding how morphisms are computed.
     */
    public AdmissibleAbstraction(MorphismOptions options) {

        ignoreConstants = !options.isAdmissibleConstants();
        admissibleMarkings = options.isAdmissibleMarkings();
    }

    @Override
    public boolean eval(VF2State state, int p, int t) {

        if (!hasOutgoingSelectorEdges(state, p)) {
            return true;
        }

        Graph graph = state.getTarget().getGraph();

        if(graph.isExternal(t)) {
            Object nodeLabel = graph.getNodeLabel(t);
            Type type = (Type) nodeLabel;
            if (!(ignoreConstants && Types.isConstantType(type))) {
                return false;
            }
        }

        TIntArrayList predecessors = graph.getPredecessorsOf(t);
        for(int i=0; i < predecessors.size(); i++) {
            int pred = predecessors.get(i);
            Object nodeLabel = graph.getNodeLabel(pred);
            if (nodeLabel.getClass() == Variable.class) {
                String label = ((Variable) nodeLabel).getName();

                if (!(ignoreConstants && Constants.isConstant(label))) {
                    if (admissibleMarkings || !Markings.isComposedMarking(label)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean hasOutgoingSelectorEdges(VF2State state, int p) {

        Graph graph = state.getPattern().getGraph();
        TIntIterator iter = graph.getSuccessorsOf(p).iterator();
        while (iter.hasNext()) {
            int succ = iter.next();
            if (graph.getNodeLabel(succ).getClass() == GeneralType.class) {
                return true;
            }
        }
        return false;
    }
}



