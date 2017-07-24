package de.rwth.i2.attestor.indexedGrammars;

import static org.junit.Assert.*;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.stack.*;

public class TestAVLStackCanoization {

	private AVLStackCanonizationStrategy canonizer;

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setup(){
		canonizer = new AVLStackCanonizationStrategy();
	}
	
	@Test
	public void testCanonizeStack() {
		HeapConfiguration graph = ExampleIndexedGraphFactory.getBalancedTreeLeft3();

		IndexedNonterminal leftNonterminal = getLabelOfVar(graph, "left");
		assertEquals("left before", 4, leftNonterminal.getStack().size());
		assertTrue("left before", leftNonterminal.getStack().hasConcreteStack());
		IndexedNonterminal rightNonterminal = getLabelOfVar(graph, "right");
		assertEquals("right before", 3, rightNonterminal.getStack().size());
		assertTrue("right before", rightNonterminal.getStack().hasConcreteStack());
		
		canonizer.canonizeStack(graph);

		//ensure original Nonterminals did not alter
		assertEquals("left before", 4, leftNonterminal.getStack().size());
		assertTrue("left before", leftNonterminal.getStack().hasConcreteStack());
		assertEquals("right before", 3, rightNonterminal.getStack().size());
		assertTrue("right before", rightNonterminal.getStack().hasConcreteStack());
		
		//ensure nonterminals now present are correctly altered
		IndexedNonterminal leftNonterminalRes = getLabelOfVar(graph, "left");
		assertEquals("left after abs", 2, leftNonterminalRes.getStack().size());
		assertFalse("left after abs", leftNonterminalRes.getStack().hasConcreteStack());
		assertEquals( leftNonterminalRes.getStackAt( 0 ), ConcreteStackSymbol.getStackSymbol( "s", false ));
		assertEquals( leftNonterminalRes.getStackAt( 1 ), AbstractStackSymbol.get( "X" ) );
		IndexedNonterminal rightNonterminalRes = getLabelOfVar(graph, "right");
		assertEquals("right after abs", 1, rightNonterminalRes.getStack().size());
		assertEquals( rightNonterminalRes.getStackAt( 0 ), AbstractStackSymbol.get( "X" ) );
	}
	
	@Test
	public void testCanonizeStack2() {
		HeapConfiguration graph = ExampleIndexedGraphFactory.getCannotAbstractStack();
		
		IndexedNonterminal leftNonterminal = getLabelOfVar(graph, "left");
		
		assertEquals("left before",4, leftNonterminal.getStack().size());
		assertFalse("left before",leftNonterminal.getStack().hasConcreteStack());
		IndexedNonterminal rightNonterminal = getLabelOfVar(graph, "right");
		assertEquals("right before",3, rightNonterminal.getStack().size());
		assertTrue("right before",rightNonterminal.getStack().hasConcreteStack());
		
		canonizer.canonizeStack(graph);
		
		//IndexedNonterminal leftNonterminal = graph.getVariable("left").getTarget().getAttachedNonterminalEdges().get(0).computeAtomicPropositions();
		assertEquals("left after abs",4, leftNonterminal.getStack().size());
		assertFalse("left after abs",leftNonterminal.getStack().hasConcreteStack());
		//IndexedNonterminal rightNonterminal = graph.getVariable("right").getTarget().getAttachedNonterminalEdges().get(0).computeAtomicPropositions();
		assertEquals("right after abs",3, rightNonterminal.getStack().size());
		assertTrue("right after abs",rightNonterminal.getStack().hasConcreteStack());
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
