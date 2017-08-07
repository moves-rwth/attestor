package de.rwth.i2.attestor.io.htmlExport;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.main.settings.Settings;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;

public class HtmlExporterTest {


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Test
	public void simpleHcExport() {
		
		HeapConfigurationExporter exporter = Settings.getInstance().factory()
				.getHeapConfigurationExporter(UnitTestGlobalSettings.getExportPath("htmlExporterTest"));

		
		HeapConfiguration  hc1 = ExampleHcImplFactory.getBadTwoElementDLL();
		exporter.export("bad dll", hc1); // name of heap configurations + actual heap configuration
		
		HeapConfiguration hc2 = ExampleHcImplFactory.getLargerTree();
		exporter.export("large tree", hc2);
		
		HeapConfiguration hc4 = ExampleHcImplFactory.getListAndConstants();
		exporter.export("list + constants", hc4);
		
		HeapConfiguration hc5 = ExampleHcImplFactory.getThreeElementDLL();
		exporter.export("dll", hc5);
		
		HeapConfiguration hc6 = ExampleHcImplFactory.getTLLRule();
		exporter.export("tll", hc6);
		
		HeapConfiguration hc7 = ExampleHcImplFactory.getTree();
		exporter.export("tree", hc7);
	}
}
