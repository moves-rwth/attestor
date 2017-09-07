package de.rwth.i2.attestor.io;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexVariable;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

class ExpectedHCs {
	
	public static HeapConfiguration getExpected_Annotated(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		AnnotatedSelectorLabel sel = new AnnotatedSelectorLabel("label", "ann");
		
		Type type = Settings.getInstance().factory().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addSelector(nodes.get(0), sel, nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getExpected_Bottom(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
		List<IndexSymbol> index = new ArrayList<>();
		index.add(bottom);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false, false}, index);
		
		Type type = Settings.getInstance().factory().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpected_TwoElementIndex(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
		IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
		List<IndexSymbol> index = new ArrayList<>();
		index.add(s);
		index.add(bottom);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,false}, index);
		
		Type type = Settings.getInstance().factory().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpected_IndexWithVar(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
		IndexSymbol var = IndexVariable.getIndexVariable();
		List<IndexSymbol> index = new ArrayList<>();
		index.add(s);
		index.add(var);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,false}, index);
		
		Type type = Settings.getInstance().factory().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpected_IndexWithAbs(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
		IndexSymbol abs = AbstractIndexSymbol.get( "X" );
		List<IndexSymbol> index = new ArrayList<>();
		index.add(s);
		index.add(abs);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,false}, index);
		
		Type type = Settings.getInstance().factory().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
					.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
					.build();
	}

	public static HeapConfiguration getExpected_AbstractIndex(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
		IndexSymbol abs = AbstractIndexSymbol.get("X");
		List<IndexSymbol> index = new ArrayList<>();
		index.add(s);
		index.add(abs);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,false}, index);
		
		Type type = Settings.getInstance().factory().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
					.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
					.build();
	}
	
	public static HeapConfiguration getExpected_Rule2(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		AnnotatedSelectorLabel sel = new AnnotatedSelectorLabel("label", "ann");
		
		Type type = Settings.getInstance().factory().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.setExternal(nodes.get(0))
				.setExternal( nodes.get(1) )
				.addSelector(nodes.get(0), sel, nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getExpected_Rule1(){
		HeapConfiguration hc = new InternalHeapConfiguration();

		IndexSymbol var = IndexVariable.getIndexVariable();
		List<IndexSymbol> index = new ArrayList<>();
		index.add(var);
		IndexedNonterminal nt = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,true}, index);
		
		Type type = Settings.getInstance().factory().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
					.setExternal(nodes.get(0))
					.setExternal( nodes.get(1) )
					.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
					.build();
	}
	

}
