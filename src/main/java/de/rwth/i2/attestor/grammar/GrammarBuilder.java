package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.grammar.util.ExternalNodesPartitioner;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

/**
 * Used to construct a grammar.
 * By default, the grammar is not marked as backwards confluent.
 * Use setConfluent() to change this.
 *
 * @author Hannah
 */
public class GrammarBuilder {

    final Map<Nonterminal, List<HeapConfiguration>> rules = new LinkedHashMap<>();
    final Map<Nonterminal, List<CollapsedHeapConfiguration>> collapsedRules = new LinkedHashMap<>();

    public Grammar build() {

        return new Grammar(rules, collapsedRules);
    }

    private void computeCollapsedRules() {

        for(Map.Entry<Nonterminal, List<HeapConfiguration>> entry : rules.entrySet()) {

            Nonterminal nonterminal = entry.getKey();
            boolean[] reductionTentacles = new boolean[nonterminal.getRank()];
            for(int i=0; i < nonterminal.getRank(); i++) {
                reductionTentacles[i] = nonterminal.isReductionTentacle(i);
            }

            List<CollapsedHeapConfiguration> rhs;
            if(collapsedRules.containsKey(nonterminal)) {
                rhs = collapsedRules.get(nonterminal);
            } else {
                rhs = new LinkedList<>();
                collapsedRules.put(nonterminal, rhs);
            }

            for(HeapConfiguration hc : entry.getValue()) {
                ExternalNodesPartitioner partitioner = new ExternalNodesPartitioner(hc, reductionTentacles);
                for(TIntArrayList extIndexPartition : partitioner.getPartitions()) {
                    HeapConfiguration collapsedHc = hc.clone().builder().mergeExternals(extIndexPartition).build();
                    CollapsedHeapConfiguration collapsed = new CollapsedHeapConfiguration(hc, collapsedHc, extIndexPartition);
                    rhs.add(collapsed);
                }
            }
        }
    }

    public GrammarBuilder addRule(Nonterminal lhs, HeapConfiguration rhs) {

        if (!rules.containsKey(lhs)) {
            rules.put(lhs, new LinkedList<>());
        }

        rules.get(lhs).add(rhs);

        return this;
    }

    public GrammarBuilder addRules(Nonterminal lhs, Collection<HeapConfiguration> rightHandSides) {

        if (!rules.containsKey(lhs)) {
            rules.put(lhs, new LinkedList<>());
        }
        rules.get(lhs).addAll(rightHandSides);
        return this;
    }

    public GrammarBuilder addRules(Map<Nonterminal, ? extends Collection<HeapConfiguration>> newRules) {

        for (Map.Entry<Nonterminal, ? extends Collection<HeapConfiguration>> ruleEntry : newRules.entrySet()) {
            this.addRules(ruleEntry.getKey(), ruleEntry.getValue());
        }
        return this;
    }

    public GrammarBuilder addRules(Grammar grammar) {

        rules.putAll(grammar.rules);
        return this;
    }

    public GrammarBuilder addCollapsedRule(Nonterminal lhs, CollapsedHeapConfiguration rhs) {

        if (!collapsedRules.containsKey(lhs)) {
            collapsedRules.put(lhs, new LinkedList<>());
        }
        collapsedRules.get(lhs).add(rhs);
        return this;

    }

    public GrammarBuilder updateCollapsedRules() {
        computeCollapsedRules();
        return this;
    }

}
