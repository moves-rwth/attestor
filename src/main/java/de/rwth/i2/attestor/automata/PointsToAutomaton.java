package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

import java.util.List;
import java.util.Set;

public class PointsToAutomaton extends HeapAutomaton {

    @Override
    protected AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        heapConfiguration = heapConfiguration.clone();
        HeapConfigurationBuilder builder = heapConfiguration.builder();
        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();

        for(int i=0; i < ntAssignment.size(); i++) {
            int edge = ntEdges.get(i);
            PointsToState state = (PointsToState) ntAssignment.get(i);
            HeapConfiguration stateKernel = state.kernel;
            builder.replaceNonterminalEdge(edge, stateKernel);
        }
        builder.build();

        HeapConfiguration kernel = computeKernel(heapConfiguration);
        return new PointsToState(kernel);
    }

    private HeapConfiguration computeKernel(HeapConfiguration heapConfiguration) {

        HeapConfigurationBuilder builder = Settings.getInstance().factory().createEmptyHeapConfiguration().builder();
        int countExt = heapConfiguration.countExternalNodes();
        TIntArrayList nodes = new TIntArrayList( countExt );

        for(int i=0; i < countExt; i++) {
            int ext = heapConfiguration.externalNodeAt(i);
            Type type = heapConfiguration.nodeTypeOf(ext);
            builder.addNodes(type, 1, nodes);
            builder.setExternal( nodes.get(i) );
        }

        for(int i=0; i < countExt; i++) {
            int ext = heapConfiguration.externalNodeAt(i);
            for(SelectorLabel sel : heapConfiguration.selectorLabelsOf(ext)) {
                int target = heapConfiguration.selectorTargetOf(ext, sel);
                if(heapConfiguration.isExternalNode(target)) {
                    int extTo =  nodes.get( heapConfiguration.externalIndexOf(target) );
                    builder.addSelector(nodes.get(i), sel, extTo);
                }
            }
        }

        return builder.build();
    }

}


class PointsToState implements AutomatonState {

    final HeapConfiguration kernel;

    PointsToState(HeapConfiguration kernel) {

        this.kernel = kernel;
    }

    @Override
    public boolean isFinal() {

        return kernel.countVariableEdges() > 0;
    }

    @Override
    public Set<String> getAtomicPropositions() {

        return null;
    }
}
