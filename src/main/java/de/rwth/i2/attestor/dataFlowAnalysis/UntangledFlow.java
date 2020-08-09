package de.rwth.i2.attestor.dataFlowAnalysis;

import java.util.Collections;
import java.util.HashSet;

public class UntangledFlow extends FlowImpl {
    public final int original;
    public final int untangled;

    public UntangledFlow(FlowImpl flow, int untangle) {
        super(flow);
        original = untangle;
        untangled = Collections.max(getLabels()) + 1;

        // TODO(mkh): fix ConcurrentModificationException
        for (Integer predecessor : new HashSet<>(getPredecessors(original))) {
            remove(predecessor, original);
            add(predecessor, untangled);
        }
    }

    public void add(int from, int to) {
        if (from == untangled) {
            super.add(original, to);
        } else if (to == original) {
            super.add(from, untangled);
        } else {
            super.add(from, to);
        }
    }
}
