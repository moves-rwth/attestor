package de.rwth.i2.attestor.dataFlowAnalysis;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import jgraphalgos.WeightedEdge;
import jgraphalgos.johnson.Johnson;

import java.util.*;


public class GraphFlow extends FlowImpl {
    private Set<Stack<Integer>> circuits = null;

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

    public Set<Stack<Integer>> getCircuits() {
        if (circuits == null) {

            DirectedSparseGraph<Integer, WeightedEdge> dsg = new DirectedSparseGraph<>();
            for (Integer label : getLabels()) {
                for (Integer successor : getSuccessors(label)) {
                    dsg.addEdge(new WeightedEdge(1), label, successor);
                }
            }

            Johnson johnson = new Johnson(dsg);
            try {
                johnson.findCircuits();
            } catch (Johnson.JohnsonIllegalStateException e) {
                throw new IllegalStateException();
            }

            circuits = johnson.getCircuits();
        }

        return circuits;
    }
}
