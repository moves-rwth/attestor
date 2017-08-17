package de.rwth.i2.attestor.automata;

import java.util.*;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

/**
 * A simple heap automaton to determine all variables that point via a single selector to each other.
 * In other words, it determines all triples (s,l,t) in a heap configuration, where s and t are variable
 * names and l is the name of a selector such that s.l = a holds.
 *
 * @author Christoph
 */
public class PointsToAutomaton extends HeapAutomaton {

    @FunctionalInterface
    public interface HeapConfigurationFactory {

        HeapConfiguration createEmptyHc();
    }

    private HeapConfigurationFactory hcFactory;

    public PointsToAutomaton(HeapConfigurationFactory hcFactory) {

        this.hcFactory = hcFactory;
    }

    @Override
    protected AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        HeapConfiguration composedHc = composeHc(ntAssignment, heapConfiguration);
        HeapConfiguration kernel = computeKernel(composedHc);
        return new PointsToState(kernel);
    }

    private HeapConfiguration composeHc(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        heapConfiguration = heapConfiguration.clone();
        HeapConfigurationBuilder builder = heapConfiguration.builder();
        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();

        for(int i=0; i < ntAssignment.size(); i++) {
            int edge = ntEdges.get(i);
            PointsToState state = (PointsToState) ntAssignment.get(i);
            HeapConfiguration stateKernel = state.kernel;
            builder.replaceNonterminalEdge(edge, stateKernel);
        }
        return builder.build();
    }

    private HeapConfiguration computeKernel(HeapConfiguration heapConfiguration) {

        HeapConfigurationBuilder builder = hcFactory.createEmptyHc().builder();
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

        Set<String> aps = new HashSet<>();
        TIntArrayList variables = kernel.variableEdges();
        TIntArrayList variableTargets = new TIntArrayList(variables.size());
        for(int i=0; i < variables.size(); i++) {
            variableTargets.add(kernel.targetOf(variables.get(i)));
        }
        for(int i=0; i < variables.size(); i++) {
            int source = variableTargets.get(i);
            for(SelectorLabel sel : kernel.selectorLabelsOf(source)) {
                int target = kernel.selectorTargetOf(source, sel);
                if(variableTargets.contains(target)) {
                    aps.add(
                            "("
                            + kernel.nameOf(variables.get(i))
                            + ","
                            + sel.getLabel()
                            + ","
                            + kernel.nameOf(variables.get(variableTargets.indexOf(target)))
                            + ")"
                    );
                }
            }
        }
        return aps;
    }

    @Override
    public boolean equals(Object other) {

        if(other instanceof PointsToState) {
            return kernel.equals( ((PointsToState) other).kernel );
        }
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(kernel);
    }
}
