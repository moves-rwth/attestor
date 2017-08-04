package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import gnu.trove.list.array.TIntArrayList;

public class TestEmbeddings {
	
	private static HeapConfiguration rhs1;
	private static HeapConfiguration rhs2;
	
	@BeforeClass
	public static void init(){

		UnitTestGlobalSettings.reset();
		

		AnnotatedSelectorLabel leftLabel = new AnnotatedSelectorLabel("left", "0");
		AnnotatedSelectorLabel rightLabel = new AnnotatedSelectorLabel("right", "0");
		
		rhs1 = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		rhs1 = rhs1.builder().addNodes(TypeFactory.getInstance().getType("AVLTree"), 2, nodes)
						.setExternal( nodes.get(0))
						.setExternal(nodes.get(1))
						.addSelector(nodes.get(0), leftLabel, nodes.get(1))
						.addSelector(nodes.get(0), rightLabel, nodes.get(1))
						.build();
		
		Type zType = TypeFactory.getInstance().getType("int_0");
		AnnotatedSelectorLabel balance = new AnnotatedSelectorLabel("balancing", "");
		
		rhs2 = new InternalHeapConfiguration();
		TIntArrayList nodes2 = new TIntArrayList();
		rhs2 = rhs2.builder().addNodes(TypeFactory.getInstance().getType("AVLTree"), 2, nodes2)
						.addNodes(zType, 1, nodes2 )
						.setExternal( nodes2.get(0))
						.setExternal(nodes2.get(1))
						.setExternal( nodes2.get(2) )
						.addSelector(nodes2.get(0), leftLabel, nodes.get(1))
						.addSelector(nodes2.get(0), rightLabel, nodes.get(1))
						.addSelector( nodes2.get(0), balance, nodes2.get(2))
						.build();
	}

	@Test
	public void testCanonizePractical() {
		IndexedState input = new IndexedState( ExampleIndexedGraphFactory.getInput_practicalCanonize() );
		input.prepareHeap();
		

		
		AbstractMatchingChecker checker = input.getHeap().getEmbeddingsOf(rhs1);

        HeapConfigurationExporter exporter = Settings.getInstance()
				.factory()
				.getHeapConfigurationExporter(
						UnitTestGlobalSettings.getExportPath("TestEmbeddings")
				);

		exporter.export( "pattern1", rhs1 );
		exporter.export("target1", input.getHeap() );

		assertTrue( checker.hasNext() );
	}
	
	@Test
	public void testCanonizePractical2() {
		IndexedState input = new IndexedState( ExampleIndexedGraphFactory.getInput_practicalCanonize2() );
		input.prepareHeap();
				
		AbstractMatchingChecker checker = input.getHeap().getEmbeddingsOf(rhs2);

		HeapConfigurationExporter exporter = Settings.getInstance()
				.factory()
				.getHeapConfigurationExporter(
						UnitTestGlobalSettings.getExportPath("TestEmbeddings")
				);

		exporter.export( "pattern2", rhs2 );
		exporter.export("target2", input.getHeap() );

		assertTrue( checker.hasNext() );
	}
	
	@Test
	public void testCanonizePractical3() {
		IndexedState input = new IndexedState( ExampleIndexedGraphFactory.getInput_practicalCanonize3() );
		input.prepareHeap();
				
		AbstractMatchingChecker checker = input.getHeap().getEmbeddingsOf(
		        ExampleIndexedGraphFactory.getEmbedding_practicalCanonize3()
		);

		HeapConfigurationExporter exporter = Settings.getInstance()
				.factory()
				.getHeapConfigurationExporter(
						UnitTestGlobalSettings.getExportPath("TestEmbeddings")
				);

		exporter.export( "pattern3", ExampleIndexedGraphFactory.getEmbedding_practicalCanonize3() );
		exporter.export("target3", input.getHeap() );

		assertTrue( checker.hasNext() );
	}
	
	@Test
	public void testCanonizeWithInst() {
		IndexedState input = new IndexedState( ExampleIndexedGraphFactory.getInput_Cononize_withInstNecessary() );
		input.prepareHeap();
				
		AbstractMatchingChecker checker = input.getHeap().getEmbeddingsOf(
                ExampleIndexedGraphFactory.getRule_Cononize_withInstNecessary()
        );

		HeapConfigurationExporter exporter = Settings.getInstance()
				.factory()
				.getHeapConfigurationExporter(
						UnitTestGlobalSettings.getExportPath("TestEmbeddings")
				);

		exporter.export( "pattern4", ExampleIndexedGraphFactory.getRule_Cononize_withInstNecessary() );
		exporter.export("target4", input.getHeap() );

		assertTrue( checker.hasNext() );
	}
	
	@Test
	public void testEmbedding5() {
		//smaller version of testCanonizeWithInst()
		IndexedState input = new IndexedState( ExampleIndexedGraphFactory.getInput_Embedding5() );
		input.prepareHeap();
				
		AbstractMatchingChecker checker = input.getHeap().getEmbeddingsOf(
                ExampleIndexedGraphFactory.getRule_Cononize_withInstNecessary()
        );

		HeapConfigurationExporter exporter = Settings.getInstance()
				.factory()
				.getHeapConfigurationExporter(
						UnitTestGlobalSettings.getExportPath("TestEmbeddings")
				);

		exporter.export( "pattern5", ExampleIndexedGraphFactory.getRule_Cononize_withInstNecessary() );
		exporter.export("target5", input.getHeap() );

		assertTrue( checker.hasNext() );
	}
}
