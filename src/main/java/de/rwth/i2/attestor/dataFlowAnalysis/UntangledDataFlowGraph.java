package de.rwth.i2.attestor.dataFlowAnalysis;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class UntangledDataFlowGraph<T> extends DataFlowGraphImpl<T> {
    final TIntIntMap untangled = new TIntIntHashMap();

    @Override
    public void setInitial(int label, boolean isInitial) {
        super.setInitial(label, isInitial);

        if (isInitial) {
            int mirror = addLabel(getNode(label));
            untangled.put(label, mirror);
        } else {
            untangled.remove(label);
        }
    }

    @Override
    public void addFlow(int from, int to) {
        if (initials.contains(to)) {
            super.addFlow(from, untangled.get(to));
        } else {
            super.addFlow(from, to);
        }
    }
}
