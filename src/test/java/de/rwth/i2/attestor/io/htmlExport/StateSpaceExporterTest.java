package de.rwth.i2.attestor.io.htmlExport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceExporter;
import org.junit.*;

public class StateSpaceExporterTest {
	//private static final Logger logger = LogManager.getLogger( "StateSpaceExporterTest.java" );

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() throws Exception{
	}

	@Test
	public void testGetInstance(){
		StateSpaceExporter t1 = Settings.getInstance().factory().getStateSpaceExporter(UnitTestGlobalSettings.getExportPath("dir"));
		StateSpaceExporter t2 = Settings.getInstance().factory().getStateSpaceExporter(UnitTestGlobalSettings.getExportPath("dir"));

		assertEquals( "same name should yield same exporter", t1, t2 );

		StateSpaceExporter t3 = Settings.getInstance().factory().getStateSpaceExporter(UnitTestGlobalSettings.getExportPath("test"));

		assertNotEquals( "different names should yield different exporters", t1, t3 );
	}
	
	@Test
	public void testStateSpaceToJson(){
		TestStateSpaceInput testSSP = new TestStateSpaceInput();
		String jsonString = StateSpaceHtmlExporter.stateSpaceToJson( testSSP );
		String expected = "elements: { \n"
							+" nodes:[\n"
							+"{ data: { id: '0', type: 'state' } },\n"
							+"{ data: { id: '1', type: 'state' } },\n"
							+" ],\n"
							+"edges: [\n"
							+" { data: { source: '0', target: '1', label: 'label', type: 'selector' } },\n"
							+"]\n"
							+"},";
		
		assertEquals( "json not as expected", expected.replaceAll("\\s",""), jsonString.replaceAll("\\s","") );
	}
}
