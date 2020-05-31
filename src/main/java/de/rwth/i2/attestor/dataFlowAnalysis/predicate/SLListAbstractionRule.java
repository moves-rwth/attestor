package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.domain.AugmentedInteger;
import de.rwth.i2.attestor.domain.RelativeIndex;
import de.rwth.i2.attestor.domain.RelativeInteger;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class SLListAbstractionRule implements IndexAbstractionRule<RelativeIndex<AugmentedInteger>> {
    private final Grammar grammar;
    private final RelativeInteger.RelativeIntegerSet indexOp;

    public SLListAbstractionRule(Grammar grammar) {
        this.grammar = grammar;
        this.indexOp = new RelativeInteger.RelativeIntegerSet();
    }

    @Override
    public TIntObjectMap<RelativeIndex<AugmentedInteger>>
    abstractForward(RelativeIndex<AugmentedInteger> index, Nonterminal nt, HeapConfiguration rule) {
        TIntObjectHashMap<RelativeIndex<AugmentedInteger>> result = new TIntObjectHashMap<>();
        switch (grammar.getRulePosition(nt, rule)) {
            case 0:
                break;
            case 1:
                result.put(3, indexOp.add(index, RelativeInteger.get(-1)));
                break;
            case 2:
                result.put(3, indexOp.add(index, RelativeInteger.get(-1)));
                result.put(4, indexOp.add(index, RelativeInteger.get(-1)));
                break;
            default:
//                throw new IllegalArgumentException("Unknown grammar rule");
                return null;
        }

        return result;
    }

    @Override
    public RelativeIndex<AugmentedInteger>
    abstractBackward(TIntObjectMap<RelativeIndex<AugmentedInteger>> assign, Nonterminal nt, HeapConfiguration rule) {
        switch (grammar.getRulePosition(nt, rule)) {
            case 0:
                return RelativeInteger.get(1);
            case 1:
                return indexOp.add(assign.get(3), RelativeInteger.get(1));
            case 2:
                return indexOp.add(assign.get(4), assign.get(3));
            default:
                // throw new IllegalArgumentException("Unknown grammar rule");
                return null;
        }
    }
}
