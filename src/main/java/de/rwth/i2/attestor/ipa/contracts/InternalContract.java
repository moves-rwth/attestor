package de.rwth.i2.attestor.ipa.contracts;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.methods.Contract;

import java.util.ArrayList;
import java.util.Collection;

public class InternalContract implements Contract {

    private final HeapConfiguration precondition;
    private final Collection<HeapConfiguration> postconditions;

    public InternalContract(HeapConfiguration precondition, Collection<HeapConfiguration> postconditions) {

        this.precondition = precondition;
        this.postconditions = postconditions;
    }

    public InternalContract(HeapConfiguration precondition) {

        this(precondition, new ArrayList<>());
    }

    @Override
    public void addPostconditions(Collection<HeapConfiguration> postconditions) {

        postconditions.addAll(postconditions);
    }

    @Override
    public HeapConfiguration getPrecondition() {

        return precondition;
    }

    @Override
    public Collection<HeapConfiguration> getPostconditions() {

        return postconditions;
    }
}
