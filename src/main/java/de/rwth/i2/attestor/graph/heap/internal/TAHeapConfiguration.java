package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Transformation Aware Heap Configuration
public class TAHeapConfiguration extends InternalHeapConfiguration {
    public final List<HeapTransformation> transformationBuffer = new LinkedList<>();

    public TAHeapConfiguration() {
        super();
    }

    public TAHeapConfiguration(TAHeapConfiguration heapConfiguration) {
        super(heapConfiguration);
        this.transformationBuffer.addAll(heapConfiguration.transformationBuffer);
    }

    public TAHeapConfiguration getBlankCopy() {
        TAHeapConfiguration copy = new TAHeapConfiguration(this);
        copy.transformationBuffer.clear();
        return copy;
    }

    void addTransformationStep(HeapTransformation step) {
        this.transformationBuffer.add(step);
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
