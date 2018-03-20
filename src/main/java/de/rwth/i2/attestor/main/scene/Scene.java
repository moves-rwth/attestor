package de.rwth.i2.attestor.main.scene;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;

import java.util.Collection;

public interface Scene {

    Type getType(String name);

    SelectorLabel getSelectorLabel(String name);

    Nonterminal getNonterminal(String name);

    Nonterminal createNonterminal(String label, int rank, boolean[] isReductionTentacle);

    HeapConfiguration createHeapConfiguration();

    ProgramState createProgramState(HeapConfiguration heapConfiguration);
    ProgramState createProgramState();

    Contract createContract(HeapConfiguration precondition, Collection<HeapConfiguration> postconditions);

    Method getOrCreateMethod(String signature);

    Collection<Method> getRegisteredMethods();

    void addNumberOfGeneratedStates(int states);

    long getNumberOfGeneratedStates();

    Options options();

    Labels labels();

    Strategies strategies();

	Method getMethodIfPresent(String signature) throws ElementNotPresentException;

}
