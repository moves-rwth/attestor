package de.rwth.i2.attestor.dataFlowAnalysis;

import java.util.Set;

public interface Flow {
    Set<Integer> getLabels();

    Set<Integer> getInitial();

    Set<Integer> getFinal();

    Set<Integer> getSuccessors(int label);

    Set<Integer> getPredecessors(int label);
}
