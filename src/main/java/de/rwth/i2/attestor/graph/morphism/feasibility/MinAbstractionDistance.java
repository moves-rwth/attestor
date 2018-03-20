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
 * Restricts the considered morphisms to ones in which the distance from variables to nodes with outgoing selector
 * edges belonging to a morphism is at least the given minAbstractionDistance.
 * If aggressiveConstantAbstraction is set to true, variables that model constants, such as null, are ignored.
 *
 * @author Christoph
 */
public class MinAbstractionDistance implements FeasibilityFunction {

    private final int minAbstractionDistance;

    private final boolean aggressiveConstantAbstraction;

    private final boolean aggressiveCompositeMarkingAbstraction;

    /**
     * @param options A collection of options guiding how morphisms are computed.
     */
    public MinAbstractionDistance(MorphismOptions options) {

        // TODO
        if(options.isAdmissibleAbstraction()) {
            minAbstractionDistance = 1;
        } else {
            minAbstractionDistance = 0;
        }

        aggressiveConstantAbstraction = !options.isAdmissibleConstants();
        aggressiveCompositeMarkingAbstraction = !options.isAdmissibleMarkings();
    }

    @Override
    public boolean eval(VF2State state, int p, int t) {

        if (!hasOutgoingSelectorEdges(state, p)) {
            return true;
        }

        Graph graph = state.getTarget().getGraph();
        TIntArrayList dist = SelectorDistanceHelper.getSelectorDistances(graph, t);

        for (int i = 0; i < graph.size(); i++) {
            Object nodeLabel = graph.getNodeLabel(i);
            if (nodeLabel.getClass() == Variable.class) {
                String label = ((Variable) nodeLabel).getName();
                //if the option aggressiveConstantAbstraction is enabled, constants are ignored.
                if (!(aggressiveConstantAbstraction && Constants.isConstant(label))) {
                    int attachedNode = graph.getSuccessorsOf(i).get(0);

                    if (dist.get(attachedNode) < minAbstractionDistance) {
                        if(aggressiveCompositeMarkingAbstraction || !Markings.isComposedMarking(label)) {
                            return false;
                        }
                    }
                }
            } else if (graph.isExternal(i)) {
                Type type = (Type) nodeLabel;
                if (!(aggressiveConstantAbstraction && Types.isConstantType(type))) {
                    if (dist.get(i) < minAbstractionDistance) {
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



