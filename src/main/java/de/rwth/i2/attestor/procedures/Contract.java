package de.rwth.i2.attestor.procedures;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collection;

public interface Contract {

    void addPostconditions(Collection<HeapConfiguration> postconditions);

    HeapConfiguration getPrecondition();
    Collection<HeapConfiguration> getPostconditions();
}
