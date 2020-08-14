package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;

import java.util.ArrayDeque;
import java.util.Deque;

// Transformation Aware Heap Configuration
public class TAHeapConfiguration extends InternalHeapConfiguration {
    public final Deque<HeapTransformation> transformationQueue = new ArrayDeque<>();

    public TAHeapConfiguration() {
        super();
    }

    public TAHeapConfiguration(TAHeapConfiguration heapConfiguration) {
        super(heapConfiguration);
        this.transformationQueue.addAll(heapConfiguration.transformationQueue);
    }

    public TAHeapConfiguration getBlankCopy() {
        TAHeapConfiguration copy = new TAHeapConfiguration(this);
        copy.transformationQueue.clear();
        return copy;
    }

    void addTransformationStep(HeapTransformation step) {
        this.transformationQueue.add(step);
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
    public HeapConfigurationBuilder builder() {
        if (builder == null) {
            builder = new TAHeapConfigurationBuilder(this);
        }

        return builder;
    }
}
