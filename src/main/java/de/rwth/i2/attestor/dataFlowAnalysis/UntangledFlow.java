package de.rwth.i2.attestor.dataFlowAnalysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UntangledFlow extends FlowImpl {
    public final int copy;
    public final int untangled;

    public UntangledFlow(FlowImpl flow, int untangle) {
        super(flow);
        this.untangled = untangle;
        this.copy = Collections.max(getLabels()) + 1;

        // TODO(mkh): fix ConcurrentModificationException
        for (Integer predecessor : new HashSet<>(getPredecessors(untangle))) {
            remove(predecessor, untangle);
            add(predecessor, copy);
        }
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
