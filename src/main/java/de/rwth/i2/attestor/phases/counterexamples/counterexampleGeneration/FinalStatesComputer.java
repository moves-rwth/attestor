package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;
import java.util.function.BiFunction;

interface FinalStatesComputer
        extends BiFunction<Collection<HeapConfiguration>, ProgramState, Collection<ProgramState>> {
}
