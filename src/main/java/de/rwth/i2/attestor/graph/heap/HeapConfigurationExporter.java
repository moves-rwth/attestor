package de.rwth.i2.attestor.graph.heap;


/**
 * A general method to export HeapConfigurations in a format that has to be specified by implementations.
 */
public interface HeapConfigurationExporter {


    /**
     * Exports a given HeapConfiguration.
     *
     * @param heapConfiguration The HeapConfiguration that should be exported.
     */
    void export(HeapConfiguration heapConfiguration);

    String exportForReport(HeapConfiguration heapConfiguration);
}
