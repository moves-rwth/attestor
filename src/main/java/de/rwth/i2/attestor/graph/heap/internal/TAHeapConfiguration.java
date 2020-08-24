package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// Transformation Aware Heap Configuration
public class TAHeapConfiguration extends InternalHeapConfiguration {
    protected final List<Integer> nonterminals = new ArrayList<>();
    public final Deque<HeapTransformation> transformationQueue = new ArrayDeque<>();

    public TAHeapConfiguration() {
        super();
    }

    public TAHeapConfiguration(TAHeapConfiguration heapConfiguration) {
        super(heapConfiguration);
        this.transformationQueue.addAll(heapConfiguration.transformationQueue);
        this.nonterminals.addAll(heapConfiguration.nonterminals);
    }

    public TAHeapConfiguration getBlankCopy() {
        TAHeapConfiguration copy = new TAHeapConfiguration(this);
        copy.transformationQueue.clear();
        copy.nonterminals.clear();
        return copy;
    }

    protected void addTransformationStep(HeapTransformation step) {
        this.transformationQueue.add(step);
    }

    protected void registerNonterminalId(int id) {
        nonterminals.add(id);
    }

    protected void replaceNonterminalAtPosition(int position, int id) {
        nonterminals.set(position, id);
    }

    public Integer getNonterminalIdByPosition(int position) {
        return nonterminals.get(position);
    }

    public Integer getNonterminalPositionById(int id) {
        return nonterminals.indexOf(id);
    }

    @Override
    public boolean equals(Object otherObject) {
        return super.equals(otherObject);
    }

    @Override
    public HeapConfiguration clone() {
        return new TAHeapConfiguration(this);
    }

    @Override
    public HeapConfiguration getEmpty() {
        return new TAHeapConfiguration();
    }

    @Override
    public TAHeapConfigurationBuilder builder() {
        if (builder == null) {
            builder = new TAHeapConfigurationBuilder(this);
        }

        return (TAHeapConfigurationBuilder) builder;
    }
}
