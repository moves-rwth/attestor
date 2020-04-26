package de.rwth.i2.attestor.grammar.materialization.strategies;

import de.rwth.i2.attestor.grammar.materialization.util.GrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.util.MaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.TAHeapConfiguration;

import java.util.Collection;

public class TAMaterializationStrategy extends GeneralMaterializationStrategy {

    public TAMaterializationStrategy(MaterializationRuleManager ruleManager, GrammarResponseApplier ruleApplier) {
        super(ruleManager, ruleApplier);
    }

    @Override
    public Collection<HeapConfiguration> materialize(HeapConfiguration heapConfiguration, ViolationPoints potentialViolationPoints) {
        if (!(heapConfiguration instanceof TAHeapConfiguration)) {
            throw new IllegalStateException("only transformation-aware heap configurations can be handled");
        }

        TAHeapConfiguration blank = ((TAHeapConfiguration) heapConfiguration).getBlankCopy();
        return super.materialize(blank, potentialViolationPoints);
    }
}
