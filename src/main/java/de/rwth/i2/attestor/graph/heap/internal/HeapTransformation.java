package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.HashMap;
import java.util.Map;

public class HeapTransformation {
    private final int ntEdge;
    private final Nonterminal label;
    private final HeapConfiguration rule;
    private final Map<Integer, Integer> ruleToHeap = new HashMap<>();

    private HeapTransformation(int ntEdge, Nonterminal label, HeapConfiguration rule, Map<Integer, Integer> ruleToHeap) {
        this.ntEdge = ntEdge;
        this.label = label;
        this.rule = rule;
        this.ruleToHeap.putAll(ruleToHeap);
    }

    public int ruleToHeap(int id) {
        return ruleToHeap.get(id);
    }

    public int getNtEdge() {
        return ntEdge;
    }

    public Nonterminal getLabel() {
        return label;
    }

    public HeapConfiguration getRule() {
        return rule;
    }

    public static class NonterminalInsertion extends HeapTransformation {
        protected NonterminalInsertion(int ntEdge, Nonterminal label, HeapConfiguration rule, Map<Integer, Integer> ruleToHeap) {
            super(ntEdge, label, rule, ruleToHeap);
        }
    }

    public static class NonterminalReplacement extends HeapTransformation {
        protected NonterminalReplacement(int ntEdge, Nonterminal label, HeapConfiguration rule, Map<Integer, Integer> ruleToHeap) {
            super(ntEdge, label, rule, ruleToHeap);
        }
    }
}
