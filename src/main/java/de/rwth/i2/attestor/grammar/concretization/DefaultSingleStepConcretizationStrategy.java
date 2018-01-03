package de.rwth.i2.attestor.grammar.concretization;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Iterator;

public class DefaultSingleStepConcretizationStrategy implements SingleStepConcretizationStrategy {

    private final Grammar grammar;

    public DefaultSingleStepConcretizationStrategy(Grammar grammar) {
        this.grammar = grammar;
    }

    @Override
    public Iterator<HeapConfiguration> concretize(HeapConfiguration heapConfiguration, int edge) {

        Nonterminal label = heapConfiguration.labelOf(edge);
        Iterator<HeapConfiguration> rules = grammar.getRightHandSidesFor(label).iterator();

        return new Iterator<HeapConfiguration>() {
            @Override
            public boolean hasNext() {

                return rules.hasNext();
            }

            @Override
            public HeapConfiguration next() {

                HeapConfiguration rhs = rules.next();
                return heapConfiguration
                        .clone()
                        .builder()
                        .replaceNonterminalEdge(edge, rhs)
                        .build();
            }
        };
    }
}
