package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.domain.RelativeInteger;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.HashMap;
import java.util.Map;

// TODO(mkh): example file; to be removed
public class SLListAbstractionRule implements IndexAbstractionRule<RelativeInteger> {
    private final Grammar grammar;

    public SLListAbstractionRule(Grammar grammar) {
        this.grammar = grammar;
    }

    @Override
    public Map<Integer, RelativeInteger>
    abstractForward(RelativeInteger index, Nonterminal nt, HeapConfiguration rule) {
        HashMap<Integer, RelativeInteger> result = new HashMap<>();
        switch (grammar.getRulePosition(nt, rule)) {
            case 0:
                break;
            case 1:
                result.put(3, RelativeInteger.opSet.add(index, RelativeInteger.get(-1)));
                break;
            case 2:
                result.put(3, RelativeInteger.opSet.add(index, RelativeInteger.get(-1)));
                result.put(4, RelativeInteger.opSet.add(index, RelativeInteger.get(-1)));
                break;
            default:
                throw new IllegalArgumentException("Unknown grammar rule");
        }

        return result;
    }

    @Override
    public RelativeInteger
    abstractBackward(Map<Integer, RelativeInteger> assign, Nonterminal nt, HeapConfiguration rule) {
        switch (grammar.getRulePosition(nt, rule)) {
            case 0:
                return RelativeInteger.get(1);
            case 1:
                return RelativeInteger.opSet.add(assign.get(3), RelativeInteger.get(1));
            case 2:
                return RelativeInteger.opSet.add(assign.get(4), assign.get(3));
            default:
                throw new IllegalArgumentException("Unknown grammar rule");
        }
    }
}
