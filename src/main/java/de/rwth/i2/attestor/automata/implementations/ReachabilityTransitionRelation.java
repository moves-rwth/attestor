package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.TransitionRelation;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract transition relation to check reachability between nodes in a heap configuration.
 * The criterion determining final states has to be implemented by subclasses.
 * <br />
 * The main idea of this transition relation is to replace every nonterminal hyperedge
 * by a kernel heap configuration that is encoded by every automaton state.
 * The resulting canonical heap configuration is then checked for reachability and determines the
 * follow-up state.
 *
 * @author Christoph
 */
public abstract class ReachabilityTransitionRelation implements TransitionRelation {

    @Override
    public AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        assert(ntAssignment.size() == heapConfiguration.countNonterminalEdges());

        HeapConfiguration canonicalHc = computeCanonicalHeapConfiguration(ntAssignment, heapConfiguration);
        ReachabilityHelper helper = new ReachabilityHelper(canonicalHc);
        List<TIntSet> reachabilityRelation = reachableExternalNodes(canonicalHc, helper);
        boolean finalState = isFinalState(canonicalHc, helper);
        return new ReachabilityAutomatonState(reachabilityRelation, finalState);
    }

    /**
     * Determines whether the analyzed heap configuration leads to a final state based on
     * its reachability information.
     * @param canonicalHc The canonical heap configuration corresponding to the analyzed heap configuration.
     * @param helper The full reachability relation for the canonical heap configuration.
     * @return True if and only if the follow-up state is a final state.
     */
    protected abstract boolean isFinalState(HeapConfiguration canonicalHc, ReachabilityHelper helper);

    /**
     * Computes the canonical heap configuration by replacing every nonterminal hyperedge by the
     * corresponding kernel graph that is determined by the assigned automaton state.
     * @param ntAssignment Assigns a state (and thus a kernel graph) to every nonterminal hyperedge.
     * @param heapConfiguration The heap configuration to analyze.
     * @return The canonical heap configuration.
     */
    private HeapConfiguration computeCanonicalHeapConfiguration(List<AutomatonState> ntAssignment,
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

    /**
     * Determines the a reachability relation between all external nodes of the given heap configuration
     * that is used to compute the kernel of the follow-up automaton state.
     * @param canonicalHc The canonical heap configuration corresponding to the analyzed heap configuration.
     * @param helper The full reachability relation for the canonical heap configuration.
     * @return A list assigning to each position of an external nodes is set of positions of reachable external nodes.
     */
    private List<TIntSet> reachableExternalNodes(HeapConfiguration canonicalHc, ReachabilityHelper helper) {

        List<TIntSet> reachabilityRelation = new ArrayList<>();
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
}
