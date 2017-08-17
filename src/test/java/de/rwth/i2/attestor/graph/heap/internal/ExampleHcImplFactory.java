package de.rwth.i2.attestor.graph.heap.internal;


import de.rwth.i2.attestor.graph.GeneralNonterminal;
import de.rwth.i2.attestor.graph.GeneralSelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

public final class ExampleHcImplFactory {
	
	public static HeapConfiguration getSimpleDLL() {
		
		HeapConfiguration result = new InternalHeapConfiguration();
		GeneralSelectorLabel sel = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralNonterminal nt = GeneralNonterminal.getNonterminal("3", 3, new boolean[]{false, false, false});
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();
		
		return  result.builder()
				.addNodes(type, 3, nodes)
				.addSelector( nodes.get(0) , sel , nodes.get(1))
				.addSelector(nodes.get(1), sel, nodes.get(2))
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(2)}))
				.addVariableEdge("x", nodes.get(0))
				.build();
	}

	public static HeapConfiguration getBadTwoElementDLL() {
		
		HeapConfiguration result = new InternalHeapConfiguration();
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralSelectorLabel prev = GeneralSelectorLabel.getSelectorLabel("prev");
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();

		return result.builder()
				.addNodes(type, 2, nodes )
				.addSelector( nodes.get( 0 ), next, nodes.get( 1 ) )
				.addSelector( nodes.get( 1 ), prev, nodes.get( 0 ) )
				.addSelector( nodes.get( 1 ), next, nodes.get( 0 ) )
				.setExternal( nodes.get( 0 ) )
				.setExternal( nodes.get( 1 ) )
				.build();
	}

	public static HeapConfiguration getTwoElementDLL() {

		HeapConfiguration result = new InternalHeapConfiguration();
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralSelectorLabel prev = GeneralSelectorLabel.getSelectorLabel("prev");
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();

		return result.builder()
				.addNodes(type, 2, nodes )
				.addSelector( nodes.get( 0 ), next, nodes.get( 1 ) )
				.addSelector( nodes.get( 1 ), prev, nodes.get( 0 ) )
				.setExternal( nodes.get( 0 ) )
				.setExternal( nodes.get( 1 ) )
				.build();
	}
	
	public static HeapConfiguration getThreeElementDLL() {

		HeapConfiguration result = new InternalHeapConfiguration();
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralSelectorLabel prev = GeneralSelectorLabel.getSelectorLabel("prev");
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(type, 3, nodes )
				.addSelector( nodes.get( 0 ), next, nodes.get( 1 ) )
				.addSelector( nodes.get( 1 ), prev, nodes.get( 0 ) )
				.addSelector( nodes.get( 1 ), next, nodes.get( 2 ) )
				.addSelector( nodes.get( 2 ), prev, nodes.get( 1 ) )
				.setExternal( nodes.get( 0 ) )
				.setExternal( nodes.get( 2 ) )
				.build();
	}
	
	public static HeapConfiguration getFiveElementDLL() {

		HeapConfiguration result = new InternalHeapConfiguration();
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralSelectorLabel prev = GeneralSelectorLabel.getSelectorLabel("prev");
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(type, 5, nodes )
				.addSelector( nodes.get( 0 ), next, nodes.get( 1 ) )
				.addSelector( nodes.get( 1 ), prev, nodes.get( 0 ) )
				.addSelector( nodes.get( 1 ), next, nodes.get( 2 ) )
				.addSelector( nodes.get( 2 ), prev, nodes.get( 1 ) )
				.addSelector( nodes.get( 2 ), next, nodes.get( 3 ) )
				.addSelector( nodes.get( 3 ), prev, nodes.get( 2 ) )
				.addSelector( nodes.get( 3 ), next, nodes.get( 4 ) )
				.addSelector( nodes.get( 4 ), prev, nodes.get( 3 ) )
				.setExternal( nodes.get( 0 ) )
				.setExternal( nodes.get( 2 ) )
				.build();
	}
	
	public static HeapConfiguration getBrokenFourElementDLL() {

		HeapConfiguration result = new InternalHeapConfiguration();
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralSelectorLabel prev = GeneralSelectorLabel.getSelectorLabel("prev");
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(type, 4, nodes )
				.addSelector( nodes.get( 0 ), next, nodes.get( 1 ) )
				.addSelector( nodes.get( 1 ), prev, nodes.get( 0 ) )
				.addSelector( nodes.get( 1 ), next, nodes.get( 2 ) )
				.addSelector( nodes.get( 2 ), prev, nodes.get( 1 ) )
				.addSelector( nodes.get( 2 ), next, nodes.get( 3 ) )
				.addSelector( nodes.get( 3 ), prev, nodes.get( 2 ) )
				.setExternal( nodes.get( 0 ) )
				.setExternal( nodes.get( 2 ) )
				.build();
	}

	public static HeapConfiguration getTLLRule() {

		HeapConfiguration result = new InternalHeapConfiguration();
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();

		GeneralSelectorLabel left = GeneralSelectorLabel.getSelectorLabel("left");
		GeneralSelectorLabel right = GeneralSelectorLabel.getSelectorLabel("right");
		GeneralSelectorLabel parent = GeneralSelectorLabel.getSelectorLabel("parent");

		GeneralNonterminal nT = GeneralNonterminal.getNonterminal( "TLL", 4, new boolean[]{false,false,false,false} );

		return result.builder()
				.addNodes(type, 7,  nodes )
				.addSelector( nodes.get( 0 ), left, nodes.get( 1 ) )
				.addSelector( nodes.get( 0 ), right, nodes.get( 2 ) )
				.addSelector( nodes.get( 0 ), parent, nodes.get( 3 ) )
				.addVariableEdge( "XYZ", nodes.get(3) )
				.addVariableEdge( "ZYX", nodes.get( 0 ) )
				.addNonterminalEdge( nT, new TIntArrayList(new int[]{nodes.get(1), nodes.get(0), nodes.get(5), nodes.get(4)}) )
				.addNonterminalEdge( nT, new TIntArrayList(new int[]{nodes.get(2), nodes.get(0), nodes.get(5), nodes.get(6)}) )
				.setExternal( nodes.get( 0 ) )
				.setExternal( nodes.get( 3 ) )
				.setExternal( nodes.get( 4 ) )
				.setExternal( nodes.get( 6 ) )
				.build();		 	 		
	}

	public static HeapConfiguration getTLLRulePermuted() {

		HeapConfiguration result = new InternalHeapConfiguration();
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();

		GeneralSelectorLabel left = GeneralSelectorLabel.getSelectorLabel("left");
		GeneralSelectorLabel right = GeneralSelectorLabel.getSelectorLabel("right");
		GeneralSelectorLabel parent = GeneralSelectorLabel.getSelectorLabel("parent");

		GeneralNonterminal nT = GeneralNonterminal.getNonterminal( "Tree", 4, new boolean[]{false,false,false,false} );
		
		return result.builder()
				.addNodes(type, 7, nodes )
				.addSelector( nodes.get( 1 ), left, nodes.get( 0 ) )
				.addSelector( nodes.get( 1 ), right, nodes.get( 2 ) )
				.addSelector( nodes.get( 1 ), parent, nodes.get( 3 ) )
				.addVariableEdge( "XYZ", nodes.get(3) )
				.addVariableEdge( "ZYX", nodes.get( 1 ) )
				.addNonterminalEdge( nT, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(5), nodes.get(4)}) )
				.addNonterminalEdge( nT, new TIntArrayList(new int[]{nodes.get(2), nodes.get(1), nodes.get(5), nodes.get(6)}) )
				.setExternal( nodes.get( 1 ) )
				.setExternal( nodes.get( 3 ) )
				.setExternal( nodes.get( 4 ) )
				.setExternal( nodes.get( 6 ) )
				.build();		 	 		
	}

	public static HeapConfiguration getTree() {
		
		HeapConfiguration result = new InternalHeapConfiguration();
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();

		GeneralSelectorLabel left = GeneralSelectorLabel.getSelectorLabel("left");
		GeneralSelectorLabel right = GeneralSelectorLabel.getSelectorLabel("right");

		return result.builder()
				.addNodes(type, 3, nodes )
				.addSelector( nodes.get( 0 ), left, nodes.get( 1 ) )
				.addSelector( nodes.get( 0 ), right, nodes.get( 2 ) )
				.setExternal( nodes.get( 1 ) )
				.setExternal( nodes.get( 2 ) )
				.build();
	}

	public static HeapConfiguration getLargerTree() {

		HeapConfiguration result = new InternalHeapConfiguration();
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();

		GeneralSelectorLabel left = GeneralSelectorLabel.getSelectorLabel("left");
		GeneralSelectorLabel right = GeneralSelectorLabel.getSelectorLabel("right");
		GeneralSelectorLabel parent = GeneralSelectorLabel.getSelectorLabel("parent");

		return result.builder()
				.addNodes(type, 6, nodes )
				.addSelector( nodes.get( 1 ), parent, nodes.get( 0 ) )
				.addSelector( nodes.get( 1 ), left, nodes.get( 2 ) )
				.addSelector( nodes.get( 1 ), right, nodes.get( 3 ) )
				.addSelector( nodes.get( 2 ), left, nodes.get( 4 ) )
				.addSelector( nodes.get( 2 ), right, nodes.get( 5 ) )
				.setExternal( nodes.get( 2 ) )
				.setExternal( nodes.get( 5 ) )
				.build();
	}

	public static HeapConfiguration getLargerTreeWithOutExternals() {

		HeapConfiguration result = new InternalHeapConfiguration();
		Type type = TypeFactory.getInstance().getType("node");	
		TIntArrayList nodes = new TIntArrayList();

		GeneralSelectorLabel left = GeneralSelectorLabel.getSelectorLabel("left");
		GeneralSelectorLabel right = GeneralSelectorLabel.getSelectorLabel("right");
		GeneralSelectorLabel parent = GeneralSelectorLabel.getSelectorLabel("parent");

		return result.builder()
				.addNodes(type, 6, nodes )
				.addSelector( nodes.get( 1 ), parent, nodes.get( 0 ) )
				.addSelector( nodes.get( 1 ), left, nodes.get( 2 ) )
				.addSelector( nodes.get( 1 ), right, nodes.get( 3 ) )
				.addSelector( nodes.get( 2 ), left, nodes.get( 4 ) )
				.addSelector( nodes.get( 2 ), right, nodes.get( 5 ) )
				.build();
	}

	public static HeapConfiguration getListAndConstants() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");	
		Type intType = TypeFactory.getInstance().getType("int");

		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		
		return result.builder()
				.addNodes(intType, 2, nodes )
				.addNodes(type, 4, nodes )
				.addVariableEdge( "1", nodes.get( 1 ) )
				.addVariableEdge( "true", nodes.get( 1 ) )
				.addVariableEdge( "false", nodes.get( 0 ) )
				.addVariableEdge( "0", nodes.get( 0 ) )
				.addVariableEdge( "null", nodes.get( 5 ) )
				.addVariableEdge( "x", nodes.get( 2 ) )
				.addSelector( nodes.get( 2 ), next, nodes.get( 3 ) )
				.addSelector( nodes.get( 3 ), next, nodes.get( 4 ) )
				.addSelector( nodes.get( 4 ), next, nodes.get( 5 ) )
				.build();
	}

	public static HeapConfiguration getEmptyGraphWithConstants() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("node");	
		Type booleanType = TypeFactory.getInstance().getType("int");
		
		return result.builder()
				.addNodes(booleanType, 2, nodes )
				.addNodes(type, 1, nodes )
				.addVariableEdge( "1", nodes.get( 1 ) )
				.addVariableEdge( "true", nodes.get( 1 ) )
				.addVariableEdge( "false", nodes.get( 0 ) )
				.addVariableEdge( "0", nodes.get( 0 ) )
				.addVariableEdge( "null", nodes.get( 2 ) )
				.build();
	}

	public static HeapConfiguration getList() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");	
		
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		
		return result.builder()
				.addNodes(type, 4, nodes )
				.addVariableEdge( "null", nodes.get( 3 ) )
				.addVariableEdge( "x", nodes.get( 0 ) )
				.addSelector( nodes.get( 0 ), next, nodes.get( 1 ) )
				.addSelector( nodes.get( 1 ), next, nodes.get( 2 ) )
				.addSelector( nodes.get( 2 ), next, nodes.get( 3 ) )
				.build();
	}


	public static HeapConfiguration getListAndConstantsWithChange() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");

		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		
		return result.builder()
				.addNodes(type, 4, nodes )
				.addVariableEdge( "null", nodes.get( 3 ) )
				.addVariableEdge( "x", nodes.get( 0 ) )
				.addSelector( nodes.get( 0 ), next, nodes.get( 0 ) )
				.addSelector( nodes.get( 1 ), next, nodes.get( 2 ) )
				.addSelector( nodes.get( 2 ), next, nodes.get( 3 ) )
				.build();
	}

	public static HeapConfiguration expectedResultEasyList(){
		
		HeapConfiguration empty = getEmptyGraphWithConstants();
		TIntArrayList nodes = new TIntArrayList();
		
		int nullNode =  empty.targetOf(empty.variableWith("null"));
		int trueNode =  empty.targetOf(empty.variableWith("true"));
		
		Type defaultType = TypeFactory.getInstance().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.EasyList");
		
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralSelectorLabel valSelector = GeneralSelectorLabel.getSelectorLabel( "value" );

		return empty.builder()
				.addNodes(defaultType, 3, nodes )
				.addSelector( nodes.get( 2 ), nextSel, nullNode )
				.addSelector( nodes.get( 1 ), nextSel, nodes.get( 2 ) )
				.addSelector( nodes.get( 0 ), nextSel, nodes.get( 1 ) )
				.addSelector( nodes.get( 2 ), valSelector, trueNode )
				.build();

	}
	
	public static HeapConfiguration expectedResultEasyList_beforeReturn(){
		
		HeapConfiguration empty = getEmptyGraphWithConstants();
		TIntArrayList nodes = new TIntArrayList();
		
		int nullNode =  empty.targetOf(empty.variableWith("null"));
		int trueNode =  empty.targetOf(empty.variableWith("true"));
		
		Type defaultType = TypeFactory.getInstance().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.EasyList");
		
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralSelectorLabel valSelector = GeneralSelectorLabel.getSelectorLabel( "value" );

		return empty.builder()
				.addNodes(defaultType, 3, nodes )
				.addSelector( nodes.get( 2 ), nextSel, nullNode )
				.addSelector( nodes.get( 1 ), nextSel, nodes.get( 2 ) )
				.addSelector( nodes.get( 0 ), nextSel, nodes.get( 1 ) )
				.addSelector( nodes.get( 2 ), valSelector, trueNode )
				.addVariableEdge( "0-$r4", nodes.get( 2 ) )
				.addVariableEdge( "0-$r6", nodes.get( 0 ) )
				.addVariableEdge( "0-$r5", nodes.get( 1 ))
				.addVariableEdge( "0-r8", nodes.get( 2 ) )
				.addVariableEdge( "0-$r7", nullNode )
				.build();

	}

	public static HeapConfiguration	expectedResNormalList() {
		
		HeapConfiguration empty = getEmptyGraphWithConstants();
		TIntArrayList nodes = new TIntArrayList();
		
		int nullNode =  empty.targetOf(empty.variableWith("null"));
		int trueNode =  empty.targetOf(empty.variableWith("true"));
		
		Type defaultType = TypeFactory.getInstance().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.NormalList");
		
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralSelectorLabel valSelector = GeneralSelectorLabel.getSelectorLabel( "value" );

		return empty.builder()
				.addNodes(defaultType, 3, nodes)
				.addSelector( nodes.get(0), nextSel, nullNode )
				.addSelector( nodes.get(0), valSelector, trueNode )
				.addSelector( nodes.get(1), nextSel, nodes.get(0) )
				.addSelector( nodes.get(2), nextSel, nodes.get(1) )
				.build();
	}

	public static HeapConfiguration expectedResBoolList() {

		HeapConfiguration empty = getEmptyGraphWithConstants();
		TIntArrayList nodes = new TIntArrayList();
		
		int nullNode =  empty.targetOf(empty.variableWith("null"));
		int trueNode =  empty.targetOf(empty.variableWith("true"));
		
		Type defaultType = TypeFactory.getInstance().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.BoolList");
		
		GeneralSelectorLabel nextSelector = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralSelectorLabel valSelector = GeneralSelectorLabel.getSelectorLabel( "value" );

		return empty.builder()
				.addNodes(defaultType, 3, nodes)
				.addSelector( nodes.get(0), nextSelector, nullNode )
				.addSelector( nodes.get(0), valSelector, trueNode )
				.addSelector( nodes.get(1), nextSelector, nodes.get(0) )
				.addSelector( nodes.get(1), valSelector, trueNode )
				.addSelector( nodes.get(2), nextSelector, nodes.get(1) )
				.addSelector( nodes.get(2), valSelector, trueNode )
				.build();
	}
	

	public static HeapConfiguration expectedResNormalList_beforeReturn() {
		
		HeapConfiguration empty = getEmptyGraphWithConstants();

		Type defaultType = TypeFactory.getInstance().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.NormalList");
		GeneralSelectorLabel nextSelector = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralSelectorLabel valSelector = GeneralSelectorLabel.getSelectorLabel( "value" );

		int nullNode =  empty.targetOf(empty.variableWith("null"));
		int trueNode =  empty.targetOf(empty.variableWith("true"));
		int falseNode =  empty.targetOf(empty.variableWith("false"));
		
		TIntArrayList nodes = new TIntArrayList();

		return empty.builder()
				.addNodes(defaultType, 3, nodes)
				.addSelector( nodes.get(0), nextSelector, nullNode )
				.addSelector( nodes.get(0), valSelector, trueNode )
				.addSelector( nodes.get(1), nextSelector, nodes.get(0) )
				.addSelector( nodes.get(2), nextSelector, nodes.get(1) )
				.addVariableEdge( "0-$r4", nodes.get(0) )
				.addVariableEdge( "0-$r6", nodes.get(2) )
				.addVariableEdge( "0-$r5", nodes.get(1) )
				.addVariableEdge( "0-r7", nodes.get(0) )
				.addVariableEdge( "0-$z0", falseNode )
				.build();
	}

	public static HeapConfiguration expectedResStaticList() {
		
		HeapConfiguration empty = getEmptyGraphWithConstants();

		Type defaultType = TypeFactory.getInstance().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.EasyList");
		GeneralSelectorLabel nextSelector = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralSelectorLabel valSelector = GeneralSelectorLabel.getSelectorLabel( "value" );

		int nullNode =  empty.targetOf(empty.variableWith("null"));
		int trueNode =  empty.targetOf(empty.variableWith("true"));
		int falseNode =  empty.targetOf(empty.variableWith("false"));
		
		TIntArrayList nodes = new TIntArrayList();

		return empty.builder()
				.addNodes(defaultType, 3, nodes)
				.addSelector( nodes.get(0), nextSelector, nullNode )
				.addSelector( nodes.get(0), valSelector, falseNode )
				.addSelector( nodes.get(1), nextSelector, nodes.get(0) )
				.addSelector( nodes.get(1), valSelector, trueNode )
				.addSelector( nodes.get(2), nextSelector, nodes.get(1) )
				.build();
	}
	
	public static HeapConfiguration expectedResStaticList_beforeReturn() {

		HeapConfiguration empty = getEmptyGraphWithConstants();

		Type defaultType = TypeFactory.getInstance().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.NormalList");
		GeneralSelectorLabel nextSelector = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralSelectorLabel valSelector = GeneralSelectorLabel.getSelectorLabel( "value" );

		int nullNode =  empty.targetOf(empty.variableWith("null"));
		int trueNode =  empty.targetOf(empty.variableWith("true"));
		int falseNode =  empty.targetOf(empty.variableWith("false"));
		
		TIntArrayList nodes = new TIntArrayList();

		return empty.builder()
				.addNodes(defaultType, 3, nodes)
				.addSelector( nodes.get(0), nextSelector, nullNode )
				.addSelector( nodes.get(0), valSelector, falseNode )
				.addSelector( nodes.get(1), nextSelector, nodes.get(0) )
				.addSelector( nodes.get(1), valSelector, trueNode )
				.addSelector( nodes.get(2), nextSelector, nodes.get(1) )
				.addVariableEdge( "0-$z0", falseNode )
				.addVariableEdge( "0-r3", nodes.get(2) )
				.addVariableEdge( "0-r4", nodes.get(0) )
				.addVariableEdge( "0-r1", nodes.get(0) )
				.addVariableEdge( "0-r2", nodes.get(0) )
				.build();
	}
	
	public static HeapConfiguration getListRule1() {

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
			.addNodes(listType, 2, nodes)
			.setExternal(nodes.get(0))
			.setExternal(nodes.get(1))
			.addSelector(nodes.get(0), nextSel, nodes.get(1))
			.build();
	}

	public static HeapConfiguration getListRule2() {

		HeapConfiguration result = new InternalHeapConfiguration();

		Type listType = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 3, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(2))
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addNonterminalEdge(listLabel, new TIntArrayList((new int[]{nodes.get(1), nodes.get(2)})))
				.build();
	}

	public static HeapConfiguration getListRule3() {

		HeapConfiguration result = new InternalHeapConfiguration();

		Type listType = TypeFactory.getInstance().getType("List");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean[]{false,true} );
			
		TIntArrayList nodes = new TIntArrayList();

		return result.builder()
				.addNodes(listType, 3, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(2))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
				.build();
	}
	
	public static HeapConfiguration getListRule2Test() {

		HeapConfiguration result = new InternalHeapConfiguration();

		Type listType = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 3, nodes)
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addVariableEdge("x", nodes.get(2))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
				.build();
	}
	
	public static HeapConfiguration getListRule2TestFail() {

		HeapConfiguration result = new InternalHeapConfiguration();

		Type listType = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 3, nodes)
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addVariableEdge("x", nodes.get(1))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
				.build();
	}
	
	public static HeapConfiguration getTestForListRule3() {
		
		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean[]{false,true} );
			
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 6, nodes)
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(2), nodes.get(3)}))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(3), nodes.get(4)}))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(4), nodes.get(5)}))
				.addVariableEdge("y", nodes.get(5))
				.build();
	}
	
	public static HeapConfiguration getTestForListRule3Fail(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean[]{false,true} );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 3, nodes)
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
				.addVariableEdge("y", nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getDLLRule1(){

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("DLL");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel("n");
		GeneralSelectorLabel prevSel = GeneralSelectorLabel.getSelectorLabel("p");
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addSelector(nodes.get(1), prevSel, nodes.get(0))
				.build();
	}
	
	public static HeapConfiguration getDLLRule2() {

		HeapConfiguration result = new InternalHeapConfiguration();

		Type listType = TypeFactory.getInstance().getType("DLL");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel("n");
		GeneralSelectorLabel prevSel = GeneralSelectorLabel.getSelectorLabel("p");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal("List", 2, new boolean []{false,false});
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 3, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(2))
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addSelector(nodes.get(1), prevSel, nodes.get(0))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
				.build();
	}
	
	public static HeapConfiguration get4DLLRule1() {
		
		HeapConfiguration result = new InternalHeapConfiguration();

		Type listType = TypeFactory.getInstance().getType("DLL");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel("n");
		GeneralSelectorLabel prevSel = GeneralSelectorLabel.getSelectorLabel("p");
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 5, nodes)
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(2))
				.setExternal(nodes.get(3))
				.setExternal(nodes.get(4))
				.addSelector(nodes.get(2), prevSel, nodes.get(1))
				.addSelector(nodes.get(2), nextSel, nodes.get(3))
				.addSelector(nodes.get(3), prevSel, nodes.get(2))
				.addSelector(nodes.get(3), nextSel, nodes.get(4))
				.build();		
	}
	
	public static HeapConfiguration get4DLLRule2() {
		
		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("DLL");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel("n");
		GeneralSelectorLabel prevSel = GeneralSelectorLabel.getSelectorLabel("p");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal("DLL4", 4, new boolean []{false,false});

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 5, nodes)
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(2))
				.setExternal(nodes.get(3))
				.setExternal(nodes.get(4))
				.addSelector(nodes.get(2), prevSel, nodes.get(1))
				.addSelector(nodes.get(2), nextSel, nodes.get(0))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(2), nodes.get(0), nodes.get(3), nodes.get(4)}))
				.build();
	}
	
	public static HeapConfiguration get4DLLRule3(){

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("DLL");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel("n");
		GeneralSelectorLabel prevSel = GeneralSelectorLabel.getSelectorLabel("p");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal("DLL4", 4, new boolean []{false,false});

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 5, nodes)
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(2))
				.setExternal(nodes.get(3))
				.setExternal(nodes.get(4))
				.addSelector(nodes.get(3), prevSel, nodes.get(0))
				.addSelector(nodes.get(3), nextSel, nodes.get(4))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2), nodes.get(0), nodes.get(3)}))
				.build();
	}
	
	public static HeapConfiguration get4DLLRule4(){

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("DLL");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal("DLL4", 4, new boolean []{false,false});

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 6, nodes)
				.setExternal(nodes.get(2))
				.setExternal(nodes.get(3))
				.setExternal(nodes.get(4))
				.setExternal(nodes.get(5))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(2), nodes.get(3), nodes.get(0), nodes.get(1)}))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(4), nodes.get(5)}))
				.build();
	}

	public static HeapConfiguration getAdmissibleGraph(){
		
		HeapConfiguration graph = getListRule2();
		
		int ntEdge = graph.nonterminalEdges().get(0);
		int node = graph.attachedNodesOf(ntEdge).get(1);
		
		return graph.builder()
				.addVariableEdge("x", node)
				.build();
	}

	public static HeapConfiguration getInAdmissibleGraph(){
		
		HeapConfiguration graph = getListRule2();
		
		int ntEdge = graph.nonterminalEdges().get(0);
		int node = graph.attachedNodesOf(ntEdge).get(0);
		
		return graph.builder()
				.addVariableEdge("x", node)
				.build();
	}

	public static HeapConfiguration getMaterializationTest(){

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean[] {false,true} );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.addVariableEdge("0-x", nodes.get(0))
				.build();
	}

	public static HeapConfiguration getMaterializationRes1(){

		HeapConfiguration result = new InternalHeapConfiguration();

		Type listType = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addVariableEdge("0-x", nodes.get(0))
				.build();
	}

	public static HeapConfiguration getMaterializationRes2() {

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("List");

		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal("List", 2,new boolean[] {false,true} );

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 3, nodes)
				.addVariableEdge("0-x", nodes.get(0))
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
				.build();
	}
	
	/*
	 * results in res1 and needs only a single abstraction step
	 */
	public static HeapConfiguration getCanonizationTest1(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getCanonizationRes1(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean[] {false,true});

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getCanonizationRes3(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean[] {false,true});

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.addVariableEdge("x", nodes.get(0))
				.build();	}
	
	/*
	 * This should also result in res1, but needs at least two abstraction steps
	 */
	public static HeapConfiguration getCanonizationTest2(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 3, nodes)
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addSelector(nodes.get(1), nextSel, nodes.get(2))
				.build();
	}
	
	public static HeapConfiguration getCanonizationTest3() {

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addVariableEdge("x", nodes.get(0))
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getOneElemWithVar(){
		
		HeapConfiguration result = getEmptyGraphWithConstants();
		
		Type listType = TypeFactory.getInstance().getType("List");
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 1, nodes)
				.addVariableEdge("x", nodes.get(0))
				.build();
	}
	
	public static HeapConfiguration getLongListRule1() {

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = TypeFactory.getInstance().getType("de.rwth.i2.attestor.abstraction.programs.LongList");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.build();
	}

	public static HeapConfiguration getLongListRule2() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();

		Type listType = TypeFactory.getInstance().getType("de.rwth.i2.attestor.abstraction.programs.LongList");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );

		return result.builder()
				.addNodes(listType, 3, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(2))
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
				.build();
	}

	public static HeapConfiguration getLongListRule3() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();

		Type listType = TypeFactory.getInstance().getType("de.rwth.i2.attestor.abstraction.programs.LongList");

		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean[]{false,true} );
			
		return result.builder()
				.addNodes(listType, 3, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(2))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
				.build();
	}
	
	public static HeapConfiguration getRListRule1() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();

		Type listType = TypeFactory.getInstance().getType("RListNode");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel("n");
			
		return result.builder()
				.addNodes(listType, 2, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.build();		
	}
	
	public static HeapConfiguration getRListRule2() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();

		Type listType = TypeFactory.getInstance().getType("RListNode");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel("n");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal("RList",2,new boolean[]{false,false});
			
		return result.builder()
				.addNodes(listType, 3, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(2))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.addSelector(nodes.get(1), nextSel, nodes.get(2))
				.build();
		}
	
	public static HeapConfiguration testRule1(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		GeneralNonterminal n2 = GeneralNonterminal.getNonterminal( "ReductionTest_N2", 3, new boolean[]{false,false,false} );
		
		Type type = TypeFactory.getInstance().getType("node");
		GeneralSelectorLabel sel1 = GeneralSelectorLabel.getSelectorLabel("sel1");

		return result.builder()
				.addNodes( type, 4, nodes )
				.addSelector( nodes.get( 0 ), sel1, nodes.get( 3 ) )
				.setExternal( nodes.get( 0 ) )
				.setExternal( nodes.get( 1 ) )
				.setExternal( nodes.get( 2 ) )
				.addNonterminalEdge( n2, new TIntArrayList(new int[]{nodes.get(3), nodes.get(1), nodes.get(2)}) )
				.build();
	}
	
	public static HeapConfiguration testRule2(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		GeneralNonterminal n1 = GeneralNonterminal.getNonterminal( "ReductionTest_N1", 3, new boolean[]{false,false,false} );

		Type type = TypeFactory.getInstance().getType("node");

		return result.builder()
				.addNodes( type, 3, nodes )
				.setExternal( nodes.get( 0 ) )
				.setExternal( nodes.get( 1 ) )
				.setExternal( nodes.get( 2 ) )
				.addNonterminalEdge( n1, new TIntArrayList(new int[]{nodes.get(1), nodes.get(0), nodes.get(2)}) )
				.build();
	}
	
	public static HeapConfiguration testRule3(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("node");
		GeneralSelectorLabel sel1 = GeneralSelectorLabel.getSelectorLabel("sel1");
		GeneralSelectorLabel sel2 = GeneralSelectorLabel.getSelectorLabel("sel2");

		return result.builder()
				.addNodes( type, 3, nodes )
				.addSelector( nodes.get( 0 ), sel1, nodes.get( 1 ) )
				.addSelector( nodes.get( 0 ), sel2, nodes.get( 2 ) )
				.setExternal( nodes.get( 0 ) )
				.setExternal( nodes.get( 1 ) )
				.setExternal( nodes.get( 2 ) )
				.build();
	}
	
	public static HeapConfiguration getLongConcreteSLL() {
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel nextSel = GeneralSelectorLabel.getSelectorLabel( "next" );
		
		return result.builder()
				.addNodes(listType, 10, nodes)
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addSelector(nodes.get(1), nextSel, nodes.get(2))
				.addSelector(nodes.get(2), nextSel, nodes.get(3))
				.addSelector(nodes.get(3), nextSel, nodes.get(4))
				.addSelector(nodes.get(4), nextSel, nodes.get(5))
				.addSelector(nodes.get(5), nextSel, nodes.get(6))
				.addSelector(nodes.get(6), nextSel, nodes.get(7))
				.addSelector(nodes.get(7), nextSel, nodes.get(8))
				.addSelector(nodes.get(8), nextSel, nodes.get(9))
				.build();
	}
	
	public static HeapConfiguration getSLLHandle() {
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type listType = TypeFactory.getInstance().getType("List");
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean[]{false,true} );
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpectedResultTestGenerateNew(){
		
		HeapConfiguration result = getEmptyGraphWithConstants();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType( "type" );
		
		return result.builder()
				.addNodes(type, 1, nodes)
				.build();
	}
	
	public static HeapConfiguration getExepectedResultTestNewExprTest(){
		
		HeapConfiguration result = getThreeElementDLL();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("node");
		
		return result.builder()
				.addNodes(type, 1, nodes)
				.build();
	}
	
	public static HeapConfiguration getExpectedResult_AssignInvokeNonTrivial(){
		
		HeapConfiguration result = getListAndConstants();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("node");
		
		return result.builder()
				.addNodes(type, 1, nodes)
				.addVariableEdge("0-x", nodes.get(0))
				.build();
	}
	
	public static HeapConfiguration getExpectedResult_AssignStmt() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();

		Type type = TypeFactory.getInstance().getType("node");

		GeneralSelectorLabel left = GeneralSelectorLabel.getSelectorLabel("left");
		GeneralSelectorLabel right = GeneralSelectorLabel.getSelectorLabel("right");
		GeneralSelectorLabel parent = GeneralSelectorLabel.getSelectorLabel("parent");

		GeneralNonterminal nT = GeneralNonterminal.getNonterminal( "TLL", 4, new boolean[]{false,false,false,false} );
		
		return result.builder()
				.addNodes(type, 7, nodes)
				.addSelector( nodes.get( 0 ), left, nodes.get( 1 ) )
				.addSelector( nodes.get( 0 ), right, nodes.get( 2 ) )
				.addSelector( nodes.get( 0 ), parent, nodes.get( 3 ) )
				.addVariableEdge( "XYZ", nodes.get(2) )
				.addVariableEdge( "ZYX", nodes.get( 0 ) )
				.addNonterminalEdge( nT, new TIntArrayList(new int[]{nodes.get(1), nodes.get(0), nodes.get(5), nodes.get(4)}) )
				.addNonterminalEdge( nT, new TIntArrayList(new int[]{nodes.get(2), nodes.get(0), nodes.get(5), nodes.get(6)}) )
				.setExternal( nodes.get( 0 ) )
				.setExternal( nodes.get( 3 ) )
				.setExternal( nodes.get( 4 ) )
				.setExternal( nodes.get( 6 ) )
				.build();		 	 		
	}

	public static HeapConfiguration getInput_InvokeWithEffect(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralSelectorLabel prev = GeneralSelectorLabel.getSelectorLabel("prev");
		
		return result.builder()
				.addNodes(type, 3, nodes)
				.addSelector(nodes.get(0), next, nodes.get(1))
				.addSelector(nodes.get(1), next, nodes.get(2))
				.addSelector(nodes.get(1), prev, nodes.get(0))
				.addSelector(nodes.get(0), prev, nodes.get(2))
				.addVariableEdge("null", nodes.get(2))
				.addVariableEdge("x", nodes.get(0))
				.build();
	}
	
	public static HeapConfiguration getExpectedResult_InvokeWithEffect(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralSelectorLabel prev = GeneralSelectorLabel.getSelectorLabel("prev");
		
		return result.builder()
				.addNodes(type, 3, nodes)
				.addSelector(nodes.get(0), next, nodes.get(1))
				.addSelector(nodes.get(1), next, nodes.get(1))
				.addSelector(nodes.get(1), prev, nodes.get(0))
				.addSelector(nodes.get(0), prev, nodes.get(2))
				.addVariableEdge("null", nodes.get(2))
				.addVariableEdge("x", nodes.get(0))
				.build();
	}
	
	public static HeapConfiguration getInput_changeSelectorLabel(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		AnnotatedSelectorLabel left = new AnnotatedSelectorLabel("left", "?");
		AnnotatedSelectorLabel right = new AnnotatedSelectorLabel("right", "?");
		Type type = TypeFactory.getInstance().getType("type");
		
		return result.builder()
				.addNodes(type, 3, nodes)
				.addSelector(nodes.get(0), left, nodes.get(1))
				.addSelector(nodes.get(0), right, nodes.get(1))
				.addSelector(nodes.get(1), left, nodes.get(2))
				.addSelector(nodes.get(1), right, nodes.get(2))
				.addVariableEdge("x", nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getExpected_changeSelectorLabel(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		AnnotatedSelectorLabel left = new AnnotatedSelectorLabel("left", "?");
		AnnotatedSelectorLabel left2 = new AnnotatedSelectorLabel("left", "2");
		AnnotatedSelectorLabel right = new AnnotatedSelectorLabel("right", "?");
		Type type = TypeFactory.getInstance().getType("type");
		
		return result.builder()
				.addNodes(type, 3, nodes)
				.addSelector(nodes.get(0), left, nodes.get(1))
				.addSelector(nodes.get(0), right, nodes.get(1))
				.addSelector(nodes.get(1), left2, nodes.get(2))
				.addSelector(nodes.get(1), right, nodes.get(2))
				.addVariableEdge("x", nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getTreeLeaf(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralSelectorLabel prev = GeneralSelectorLabel.getSelectorLabel("prev");
		
		return result.builder()
				.addNodes(type, 2, nodes)
				.addSelector(nodes.get(0), next, nodes.get(1))
				.addSelector(nodes.get(0), prev, nodes.get(1))
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration get2TreeLeaf(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralSelectorLabel prev = GeneralSelectorLabel.getSelectorLabel("prev");
		GeneralSelectorLabel parent = GeneralSelectorLabel.getSelectorLabel("parent");
		
		return result.builder()
				.addNodes(type, 3, nodes)
				.addSelector(nodes.get(0), next, nodes.get(1))
				.addSelector(nodes.get(0), prev, nodes.get(1))
				.addSelector(nodes.get(0), parent, nodes.get(2))
				.addSelector(nodes.get(2), next, nodes.get(1))
				.addSelector(nodes.get(2), prev, nodes.get(1))
				.addSelector(nodes.get(2), parent, nodes.get(1))
				.addVariableEdge("nil", nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getDLL2Rule(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");
		GeneralNonterminal dllLabel = GeneralNonterminal.getNonterminal( "DLL", 3, new boolean[]{true, false, true} );
	
		return result.builder()
				.addNodes(type, 4, nodes)
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(3)}))
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(3), nodes.get(2)}))
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(2))
				.build();
	}
	
	public static HeapConfiguration getDLLTarget(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");
		GeneralSelectorLabel next = GeneralSelectorLabel.getSelectorLabel("next");
		GeneralSelectorLabel prev = GeneralSelectorLabel.getSelectorLabel("prev");
		GeneralSelectorLabel list = GeneralSelectorLabel.getSelectorLabel("list");
		GeneralNonterminal dllLabel = GeneralNonterminal.getNonterminal( "DLL", 3, new boolean[]{true, false, true} );
	
		return result.builder()
				.addNodes(type, 6, nodes)
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(3)}))
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(3), nodes.get(2)}))
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(2))
				.addVariableEdge("0-$r0", nodes.get(0))
				.addSelector(nodes.get(1), list, nodes.get(0))
				.addSelector(nodes.get(4), next, nodes.get(1))
				.addSelector(nodes.get(4), list, nodes.get(0))
				.addSelector(nodes.get(1), prev, nodes.get(4))
				.addVariableEdge("0-$r1", nodes.get(4))
				//.addSelector(nodes.get(4), list, nodes.get(5))
				.build();
	}
	
	public static HeapConfiguration getInput_testHash(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");
		GeneralNonterminal dllLabel = GeneralNonterminal.getNonterminal( "DLL", 3, new boolean[]{true, false, true} );
	
		return result.builder()
				.addNodes(type, 4, nodes)
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(3)}))
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(3), nodes.get(2)}))
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(2))
				.build();
	}
	
	public static HeapConfiguration getInput_testHash_Permuted(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");
		GeneralNonterminal dllLabel = GeneralNonterminal.getNonterminal( "DLL", 3, new boolean[]{true, false, true} );
	
		return result.builder()
				.addNodes(type, 4, nodes)
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(3), nodes.get(1), nodes.get(0)}))
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(3), nodes.get(0), nodes.get(2)}))
				.setExternal(nodes.get(3))
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(2))
				.build();
	}

	public static HeapConfiguration getInput_DifferentStacks_1() {

		IndexSymbol abstractIndexSymbol = AbstractIndexSymbol.get("X");
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(abstractIndexSymbol);
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");
		IndexedNonterminal nt = new IndexedNonterminalImpl("DifferentStacks", 1, new boolean[]{false}, stack);
	
		return result.builder()
				.addNodes(type, 1, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0)}))
				.build();
	}
	
	public static HeapConfiguration getInput_DifferentStacks_2() {

		IndexSymbol concreteIndexSymbol = ConcreteIndexSymbol.getStackSymbol("s", false);
		IndexSymbol abstractIndexSymbol = AbstractIndexSymbol.get("X");
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(concreteIndexSymbol);
		stack.add(abstractIndexSymbol);
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = TypeFactory.getInstance().getType("List");
		IndexedNonterminal nt = new IndexedNonterminalImpl("DifferentStacks", 1, new boolean[]{false}, stack);
	
		return result.builder()
				.addNodes(type, 1, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0)}))
				.build();
	}
}
