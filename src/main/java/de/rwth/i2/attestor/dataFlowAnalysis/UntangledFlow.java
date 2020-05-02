package de.rwth.i2.attestor.dataFlowAnalysis;

import gnu.trove.list.array.TIntArrayList;

public class UntangledFlow extends FlowImpl {
    public UntangledFlow(FlowImpl flow, int untangle) {
        super(flow);

        int untangled = new TIntArrayList(getLabels().toArray()).max() + 1;

        getPredecessors(untangle).forEach(l -> {
            remove(l, untangle);
            add(l, untangled);

            return true;
        });
    }
}
