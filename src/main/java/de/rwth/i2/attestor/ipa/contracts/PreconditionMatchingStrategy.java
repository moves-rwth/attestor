package de.rwth.i2.attestor.ipa.contracts;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.methods.Contract;
import de.rwth.i2.attestor.ipa.methods.ContractMatch;

public interface PreconditionMatchingStrategy {

    ContractMatch match(Contract contract, HeapConfiguration heapInScope);
}
