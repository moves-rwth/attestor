package de.rwth.i2.attestor.io;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.*;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexVariable;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import gnu.trove.list.array.TIntArrayList;

class ExpectedHCs {
	
	public static HeapConfiguration getExpected_Annotated(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		AnnotatedSelectorLabel sel = new AnnotatedSelectorLabel("label", "ann");
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addSelector(nodes.get(0), sel, nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getExpected_Bottom(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		IndexSymbol bottom = ConcreteIndexSymbol.getStackSymbol("Z", true);
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(bottom);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false, false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpected_TwoElementStack(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		IndexSymbol s = ConcreteIndexSymbol.getStackSymbol("s", false);
		IndexSymbol bottom = ConcreteIndexSymbol.getStackSymbol("Z", true);
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(bottom);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpected_StackWithVar(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		IndexSymbol s = ConcreteIndexSymbol.getStackSymbol("s", false);
		IndexSymbol var = IndexVariable.getGlobalInstance();
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(var);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpected_StackWithAbs(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		IndexSymbol s = ConcreteIndexSymbol.getStackSymbol("s", false);
		IndexSymbol abs = AbstractIndexSymbol.get( "X" );
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(abs);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
					.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
					.build();
	}

	public static HeapConfiguration getExpected_AbstractStack(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		IndexSymbol s = ConcreteIndexSymbol.getStackSymbol("s", false);
		IndexSymbol abs = AbstractIndexSymbol.get("X");
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(abs);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
					.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
					.build();
	}
	
	public static HeapConfiguration getExpected_Rule2(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		AnnotatedSelectorLabel sel = new AnnotatedSelectorLabel("label", "ann");
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.setExternal(nodes.get(0))
				.setExternal( nodes.get(1) )
				.addSelector(nodes.get(0), sel, nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getExpected_Rule1(){
		HeapConfiguration hc = new InternalHeapConfiguration();

		IndexSymbol var = IndexVariable.getGlobalInstance();
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(var);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
					.setExternal(nodes.get(0))
					.setExternal( nodes.get(1) )
					.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
					.build();
	}
	

}
