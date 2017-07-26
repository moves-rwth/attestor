package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.TransitionRelation;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;

public class ReachabilityTransitionRelation implements TransitionRelation {

    private int from;
    private int to;
    private int maxRank;

    public ReachabilityTransitionRelation(int from, int to, int maxRank) {

        this.from = from;
        this.to = to;
        this.maxRank = maxRank;
    }

    @Override
    public AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        assert(ntAssignment.size() == heapConfiguration.countNonterminalEdges());

        HeapConfiguration canonicalHc = computeCanonicalHeapConiguration(ntAssignment, heapConfiguration);
        Map<Integer, TIntSet> reachabilityRelation = computeReachabilityRelation(canonicalHc);
        return new ReachabilityAutomatonState(reachabilityRelation, isFinal(reachabilityRelation));
    }

    private HeapConfiguration computeCanonicalHeapConiguration(List<AutomatonState> ntAssignment,
                                                               HeapConfiguration heapConfiguration) {

        heapConfiguration = heapConfiguration.clone();
        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        for(int i=0; i < ntAssignment.size(); i++) {
            AutomatonState state = ntAssignment.get(i);
            assert(state instanceof ReachabilityAutomatonState);
            ReachabilityAutomatonState rState = (ReachabilityAutomatonState) state;
            int edge = ntEdges.get(i);
            heapConfiguration.builder().replaceNonterminalEdge(edge, rState.getKernel());
        }
        return heapConfiguration.builder().build();
    }

    private Map<Integer, TIntSet> computeReachabilityRelation(HeapConfiguration canonicalHc) {

        Map<Integer, TIntSet> reachabilityRelation = new HashMap<>();
        HeapConfigurationDistanceHelper helper = new HeapConfigurationDistanceHelper(canonicalHc);
        int size = canonicalHc.countExternalNodes();
        for(int i=0; i < canonicalHc.countExternalNodes(); i++) {
            int ext1 = canonicalHc.externalNodeAt(i);
            TIntSet set = new TIntHashSet(size);
            for(int j=0; j < canonicalHc.countExternalNodes(); j++) {
                int ext2 = canonicalHc.externalNodeAt(j);
                if(helper.isReachable(ext1, ext2)) {
                    set.add(j);
                }
            }
            reachabilityRelation.put(i, set);
        }
        return reachabilityRelation;
    }

    private boolean isFinal(Map<Integer, TIntSet> reachabilityRelation) {

        return reachabilityRelation.containsKey(from)
                && reachabilityRelation.get(from).contains(to);
    }

}
