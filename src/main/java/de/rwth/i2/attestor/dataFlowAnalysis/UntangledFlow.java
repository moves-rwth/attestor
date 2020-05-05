package de.rwth.i2.attestor.dataFlowAnalysis;

import gnu.trove.list.array.TIntArrayList;

public class UntangledFlow extends FlowImpl {
    final int copy;
    final int untangled;

    public UntangledFlow(FlowImpl flow, int untangle) {
        super(flow);
        this.untangled = untangle;
        this.copy = new TIntArrayList(getLabels().toArray()).max() + 1;

        getPredecessors(untangle).forEach(l -> {
            remove(l, untangle);
            add(l, copy);

            return true;
        });
    }

    public void add(int from, int to) {
        if (from == copy) {
            super.add(untangled, to);
        } else if (to == untangled) {
            super.add(from, copy);
        } else {
            super.add(from, to);
        }
    }
}
