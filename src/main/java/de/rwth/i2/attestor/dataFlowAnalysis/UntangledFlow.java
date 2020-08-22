package de.rwth.i2.attestor.dataFlowAnalysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UntangledFlow extends FlowImpl {
    public final int original;
    public final int untangled;

    public UntangledFlow(Flow flow, int untangle) {
        super(flow);
        original = untangle;
        untangled = Collections.max(getLabels()) + 1;

        Set<Integer> predecessors = new HashSet<>(getPredecessors(original));
        for (Integer predecessor : predecessors) {
            super.remove(predecessor, original);
            super.add(predecessor, untangled);
        }
    }

    @Override
    public void add(int from, int to) {
        if (from == untangled) {
            super.add(original, to);
        } else if (to == original) {
            super.add(from, untangled);
        } else {
            super.add(from, to);
        }
    }

    @Override
    public void remove(int from, int to) {
        if (from == untangled) {
            super.remove(original, to);
        } else if (to == original) {
            super.remove(from, untangled);
        } else {
            super.remove(from, to);
        }
    }
}
