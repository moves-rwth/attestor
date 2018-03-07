package de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies;

import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collection;
import java.util.Collections;

public class NoMaterializationStrategy implements MaterializationStrategy {

    @Override
    public Collection<HeapConfiguration> materialize(HeapConfiguration heapConfiguration,
                                                     ViolationPoints potentialViolationPoints) {
        return Collections.emptySet();
    }
}
