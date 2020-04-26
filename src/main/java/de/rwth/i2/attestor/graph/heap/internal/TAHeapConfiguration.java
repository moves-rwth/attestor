package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;

// Transformation Aware Heap Configuration
public class TAHeapConfiguration extends InternalHeapConfiguration {
    private TransformationLog transformationLog;

    public TAHeapConfiguration() {
        super();
    }

    public TAHeapConfiguration(TAHeapConfiguration heapConfiguration) {
        super(heapConfiguration);
        this.transformationLog = heapConfiguration.transformationLog;
    }

    public TAHeapConfiguration getBlankCopy() {
        TAHeapConfiguration copy = new TAHeapConfiguration(this);
        copy.transformationLog = null;
        return copy;
    }

    public TransformationLog getTransformationLog() {
        return transformationLog;
    }


    void setTransformationLog(TransformationLog transformationLog) {
        this.transformationLog = transformationLog;
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
