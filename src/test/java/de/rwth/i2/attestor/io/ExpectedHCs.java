package de.rwth.i2.attestor.io;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.AbstractStackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.ConcreteStackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.StackVariable;
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
		
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);
		List<StackSymbol> stack = new ArrayList<>();
		stack.add(bottom);
		IndexedNonterminal nt = new IndexedNonterminal("TestJson", 2, new boolean[]{false, false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpected_TwoElementStack(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);
		List<StackSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(bottom);
		IndexedNonterminal nt = new IndexedNonterminal("TestJson", 2, new boolean[]{false,false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpected_StackWithVar(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		StackSymbol var = StackVariable.getGlobalInstance();
		List<StackSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(var);
		IndexedNonterminal nt = new IndexedNonterminal("TestJson", 2, new boolean[]{false,false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpected_StackWithAbs(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		StackSymbol abs = AbstractStackSymbol.get( "X" );
		List<StackSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(abs);
		IndexedNonterminal nt = new IndexedNonterminal("TestJson", 2, new boolean[]{false,false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
					.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
					.build();
	}

	public static HeapConfiguration getExpected_AbstractStack(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		StackSymbol abs = AbstractStackSymbol.get("X");
		List<StackSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(abs);
		IndexedNonterminal nt = new IndexedNonterminal("TestJson", 2, new boolean[]{false,false}, stack);
		
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

		StackSymbol var = StackVariable.getGlobalInstance();
		List<StackSymbol> stack = new ArrayList<>();
		stack.add(var);
		IndexedNonterminal nt = new IndexedNonterminal("TestJson", 2, new boolean[]{false,false}, stack);
		
		Type type = TypeFactory.getInstance().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
					.setExternal(nodes.get(0))
					.setExternal( nodes.get(1) )
					.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
					.build();
	}
	

}
