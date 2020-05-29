package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import gnu.trove.list.array.TIntArrayList;

public abstract class TransformationStep {
    private final Nonterminal label;
    private final int ntEdge;
    private final HeapConfiguration rule;

    public abstract int match(int id);

    private TransformationStep(int ntEdge, Nonterminal label, HeapConfiguration rule) {
        this.ntEdge = ntEdge;
        this.label = label;
        this.rule = rule;
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

    public static class CanonicalizationStep extends TransformationStep {
        private final Matching matching;

        public CanonicalizationStep(int ntEdge, Nonterminal label, HeapConfiguration rule, Matching matching) {
            super(ntEdge, label, rule);
            this.matching = matching;
        }

        @Override
        public int match(int id) {
            return this.matching.match(id);
        }
    }

    public static class MaterializationStep extends TransformationStep {
        private final TIntArrayList matching;

        public MaterializationStep(int ntEdge, Nonterminal label, HeapConfiguration rule, TIntArrayList matching) {
            super(ntEdge, label, rule);
            this.matching = matching;
        }

        @Override
        public int match(int id) {
            return this.matching.get(id);
        }
    }
}
