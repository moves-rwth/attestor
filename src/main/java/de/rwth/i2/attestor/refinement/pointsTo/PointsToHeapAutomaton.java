package de.rwth.i2.attestor.refinement.pointsTo;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.HeapAutomatonState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PointsToHeapAutomaton implements HeapAutomaton {

    @Override
    public HeapAutomatonState transition(HeapConfiguration heapConfiguration,
                                         List<HeapAutomatonState> statesOfNonterminals) {

        HeapConfiguration composedHc = composeHc(heapConfiguration, statesOfNonterminals);
        HeapConfiguration kernel = computeKernel(composedHc);
        return new PointsToHeapAutomatonState(kernel);
    }

    private HeapConfiguration composeHc(HeapConfiguration heapConfiguration,
                                        List<HeapAutomatonState> statesOfNonterminals) {

        heapConfiguration = heapConfiguration.clone();
        HeapConfigurationBuilder builder = heapConfiguration.builder();
        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        for(int i=0; i < statesOfNonterminals.size(); i++) {
            int edge = ntEdges.get(i);
            PointsToHeapAutomatonState state = (PointsToHeapAutomatonState) statesOfNonterminals.get(i);
            HeapConfiguration stateKernel = state.kernel;
            builder.replaceNonterminalEdge(edge, stateKernel);
        }
        return builder.build();
    }

    private HeapConfiguration computeKernel(HeapConfiguration heapConfiguration) {

        HeapConfigurationBuilder builder = Settings.getInstance().factory().createEmptyHeapConfiguration().builder();
        int countExt = heapConfiguration.countExternalNodes();
        TIntArrayList nodes = new TIntArrayList( countExt );

        addExternalsAndSelectors(builder, nodes, heapConfiguration);
        addVariablesAndSelectors(builder, nodes, heapConfiguration);
        return builder.build();
    }

    private void addExternalsAndSelectors(HeapConfigurationBuilder builder,
                                          TIntArrayList nodes, HeapConfiguration heapConfiguration) {

        int countExt = heapConfiguration.countExternalNodes();

        for(int i=0; i < heapConfiguration.countExternalNodes(); i++) {
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
    }

    private void addVariablesAndSelectors(HeapConfigurationBuilder builder,
                                          TIntArrayList nodes, HeapConfiguration heapConfiguration) {

        int countExt = heapConfiguration.countExternalNodes();
        TIntArrayList variables = heapConfiguration.variableEdges();
        TIntArrayList variableTargets = new TIntArrayList(variables.size());

        for(int i=0; i < variables.size(); i++) {
            int var = variables.get(i);
            String varName = heapConfiguration.nameOf(var);
            int varTarget = heapConfiguration.targetOf(var);
            variableTargets.add(varTarget);
            if(heapConfiguration.isExternalNode(varTarget)) {
                int ext = nodes.get( heapConfiguration.externalIndexOf(varTarget) );
                builder.addVariableEdge(varName, ext);
            } else {
                Type type = heapConfiguration.nodeTypeOf(varTarget);
                builder.addNodes(type, 1, nodes);
                builder.addVariableEdge(varName, nodes.get(nodes.size()-1));
            }
        }

        for(int i=0; i < variables.size(); i++) {
            int var = variables.get(i);
            int hcSource = heapConfiguration.targetOf(var);
            if(!heapConfiguration.isExternalNode(hcSource)) {
                int source = nodes.get(countExt + i);
                for(SelectorLabel sel : heapConfiguration.selectorLabelsOf(hcSource)) {
                    int hcTarget = heapConfiguration.selectorTargetOf(hcSource, sel);
                    if(heapConfiguration.isExternalNode(hcTarget)) {
                        int target = nodes.get( heapConfiguration.externalIndexOf(hcTarget) );
                        builder.addSelector(source, sel, target);
                    } else if(variableTargets.contains(hcTarget)) {
                        int target = nodes.get( countExt + variableTargets.indexOf(hcTarget) );
                        builder.addSelector(source, sel, target);
                    }
                }
            }
        }
    }

    @Override
    public boolean isInitialState(HeapAutomatonState heapAutomatonState) {

        return true;
    }

    @Override
    public List<HeapConfiguration> getPossibleHeapRewritings(HeapConfiguration heapConfiguration) {

        return Collections.singletonList(heapConfiguration);
    }
}

class PointsToHeapAutomatonState extends HeapAutomatonState {

    final HeapConfiguration kernel;

    PointsToHeapAutomatonState(HeapConfiguration kernel) {

        this.kernel = kernel;
    }

    @Override
    public Set<String> toAtomicPropositions() {

        return null;
    }

    @Override
    public boolean isError() {

        return false;
    }

    @Override
    public boolean equals(Object otherObject) {

        if(otherObject == this) {
            return true;
        }

        if(otherObject == null) {
            return false;
        }

        if(otherObject.getClass() != PointsToHeapAutomatonState.class) {
            return false;
        }

        PointsToHeapAutomatonState other = (PointsToHeapAutomatonState) otherObject;
        return kernel != null && kernel.equals(other.kernel);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(kernel);
    }
}
