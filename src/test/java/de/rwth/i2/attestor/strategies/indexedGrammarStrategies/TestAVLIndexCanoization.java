package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import static org.junit.Assert.*;

import org.junit.*;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.*;


public class TestAVLIndexCanoization {

	private AVLIndexCanonizationStrategy canonizer;

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setup(){
		canonizer = new AVLIndexCanonizationStrategy();
	}
	
	@Test
	public void testCanonizeStack() {
		HeapConfiguration graph = ExampleIndexedGraphFactory.getBalancedTreeLeft3();

		IndexedNonterminal leftNonterminal = getLabelOfVar(graph, "left");
		assertEquals("left before", 4, leftNonterminal.getIndex().size());
		assertTrue("left before", leftNonterminal.getIndex().hasConcreteStack());
		IndexedNonterminal rightNonterminal = getLabelOfVar(graph, "right");
		assertEquals("right before", 3, rightNonterminal.getIndex().size());
		assertTrue("right before", rightNonterminal.getIndex().hasConcreteStack());
		
		canonizer.canonizeStack(graph);

		//ensure original Nonterminals did not alter
		assertEquals("left before", 4, leftNonterminal.getIndex().size());
		assertTrue("left before", leftNonterminal.getIndex().hasConcreteStack());
		assertEquals("right before", 3, rightNonterminal.getIndex().size());
		assertTrue("right before", rightNonterminal.getIndex().hasConcreteStack());
		
		//ensure nonterminals now present are correctly altered
		IndexedNonterminal leftNonterminalRes = getLabelOfVar(graph, "left");

		assertEquals("left after abs", 2, leftNonterminalRes.getIndex().size());
		assertFalse("left after abs", leftNonterminalRes.getIndex().hasConcreteStack());
		assertEquals( leftNonterminalRes.getIndex().get( 0 ), ConcreteIndexSymbol.getStackSymbol( "s", false ));
		assertEquals( leftNonterminalRes.getIndex().get( 1 ), AbstractIndexSymbol.get( "X" ) );
		IndexedNonterminal rightNonterminalRes = getLabelOfVar(graph, "right");
		assertEquals("right after abs", 1, rightNonterminalRes.getIndex().size());
		assertEquals( rightNonterminalRes.getIndex().get( 0 ), AbstractIndexSymbol.get( "X" ) );
	}
	
	@Test
	public void testCanonizeStack2() {
		HeapConfiguration graph = ExampleIndexedGraphFactory.getCannotAbstractStack();
		
		IndexedNonterminal leftNonterminal = getLabelOfVar(graph, "left");
		
		assertEquals("left before",4, leftNonterminal.getIndex().size());
		assertFalse("left before",leftNonterminal.getIndex().hasConcreteStack());
		IndexedNonterminal rightNonterminal = getLabelOfVar(graph, "right");
		assertEquals("right before",3, rightNonterminal.getIndex().size());
		assertTrue("right before",rightNonterminal.getIndex().hasConcreteStack());
		
		canonizer.canonizeStack(graph);
		
		//IndexedNonterminal leftNonterminal = graph.getVariable("left").getTarget().getAttachedNonterminalEdges().get(0).computeAtomicPropositions();
		assertEquals("left after abs",4, leftNonterminal.getIndex().size());
		assertFalse("left after abs",leftNonterminal.getIndex().hasConcreteStack());
		//IndexedNonterminal rightNonterminal = graph.getVariable("right").getTarget().getAttachedNonterminalEdges().get(0).computeAtomicPropositions();
		assertEquals("right after abs",3, rightNonterminal.getIndex().size());
		assertTrue("right after abs",rightNonterminal.getIndex().hasConcreteStack());
	}
	
	private IndexedNonterminal getLabelOfVar(HeapConfiguration graph, String name) {
		
		int var = graph.variableWith(name);
		int tar = graph.targetOf(var);
		int ntEdge = graph.attachedNonterminalEdgesOf(tar).get(0);
		return (IndexedNonterminal) graph.labelOf(ntEdge);
	}
	
	@Test
	public void testCanonizeStack_Blocked(){
		HeapConfiguration input = ExampleIndexedGraphFactory.getInput_stackCanonization_Blocked();
		canonizer.canonizeStack(input);
		assertEquals(ExampleIndexedGraphFactory.getInput_stackCanonization_Blocked(), input);		
	}

}
