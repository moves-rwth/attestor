package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import gnu.trove.list.TIntList;

import java.util.HashMap;
import java.util.Map;

public abstract class HeapTransformation {
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
        public NonterminalInsertion(int ntEdge, Nonterminal label, HeapConfiguration rule, Matching matching) {
            super(ntEdge, label, rule, matchingToMap(rule, matching));
        }

        private static Map<Integer, Integer> matchingToMap(HeapConfiguration source, Matching matching) {
            Map<Integer, Integer> map = new HashMap<>();

            source.nodes().forEach(node -> {
                map.put(node, matching.match(node));
                return true;
            });

            source.variableEdges().forEach(v -> {
                map.put(v, matching.match(v));
                return true;
            });

            source.nonterminalEdges().forEach(nt -> {
                map.put(nt, matching.match(nt));
                return true;
            });

            return map;
        }
    }

    public static class NonterminalReplacement extends HeapTransformation {
        public NonterminalReplacement(int ntEdge, Nonterminal label, HeapConfiguration rule, TIntList matching) {
            super(ntEdge, label, rule, arrayToMap(matching));
        }

        private static Map<Integer, Integer> arrayToMap(TIntList matching) {
            Map<Integer, Integer> map = new HashMap<>();

            for (int i = 0; i < matching.size(); i++) {
                map.put(i, matching.get(i));
            }

            return map;
        }
    }
}
