package de.rwth.i2.attestor.grammar.canoncalization;

import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.heap.matching.EmbeddingChecker;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.TerminalStatement;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnVoidStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultEmbeddingCheckerProviderTest {

	/**
	 * aggressiveAbstractionThreshold &gt; graphSize.
	 * aggressiveReturnAbstraction = true, statement != return
	 * expect DepthEmbeddingChecker
	 */
	@Test
	public void testSimpleCase() {
		int aggressiveAbstractionThreshold = 10;
		boolean aggressiveReturnAbstraction = true;
		HeapConfiguration graph = getGraphSmallerThan( aggressiveAbstractionThreshold );
		HeapConfiguration pattern = getPattern();
		Statement statement = new Skip(0);
		
		AbstractMatchingChecker expected = graph.getEmbeddingsOf( pattern, 1 );
		
		performTest( aggressiveAbstractionThreshold, aggressiveReturnAbstraction, 
					 graph, pattern, statement, expected );
	}
	
	/**
	 * aggressiveAbstractionThreshold &lt; graphSize.
	 * aggressiveReturnAbstraction = false, statement != return
	 * expect  aggressive EmbeddingChecker
	 */
	@Test
	@Deprecated
	@Ignore
	public void testLargeState() {
		int aggressiveAbstractionThreshold = 2;
		boolean aggressiveReturnAbstraction = false;
		HeapConfiguration graph = getGraphBiggerThan( aggressiveAbstractionThreshold );
		HeapConfiguration pattern = getPattern();
		Statement statement = new Skip(0);
		
		AbstractMatchingChecker expected = new EmbeddingChecker(pattern, graph );
		
		performTest( aggressiveAbstractionThreshold, aggressiveReturnAbstraction, 
					 graph, pattern, statement, expected );
	}

	
	/**
	 * aggressiveAbstractionThreshold &gt; graphSize.
	 * aggressiveReturnAbstraction = true, statement == return
	 * expect aggressive EmbeddingChecker
	 */
	@Test
	@Deprecated
	@Ignore
	public void testAggressiveReturn() {
		int aggressiveAbstractionThreshold = 10;
		boolean aggressiveReturnAbstraction = true;
		HeapConfiguration graph = getGraphSmallerThan( aggressiveAbstractionThreshold );
		HeapConfiguration pattern = getPattern();
		Semantics semantics = new TerminalStatement();
		
		AbstractMatchingChecker expected = new EmbeddingChecker(pattern, graph );
		
		performTest( aggressiveAbstractionThreshold, aggressiveReturnAbstraction, 
					 graph, pattern, semantics, expected );
	}

	/**
	 * aggressiveAbstractionThreshold &gt; graphSize.
	 * aggressiveReturnAbstraction = false, statement == return
	 * expect DepthEmbeddingChecker
	 */
	@Test
	public void testNormalReturn() {
		int aggressiveAbstractionThreshold = 10;
		boolean aggressiveReturnAbstraction = false;
		HeapConfiguration graph = getGraphSmallerThan( aggressiveAbstractionThreshold );
		HeapConfiguration pattern = getPattern();
		Statement statement = new ReturnVoidStmt();
		
		AbstractMatchingChecker expected = graph.getEmbeddingsOf( pattern, 1 );
		
		performTest( aggressiveAbstractionThreshold, aggressiveReturnAbstraction, 
					 graph, pattern, statement, expected );
	}


	private void performTest(int aggressiveAbstractionThreshold, boolean aggressiveReturnAbstraction,
			HeapConfiguration graph, HeapConfiguration pattern, Semantics semantics, AbstractMatchingChecker expected) {
		
		final int minDereferenceDepth = 1;
		EmbeddingCheckerProvider checkerProvider = 
				new EmbeddingCheckerProvider( minDereferenceDepth,
											  aggressiveAbstractionThreshold,
											  aggressiveReturnAbstraction 	);
		
	
		AbstractMatchingChecker checker = checkerProvider.getEmbeddingChecker( graph, pattern, semantics);
		
		assertEquals( expected.getClass(), checker.getClass() );
		assertEquals( expected.getPattern(), checker.getPattern());
		assertEquals( expected.getTarget(), checker.getTarget() );
	}

	private HeapConfiguration getPattern() {
	HeapConfiguration hc =  new InternalHeapConfiguration();
		
		Type type = Settings.getInstance().factory().getType("someType");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 1, nodes).build();
	}

	private HeapConfiguration getGraphSmallerThan( int aggressiveAbstractionThreshold ) {
		HeapConfiguration hc =  new InternalHeapConfiguration();
		
		Type type = Settings.getInstance().factory().getType("someType");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, aggressiveAbstractionThreshold - 1, nodes).build();
	}

	private HeapConfiguration getGraphBiggerThan(int aggressiveAbstractionThreshold) {
		HeapConfiguration hc =  new InternalHeapConfiguration();
		
		Type type = Settings.getInstance().factory().getType("someType");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, aggressiveAbstractionThreshold + 2, nodes).build();
	}
	
	

}
