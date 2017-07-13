package de.rwth.i2.attestor.graph.heap;


/**
 * A general method to export HeapConfigurations in a format that has to be specified by implementations.
 */
public interface HeapConfigurationExporter{


    /**
     * Exports a given HeapConfiguration into a file.
     * @param filename The name of the file the HeapConfiguration should be exported into.
     * @param heapConfiguration The HeapConfiguration that should be exported.
     */
	void export(String filename, HeapConfiguration heapConfiguration);
}
