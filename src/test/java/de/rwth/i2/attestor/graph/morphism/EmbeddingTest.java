package de.rwth.i2.attestor.graph.morphism;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.*;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2EmbeddingChecker;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2MinDepthEmbeddingChecker;
import de.rwth.i2.attestor.io.CustomHcListExporter;
import de.rwth.i2.attestor.io.jsonExport.JsonCustomHcListExporter;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.util.ZipUtils;

public class EmbeddingTest {

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Test 
	public void testIdenticalGraphs() {
		
		Graph g = (Graph) ExampleHcImplFactory.getListRule1();
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run(g, g);
		assertTrue("Identical graphs are embeddings of each other", checker.hasMorphism());
	}
	
	@Test 
	public void testSimpleDLLEmbedding() {
		
		Graph p = (Graph) ExampleHcImplFactory.getTwoElementDLL();
		Graph t = (Graph) ExampleHcImplFactory.getThreeElementDLL();
	
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run(p, t);
		assertTrue("Two element DLL is embedded in three element DLL", checker.hasMorphism());
	}
	
	@Test 
	public void testWithInternal() {

		Graph p = (Graph) ExampleHcImplFactory.getThreeElementDLL();
		Graph t = (Graph) ExampleHcImplFactory.getFiveElementDLL();
		
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run(p, t);
		assertTrue("Three element DLL is embedded in five element DLL, both with 2 external nodes", checker.hasMorphism());
	}
	
	@Test 
	public void testNegative() {
		

		Graph p = (Graph) ExampleHcImplFactory.getBrokenFourElementDLL();
		Graph t = (Graph) ExampleHcImplFactory.getFiveElementDLL();
		
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run(p, t);
		assertFalse("Three element DLL with one additional pointer not embedded in five element dll", checker.hasMorphism());
	}
	
	@Test
	public void testSLLRule2(){
	
		Graph p = (Graph) ExampleHcImplFactory.getListRule2();
		Graph t = (Graph) ExampleHcImplFactory.getListRule2Test();
		
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run(p, t);
		assertTrue( checker.hasMorphism() );
	}
	
	@Test
	public void testSLLRule2Fail(){
	
		Graph p = (Graph) ExampleHcImplFactory.getListRule2();
		Graph t = (Graph) ExampleHcImplFactory.getListRule2TestFail();
		
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run(p, t);
		assertFalse( checker.hasMorphism() );
	}
	
	@Test
	public void testSLLRule3(){
	
		Graph p = (Graph) ExampleHcImplFactory.getListRule3();
		Graph t = (Graph) ExampleHcImplFactory.getTestForListRule3();
		
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run(p, t);
		assertTrue( checker.hasMorphism() );
	}
	
	@Test
	public void testSLLRule3Fail(){
	
		Graph p = (Graph) ExampleHcImplFactory.getListRule3();
		Graph t = (Graph) ExampleHcImplFactory.getTestForListRule3Fail();
		
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run(p, t);
		assertFalse( checker.hasMorphism() );
	}
	
	@Test
	public void testAllExternal() {
		
		Graph p = (Graph) ExampleHcImplFactory.getTreeLeaf();
		Graph t = (Graph) ExampleHcImplFactory.get2TreeLeaf();
		
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run(p, t);
		assertTrue( checker.hasMorphism() );
		
	}
	
	@Test
	public void testDLLWithThirdPointer() {
		
		Graph p = (Graph) ExampleHcImplFactory.getDLL2Rule();
		Graph t = (Graph) ExampleHcImplFactory.getDLLTarget();
		
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run(p, t);
		assertTrue( checker.hasMorphism() );

	}
	
	@Test
	public void testHeapsWithDifferentIndices(){
		HeapConfiguration oneIndex = ExampleHcImplFactory.getInput_DifferentIndices_1();
		HeapConfiguration otherIndex = ExampleHcImplFactory.getInput_DifferentIndices_2();
		
		VF2EmbeddingChecker checker = new VF2EmbeddingChecker();
		checker.run( (Graph) oneIndex, (Graph) otherIndex);
		assertTrue(checker.hasMorphism());
	}
	
	@Test
	public void testAbstractionDistance_shouldFindEmbedding(){
		Settings.getInstance().options().setAggressiveNullAbstraction(true);
		
		HeapConfiguration inputWithEnoughDistance = ExampleHcImplFactory.getInput_EnoughAbstractionDistance();
		HeapConfiguration matchingPattern = ExampleHcImplFactory.getPattern_PathAbstraction();
		
		VF2MinDepthEmbeddingChecker checker = new VF2MinDepthEmbeddingChecker( 1 );
		checker.run( (Graph) matchingPattern, (Graph) inputWithEnoughDistance);
		assertTrue( checker.hasMorphism() );
	}
	
	@Test
	public void testAbstractionDistance_shouldNotFindEmbedding(){
		Settings.getInstance().options().setAggressiveNullAbstraction( true );
		
		HeapConfiguration inputWithoutEnoughDistance = ExampleHcImplFactory.getInput_NotEnoughAbstractionDistance();
		HeapConfiguration matchingPattern = ExampleHcImplFactory.getPattern_GraphAbstraction();
		
		VF2MinDepthEmbeddingChecker checker = new VF2MinDepthEmbeddingChecker( 1 );
		checker.run( (Graph) matchingPattern, (Graph) inputWithoutEnoughDistance);
		assertFalse( checker.hasMorphism() );
	}
	
	@Test
	public void testAbstractionDistance_onlyNonterminalEdge_shouldFindEmbedding() throws IOException{
final Settings settings = Settings.getInstance();
settings.options().setAggressiveNullAbstraction(true);
		
		HeapConfiguration inputWithEnoughDistance = ExampleHcImplFactory.getInput_OnlyNonterminalEdgesToAbstract();
		HeapConfiguration matchingPattern = ExampleHcImplFactory.getPattern_PathAbstraction();
		
		settings.output().setExportCustomHcs(true);
		settings.output().addCustomHc( "target", inputWithEnoughDistance );
		settings.output().addCustomHc( "pattern", matchingPattern );
		

	        String location = settings.output().getLocationForCustomHcs();

            // Copy necessary libraries
            InputStream zis = getClass().getClassLoader().getResourceAsStream("customHcViewer" +
                    ".zip");

            File targetDirectory = new File(location + File.separator);
            ZipUtils.unzip(zis, targetDirectory);

            // Generate JSON files for prebooked HCs and their summary
            CustomHcListExporter exporter = new JsonCustomHcListExporter();
            exporter.export(location + File.separator + "customHcsData", settings.output().getCustomHcSet());

           
		VF2MinDepthEmbeddingChecker checker = new VF2MinDepthEmbeddingChecker( 1 );
		checker.run( (Graph) matchingPattern, (Graph) inputWithEnoughDistance);
		assertTrue( checker.hasMorphism() );
	}
	
	
	
	
}
