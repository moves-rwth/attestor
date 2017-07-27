package de.rwth.i2.attestor.automata.implementations;

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

public class ReachabilityAutomatonState implements AutomatonState {

    private HeapConfiguration kernel;
    private boolean isFinal;

    public ReachabilityAutomatonState(List<TIntSet> reachabilityRelation, boolean isFinal) {

        this.kernel = computeKernel(reachabilityRelation);
        this.isFinal = isFinal;
    }

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

    private String format(int x, int y) {
        return "(" + x + "," + y + ")";
    }

    public String toString() {

        return getAtomicPropositions().toString();
    }
}
