package de.rwth.i2.attestor.grammar.concretization;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NaiveConcretizer implements Concretizer {

    private Grammar grammar;

    public NaiveConcretizer(Grammar grammar) {

        this.grammar = grammar;
    }

    @Override
    public List<HeapConfiguration> concretize(HeapConfiguration abstractHc, int count) {

        List<HeapConfiguration> result = new ArrayList<>(count);
        LinkedList<HeapConfiguration> queue = new LinkedList<>();
        queue.add(abstractHc);
        while (result.size() < count && !queue.isEmpty()) {
            HeapConfiguration current = queue.removeFirst();
            if (current.countNonterminalEdges() == 0) {
                result.add(current);
                continue;
            }
            TIntIterator ntEdgeIterator = abstractHc.nonterminalEdges().iterator();
            while (ntEdgeIterator.hasNext()) {
                int ntEdge = ntEdgeIterator.next();
                Nonterminal label = abstractHc.labelOf(ntEdge);
                for (HeapConfiguration rhs : grammar.getRightHandSidesFor(label)) {
                    HeapConfiguration nextHc = abstractHc.clone()
                            .builder()
                            .replaceNonterminalEdge(ntEdge, rhs)
                            .build();
                    queue.addLast(nextHc);
                }
            }
        }
        return result;
    }
}
