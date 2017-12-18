package de.rwth.i2.attestor.grammar.concretization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FullConcretizationStrategyImpl implements FullConcretizationStrategy {

    private final SingleStepConcretizationStrategy singleStepConcretizationStrategy;

    public FullConcretizationStrategyImpl(SingleStepConcretizationStrategy singleStepConcretizationStrategy) {

        this.singleStepConcretizationStrategy = singleStepConcretizationStrategy;
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
                Iterator<HeapConfiguration> nextHcIterator = singleStepConcretizationStrategy.concretize(abstractHc, ntEdge);
                while(nextHcIterator.hasNext()) {
                   HeapConfiguration nextHc = nextHcIterator.next();
                    queue.addLast(nextHc);
                }
            }
        }
        return result;
    }
}
