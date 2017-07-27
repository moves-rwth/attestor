package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.TransitionRelation;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.List;

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
        List<TIntSet> reachabilityRelation = computeReachabilityRelation(canonicalHc);
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

    private List<TIntSet> computeReachabilityRelation(HeapConfiguration canonicalHc) {

        List<TIntSet> reachabilityRelation = new ArrayList<>();
        ReachabilityHelper helper = new ReachabilityHelper(canonicalHc);

        int size = canonicalHc.countExternalNodes();
        for(int i=0; i < size; i++) {
            int ext1 = canonicalHc.externalNodeAt(i);
            TIntSet set = new TIntHashSet(size);
            for(int j=0; j < size; j++) {
                int ext2 = canonicalHc.externalNodeAt(j);
                if(helper.isReachable(ext1, ext2)) {
                    set.add(j);
                }
            }
            reachabilityRelation.add(set);
        }

        return reachabilityRelation;
    }

    private boolean isFinal(List<TIntSet> reachabilityRelation) {

        return from < reachabilityRelation.size()
                && reachabilityRelation.get(from).contains(to);
    }

}
