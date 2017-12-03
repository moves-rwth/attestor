package de.rwth.i2.attestor.main.environment;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.IpaAbstractMethod;
import de.rwth.i2.attestor.main.settings.OptionSettings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;

public interface Scene {

    Type getType(String name);

    SelectorLabel getSelectorLabel(String name);

    Nonterminal getNonterminal(String name);
    Nonterminal createNonterminal(String label, int rank, boolean [] isReductionTentacle );

    HeapConfiguration createHeapConfiguration();

    ProgramState createProgramState(HeapConfiguration heapConfiguration);

    IpaAbstractMethod getMethod(String name);

    void addNumberOfGeneratedStates(int states);
    long getNumberOfGeneratedStates();

    OptionSettings options();

}
