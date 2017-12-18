package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;

import java.util.ArrayList;
import java.util.Collection;

public class MockupMaterializationStrategy implements MaterializationStrategy {
    //private static final Logger logger = LogManager.getLogger( "TestMaterializationStrategy" );

    @Override
    public Collection<HeapConfiguration> materialize(HeapConfiguration heapConfiguration,
                                                     ViolationPoints potentialViolationPoints) {

        return new ArrayList<>();
    }


}
