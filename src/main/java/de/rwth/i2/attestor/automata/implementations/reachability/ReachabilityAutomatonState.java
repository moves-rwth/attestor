package de.rwth.i2.attestor.automata.implementations.reachability;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.FactorySettings;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Specialized heap automaton state to check reachability between nodes.
 * These states encode reachability information in a small HeapConfiguration
 * that is called a 'kernel'. A kernel consists of external nodes only and
 * contains a selector edge between two external nodes, say u and v, whenever v is reachable
 * from u.
 *
 * @author Christoph
 */
public class ReachabilityAutomatonState implements AutomatonState {

    /**
     * The kernel encoding reachability between external nodes.
     */
    private HeapConfiguration kernel;

    /**
     * True if and only if this state is a final state.
     */
    private boolean isFinal;

    /**
     * @param reachabilityRelation The set of reachable external nodes for each external node.
     * @param isFinal True if and only if this state is a final state.
     */
    public ReachabilityAutomatonState(List<TIntSet> reachabilityRelation, boolean isFinal) {

        this.kernel = computeKernel(reachabilityRelation);
        this.isFinal = isFinal;
    }

    /**
     * Extracts a kernel heap configuration from a reachability relation.
     * @param reachabilityRelation The set of reachable external nodes for each external node.
     * @return The kernel corresponding to this state.
     */
    private HeapConfiguration computeKernel(List<TIntSet> reachabilityRelation) {

        FactorySettings factory = Settings.getInstance().factory();

        int rank = reachabilityRelation.size();
        Type type = factory.getType("kernelNode");
        HeapConfiguration kernel = factory.createEmptyHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList(rank);
        kernel.builder().addNodes(type, rank, nodes);

        for(int i=0; i < rank; i++) {
            kernel.builder().setExternal(i);
            TIntSet targets = reachabilityRelation.get(i);
            TIntIterator iter = targets.iterator();
            while(iter.hasNext()) {
                int j = iter.next();
                kernel.builder().addSelector(i, factory.getSelectorLabel(String.valueOf(j)), j);
            }
        }

        return kernel.builder().build();
    }

    /**
     * @return The kernel encoding the reachability information of this state.
     */
    public HeapConfiguration getKernel() {

        return kernel;
    }

    @Override
    public boolean isFinal() {

        return isFinal;
    }

    @Override
    public Set<String> getAtomicPropositions() {

        int rank = kernel.countNodes();
        Set<String> res = new HashSet<>();

        for(int i=0; i < rank; i++) {
            TIntIterator iter = kernel.successorNodesOf(i).iterator();
            while(iter.hasNext()) {
                int j = iter.next();
                res.add(format(i,j));
            }
        }

        return res;
    }

    @Override
    public Set<String> getAllAtomicPropositions() {

        int rank = kernel.countNodes();
        Set<String> res = new HashSet<>();
        for(int i=0; i < rank; i++) {
            for(int j=0; j < rank; j++) {
                res.add(format(i,j));
            }
        }
        return res;
    }

    /**
     * Formats a pair of reachable external nodes.
     * @param x The position of the source external node.
     * @param y The position of the target external node.
     * @return A formatted string.
     */
    private String format(int x, int y) {
        return "(" + x + "," + y + ")";
    }

    @Override
    public String toString() {

        return getAtomicPropositions().toString();
    }
}
