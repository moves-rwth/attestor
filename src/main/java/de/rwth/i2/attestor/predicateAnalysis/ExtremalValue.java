package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.Variable;
import de.rwth.i2.attestor.predicateAnalysis.relativeIndex.RelativeIndex;

public interface ExtremalValue<T extends RelativeIndex> {
    T ofVariable(Variable label);

    T ofSelector(SelectorLabel label);

    T ofNonTerminal(Nonterminal label);
}
