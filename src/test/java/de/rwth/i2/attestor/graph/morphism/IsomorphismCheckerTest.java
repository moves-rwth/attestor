package de.rwth.i2.attestor.graph.morphism;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2IsomorphismChecker;

public class IsomorphismCheckerTest {
	
	@Test 
	public void checkIdenticalGraphs() {
		
		Graph g = (Graph) ExampleHcImplFactory.getListRule1();
		VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
		checker.run(g,g);
		assert(checker.hasMorphism());
	}
	
	@Test 
	public void checkClonedGraphs() {
		
		HeapConfiguration g1 = ExampleHcImplFactory.getListRule1();
		HeapConfiguration g2 = g1.clone();
		
		VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
		checker.run( (Graph) g1, (Graph) g2);
		assert(checker.hasMorphism());
	}
	
	@Test 
	public void checkTwoFactoryCopies() {
		
		HeapConfiguration g1 = ExampleHcImplFactory.getListRule1();
		HeapConfiguration g2 = ExampleHcImplFactory.getListRule1();
		
		VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
		checker.run( (Graph) g1, (Graph) g2);
		assert(checker.hasMorphism());
	}
	
	@Test
	public void checkNegative() {
		
		HeapConfiguration g1 = ExampleHcImplFactory.getListRule1();
		HeapConfiguration g2 = ExampleHcImplFactory.getListRule2();
		
		VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
		checker.run( (Graph) g1, (Graph) g2);
		assertFalse(checker.hasMorphism());
	}
	
	@Test
	public void checkIdenticalWithNonterminals() {
	
		HeapConfiguration g1 = ExampleHcImplFactory.getListRule3();
		
		VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
		checker.run( (Graph) g1, (Graph) g1);	
		
		assert(checker.hasMorphism());
	
	}

	@Test 
	public void checkTwoFactoryCopiesWithNonterminals() {
		
		HeapConfiguration g1 = ExampleHcImplFactory.getListRule3();
		HeapConfiguration g2 = ExampleHcImplFactory.getListRule3();
		
		VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
		checker.run( (Graph) g1, (Graph) g2);
		
		assert(checker.hasMorphism());
	}
	
	@Test
	public void testGraphWithConstants() {
		
		HeapConfiguration g1 = ExampleHcImplFactory.getListAndConstantsWithChange();
		HeapConfiguration g2 = ExampleHcImplFactory.getListAndConstantsWithChange().clone();
		
		VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
		checker.run( (Graph) g1, (Graph) g2);
		
		assert(checker.hasMorphism());
		
	}
	
	@Test
	public void testExpectedResultBoolList(){
		
		HeapConfiguration g1 = ExampleHcImplFactory.getExpectedResult_AssignStmt();
		HeapConfiguration g2 = ExampleHcImplFactory.getExpectedResult_AssignStmt();
		
		int var = g2.variableWith("XYZ");
		int node = g2.targetOf( var );
		
		g2.builder()
			.removeVariableEdge(var)
			.addVariableEdge("XYZ", node)
			.build();
		
		VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
		checker.run( (Graph) g1, (Graph) g2);
		
		assert(checker.hasMorphism());
	}
	
	@Test
	public void testRejectSubgraphs() {
		
		HeapConfiguration empty = new InternalHeapConfiguration();
		HeapConfiguration list = ExampleHcImplFactory.getList();
		
		
		VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
		checker.run( (Graph) empty, (Graph) list);
		assertFalse(checker.hasMorphism());	
	}
	
	@Test
	public void testHeapsWithDifferentStacks(){
		HeapConfiguration oneStack = ExampleHcImplFactory.getInput_DifferentStacks_1();
		HeapConfiguration otherStack = ExampleHcImplFactory.getInput_DifferentStacks_2();
		
		VF2IsomorphismChecker checker = new VF2IsomorphismChecker();
		checker.run( (Graph) oneStack, (Graph) otherStack);
		assertFalse(checker.hasMorphism());
	}
	
}
