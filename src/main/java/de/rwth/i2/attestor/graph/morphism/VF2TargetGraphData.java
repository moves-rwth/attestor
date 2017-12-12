package de.rwth.i2.attestor.graph.morphism;

public class VF2TargetGraphData extends AbstractVF2GraphData {

    private final int[] matchCounter;

    VF2TargetGraphData(Graph graph) {

        super(graph);

        int noNodes = graph.size();
        matchCounter = new int[noNodes];
    }

    VF2TargetGraphData(VF2TargetGraphData data) {

        super(data);
        matchCounter = data.matchCounter;
    }

    @Override
    protected void matchNode(int matchFrom, int matchTo) {

        ++matchCounter[matchFrom];
    }

    @Override
    protected void unmatchNode(int node) {

        --matchCounter[node];
    }

    @Override
    public boolean containsMatch(int node) {

        return matchCounter[node] > 0;
    }
}
