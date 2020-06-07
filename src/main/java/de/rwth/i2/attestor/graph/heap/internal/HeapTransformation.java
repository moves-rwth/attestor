package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.function.Function;

// TODO(mkh) use bijective mappings for matching

public abstract class HeapTransformation {
    private int ntEdge;
    private final Nonterminal label;
    private final HeapConfiguration rule;
    private final TIntIntMap ruleToHeap = new TIntIntHashMap();
    private final TIntIntMap heapToRule = new TIntIntHashMap();

    private HeapTransformation(int ntEdge, Nonterminal label, HeapConfiguration rule, TIntIntMap ruleToHeap) {
        this.ntEdge = ntEdge;
        this.label = label;
        this.rule = rule;
        this.ruleToHeap.putAll(ruleToHeap);
        invertRuleToHeap();
    }

    private void invertRuleToHeap() {
        heapToRule.clear();
        ruleToHeap.forEachEntry((key, value) -> {
            heapToRule.put(value, key);
            return true;
        });
    }

    public int ruleToHeap(int id) {
        return ruleToHeap.containsKey(id) ? ruleToHeap.get(id) : -1;
    }

    public int heapToRule(int id) {
        return heapToRule.containsKey(id) ? heapToRule.get(id) : -1;
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

    public void merge(Matching matching) {
        ntEdge = matching.match(ntEdge);
        ruleToHeap.transformValues(current -> {
            if (matching.pattern().nodes().contains(current) ||
                    matching.pattern().variableEdges().contains(current) ||
                    matching.pattern().nonterminalEdges().contains(current)
            ) {
                return matching.match(current);
            } else {
                return current;
            }
        });
        invertRuleToHeap();
    }

    public static class NonterminalInsertion extends HeapTransformation {
        public NonterminalInsertion(int ntEdge, Nonterminal label, HeapConfiguration rule, Matching matching) {
            super(ntEdge, label, rule, matchingToMap(rule, matching));
        }

        private static TIntIntMap matchingToMap(HeapConfiguration source, Matching matching) {
            TIntIntHashMap map = new TIntIntHashMap();
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
        public NonterminalReplacement(int ntEdge, Nonterminal label, HeapConfiguration rule, TIntArrayList matching) {
            super(ntEdge, label, rule, arrayToMap(matching));
        }

        private static TIntIntMap arrayToMap(TIntList matching) {
            TIntIntHashMap map = new TIntIntHashMap();
            for (int i = 0; i < matching.size(); i++) {
                map.put(i, matching.get(i));
            }

            return map;
        }
    }
}
