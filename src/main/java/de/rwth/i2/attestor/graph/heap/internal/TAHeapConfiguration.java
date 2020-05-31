package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;

import java.util.LinkedList;
import java.util.Queue;

// Transformation Aware Heap Configuration
public class TAHeapConfiguration extends InternalHeapConfiguration {
    public final Queue<TransformationStep> transformationHistory = new LinkedList<>();

    public TAHeapConfiguration() {
        super();
    }

    public TAHeapConfiguration(TAHeapConfiguration heapConfiguration) {
        super(heapConfiguration);
        // this.transformationHistory.addAll(heapConfiguration.transformationHistory);
    }

    public TAHeapConfiguration getBlankCopy() {
        TAHeapConfiguration copy = new TAHeapConfiguration(this);
        copy.transformationHistory.clear();
        return copy;
    }

    void addTransformationStep(TransformationStep step) {
        this.transformationHistory.add(step);
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
