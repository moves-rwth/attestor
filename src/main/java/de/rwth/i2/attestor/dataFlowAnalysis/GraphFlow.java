package de.rwth.i2.attestor.dataFlowAnalysis;

import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.graph.*;

import java.util.*;


public class GraphFlow extends FlowImpl {
    private Set<Set<Integer>> circuits = null;

    public GraphFlow() {
    }

    public GraphFlow(Flow flow) {
        super(flow);
    }

    public Set<Integer> reachableLabels(int label) {
        Set<Integer> reachable = new HashSet<>();
        reachableHelper(label, reachable);
        return Collections.unmodifiableSet(reachable);
    }

    private void reachableHelper(int label, Set<Integer> accumulator) {
        for (Integer successor : getSuccessors(label)) {
            if (!accumulator.contains(successor)) {
                accumulator.add(successor);
                reachableHelper(successor, accumulator);
            }
        }
    }

    public Set<Set<Integer>> getCircuits() {
        if (circuits == null) {
            circuits = new HashSet<>();

            DefaultDirectedGraph<Integer, DefaultEdge> dg = new DefaultDirectedGraph<>(DefaultEdge.class);
            for (Integer label : getLabels()) {
                dg.addVertex(label);
                for (Integer successor : getSuccessors(label)) {
                    dg.addVertex(successor);
                    dg.addEdge(label, successor);
                }
            }

            JohnsonSimpleCycles<Integer, DefaultEdge> johnson = new JohnsonSimpleCycles<>(dg);
            List<List<Integer>> circuitList = johnson.findSimpleCycles();
            for ( List<Integer> circuit: circuitList) {
                circuits.add(new HashSet<Integer>(circuit));
            }
        }

        return circuits;
    }
}
