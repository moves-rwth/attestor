package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class HeapConfigurationDistanceHelper {

    private HeapConfiguration hc;
    private List<TIntArrayList> distancesToExternalNode;

    public HeapConfigurationDistanceHelper(HeapConfiguration hc) {

        this.hc = hc;
        this.distancesToExternalNode = new ArrayList<>();
        for(int i=0; i < hc.countExternalNodes(); i++) {
            int ext = hc.externalNodeAt(i);
            distancesToExternalNode.add( distancesToNode(ext) );
        }
    }

    private TIntArrayList distancesToNode(int node) {

        int size = hc.countNodes();
        TIntArrayList distances = new TIntArrayList(size);
        PriorityQueue<Integer> queue = initQueue(distances, size, node);
        while(!queue.isEmpty()) {
            int u = queue.poll();
            TIntArrayList succ = hc.successorNodesOf(u);
            for(int i=0; i < succ.size(); i++) {
                int v = succ.get(i);
                int update = distances.get(u) + 1;
                if(update < distances.get(v)) {
                    distances.set(v, update);
                    queue.remove(v);
                    queue.add(v);
                }
            }
        }
        return distances;
    }

    private PriorityQueue<Integer> initQueue(TIntArrayList distances, int size, int node) {

        PriorityQueue<Integer> queue = new PriorityQueue<>(size, Comparator.comparingInt(distances::get));
        for(int i=0; i < size; i++) {
            if(i == node) {
                distances.add(0);
            } else {
                distances.add(size);
            }
            queue.add(i);
        }
        return queue;
    }

    public boolean isReachable(int externalNode1, int externalNode2) {

        return distancesToExternalNode.get(externalNode1).get(externalNode2) < hc.countNodes();
    }
}
