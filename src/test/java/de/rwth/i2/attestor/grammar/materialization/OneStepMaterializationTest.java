package de.rwth.i2.attestor.grammar.materialization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.materialization.strategies.OneStepMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.OneStepMaterializationStrategyBuilder;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.DefaultScene;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.*;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public class OneStepMaterializationTest {
	
	public final static String VAR = "x";
	Scene scene;
	OneStepMaterializationStrategyBuilder strategyBuilder;

	@Before
	public void setUp() throws Exception {
		scene = new DefaultScene();
		strategyBuilder = new OneStepMaterializationStrategyBuilder();
	}

	@Test
	public void testTwoRules() {
		
		Nonterminal nt = scene.createNonterminal("Nt", 2, new boolean[]{false,false});
		Grammar grammar = Grammar.builder().addRule(nt, aSimpleRhs())
						.addRule(nt, anotherSimpleRhs())
						.build();
		strategyBuilder.setGrammar(grammar)
					    .setIndexedMode(false);
		OneStepMaterializationStrategy materializer = strategyBuilder.build();
		
		Collection<HeapConfiguration> res = materializer.materialize(aSimpleInput(nt));
		
		assertThat( res, containsInAnyOrder( aSimpleRhsApplied(), anotherSimpleRhsApplied() ));
	}
	
	@Test
	public void testTwoNonterminals(){

		Nonterminal nt = scene.createNonterminal("Nt", 2, new boolean[]{false,false});
		SelectorLabel sel = scene.getSelectorLabel("some label");
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration rhs = concreteHeap(nodes, sel);
		makeNodesExternal(rhs, nodes);
		Grammar grammar = Grammar.builder().addRule(nt, rhs )
						.build();
		strategyBuilder.setGrammar(grammar)
					    .setIndexedMode(false);
		OneStepMaterializationStrategy materializer = strategyBuilder.build();
		
		Collection<HeapConfiguration> res = materializer.materialize(aLongerInput(nt));
		TIntArrayList nodes1 = new TIntArrayList();
		HeapConfiguration expectedRes1 = mixedHeapNtLeft(nodes1, sel, nt);
		addVariable(expectedRes1, nodes1);
		
		TIntArrayList nodes2 = new TIntArrayList();
		HeapConfiguration expectedRes2 = mixedHeapNtRight(nodes2, sel, nt);
		addVariable(expectedRes2, nodes2);
		
		assertThat( res, containsInAnyOrder( expectedRes1, expectedRes2 ));
	}
	
	@Test
	public void testNonConcreteRule(){
		Nonterminal nt = scene.createNonterminal("Nt", 2, new boolean[]{false,false});
		SelectorLabel sel = scene.getSelectorLabel("some label");
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration rhs = mixedHeapNtLeft(nodes, sel, nt);
		makeNodesExternal(rhs, nodes);
		Grammar grammar = Grammar.builder().addRule(nt, rhs )
						.build();
		strategyBuilder.setGrammar(grammar)
					    .setIndexedMode(false);
		OneStepMaterializationStrategy materializer = strategyBuilder.build();
		
		Collection<HeapConfiguration> res = materializer.materialize(aSimpleInput(nt));
		TIntArrayList nodes1 = new TIntArrayList();
		HeapConfiguration expectedRes1 = mixedHeapNtLeft(nodes1, sel, nt);
		addVariable(expectedRes1, nodes1);
		
		assertThat( res, containsInAnyOrder( expectedRes1 ));
	}
	
	@Test
	public void testIndexedCase(){
		Nonterminal nt = scene.createNonterminal("Nt", 2, new boolean[]{false,false});
		List<IndexSymbol> stack1 = new ArrayList<>();
		stack1.add(ConcreteIndexSymbol.getIndexSymbol("Z", true));
		IndexedNonterminal nt1 = new IndexedNonterminalImpl(nt, stack1);
		
		List<IndexSymbol> stack2 = new ArrayList<>();
		stack2.add(ConcreteIndexSymbol.getIndexSymbol("s", false));
		stack2.add( IndexVariable.getIndexVariable() );
		IndexedNonterminal nt2 = new IndexedNonterminalImpl(nt, stack2);
		
		Grammar grammar = Grammar.builder().addRule(nt1, aSimpleRhs())
							.addRule(nt2, anotherSimpleRhs())
							.build();

		strategyBuilder.setGrammar(grammar)
	    			   .setIndexedMode(true);
		OneStepMaterializationStrategy materializer = strategyBuilder.build();
		
		List<IndexSymbol> stack4 = new ArrayList<>();
		stack4.add( AbstractIndexSymbol.get("X") );
		IndexedNonterminal nt4 = new IndexedNonterminalImpl(nt, stack4);
		
		HeapConfiguration input = aSimpleInput(nt4);
		
		Collection<HeapConfiguration> res = materializer.materialize(input);
		assertThat( res, containsInAnyOrder( aSimpleRhsApplied(), anotherSimpleRhsApplied() ));
		
	}
	
	private HeapConfiguration aLongerInput(Nonterminal nt){
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration hc = nonterminalChain(2, nt, nodes);
		return addVariable(hc, nodes);
	}
	
	private HeapConfiguration aSimpleInput(Nonterminal nt){
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration hc = nonterminalChain(1, nt, nodes);
		return addVariable(hc, nodes);
	}
	
	private HeapConfiguration anotherSimpleRhsApplied(){
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration anotherGraph = anotherHeap(nodes);
		return addVariable(anotherGraph, nodes);
	}
	
	private HeapConfiguration anotherSimpleRhs(){
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration anotherGraph = anotherHeap(nodes);
		return makeNodesExternal(anotherGraph, nodes);
	}
	
	private HeapConfiguration aSimpleRhsApplied(){
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration someGraph = someHeap(nodes);
		return addVariable(someGraph, nodes);
	}
	
	private HeapConfiguration aSimpleRhs(){
		TIntArrayList nodes = new TIntArrayList();
		HeapConfiguration someGraph = someHeap(nodes);
		return makeNodesExternal(someGraph, nodes);
	}
	
	private HeapConfiguration someHeap( TIntArrayList nodes ){
		SelectorLabel someSelector = scene.getSelectorLabel("some selector");
		return concreteHeap( nodes, someSelector );
	}
	
	private HeapConfiguration anotherHeap( TIntArrayList nodes ){
		SelectorLabel anotherLabel = scene.getSelectorLabel("another label");
		return concreteHeap(nodes, anotherLabel);
	}
	
	//to ensure an orientation
	private HeapConfiguration addVariable(HeapConfiguration hc, TIntArrayList nodes) {
		return hc.builder().addVariableEdge(VAR, nodes.get(0)).build();
		
	}
	
	private HeapConfiguration makeNodesExternal(HeapConfiguration hc, TIntArrayList nodes ){
		return hc.builder().setExternal(nodes.get(0))
				.setExternal(nodes.get(nodes.size() - 1))
				.build();
	}
	
	private HeapConfiguration nonterminalChain( int numberOfNonterminals, Nonterminal nt, TIntArrayList nodes ){
		HeapConfigurationBuilder builder = new InternalHeapConfiguration().builder();
		Type type = scene.getType("type");
		
		builder.addNodes(type, numberOfNonterminals + 1, nodes);
		
		for( int i = 0; i < numberOfNonterminals; i++ ){
			builder.addNonterminalEdge(nt)
				.addTentacle(nodes.get(i))
				.addTentacle(nodes.get(i+1))
				.build();
		}
		
		return builder.build();
	}
	
	private HeapConfiguration concreteHeap( TIntArrayList nodes, SelectorLabel sel ){
		HeapConfigurationBuilder builder = new InternalHeapConfiguration().builder();
		Type type = scene.getType("type");
		
		builder.addNodes(type, 2, nodes)
			.addSelector(nodes.get(0), sel, nodes.get(1));
		
		return builder.build();
	}
	
	private HeapConfiguration mixedHeapNtRight( TIntArrayList nodes, SelectorLabel sel, Nonterminal nt ){
		HeapConfigurationBuilder builder = new InternalHeapConfiguration().builder();
		Type type = scene.getType("type");
		
		return builder.addNodes(type, 3, nodes)
				.addSelector(nodes.get(0), sel, nodes.get(1))
				.addNonterminalEdge(nt)
					.addTentacle(nodes.get(1))
					.addTentacle(nodes.get(2))
					.build()
				.build();
	}
	
	private HeapConfiguration mixedHeapNtLeft( TIntArrayList nodes, SelectorLabel sel, Nonterminal nt ){
		HeapConfigurationBuilder builder = new InternalHeapConfiguration().builder();
		Type type = scene.getType("type");
		
		return builder.addNodes(type, 3, nodes)
				.addSelector(nodes.get(1), sel, nodes.get(2))
				.addNonterminalEdge(nt)
					.addTentacle(nodes.get(0))
					.addTentacle(nodes.get(1))
					.build()
				.build();
	}

}
