package de.rwth.i2.attestor.graph.heap.internal;


import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.graph.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.programState.indexedState.*;
import de.rwth.i2.attestor.programState.indexedState.index.*;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public final class ExampleHcImplFactory {

	public static HeapConfiguration getEmptyHc() {

		return new InternalHeapConfiguration();
	}
	
	public static HeapConfiguration getSimpleDLL() {
		
		HeapConfiguration result = new InternalHeapConfiguration();
		BasicSelectorLabel sel = BasicSelectorLabel.getSelectorLabel("next");
		BasicNonterminal nt = BasicNonterminal.getNonterminal("3", 3, new boolean[]{false, false, false});
		Type type = Settings.getInstance().factory().getType("node");
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
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		BasicSelectorLabel prev = BasicSelectorLabel.getSelectorLabel("prev");
		Type type = Settings.getInstance().factory().getType("node");
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
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		BasicSelectorLabel prev = BasicSelectorLabel.getSelectorLabel("prev");
		Type type = Settings.getInstance().factory().getType("node");
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
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		BasicSelectorLabel prev = BasicSelectorLabel.getSelectorLabel("prev");
		Type type = Settings.getInstance().factory().getType("node");
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
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		BasicSelectorLabel prev = BasicSelectorLabel.getSelectorLabel("prev");
		Type type = Settings.getInstance().factory().getType("node");
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
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		BasicSelectorLabel prev = BasicSelectorLabel.getSelectorLabel("prev");
		Type type = Settings.getInstance().factory().getType("node");
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
		Type type = Settings.getInstance().factory().getType("node");
		TIntArrayList nodes = new TIntArrayList();

		BasicSelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		BasicSelectorLabel right = BasicSelectorLabel.getSelectorLabel("right");
		BasicSelectorLabel parent = BasicSelectorLabel.getSelectorLabel("parent");

		BasicNonterminal nT = BasicNonterminal.getNonterminal( "TLL", 4, new boolean[]{false,false,false,false} );

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
		Type type = Settings.getInstance().factory().getType("node");
		TIntArrayList nodes = new TIntArrayList();

		BasicSelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		BasicSelectorLabel right = BasicSelectorLabel.getSelectorLabel("right");
		BasicSelectorLabel parent = BasicSelectorLabel.getSelectorLabel("parent");

		BasicNonterminal nT = BasicNonterminal.getNonterminal( "Tree", 4, new boolean[]{false,false,false,false} );
		
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
		Type type = Settings.getInstance().factory().getType("node");
		TIntArrayList nodes = new TIntArrayList();

		BasicSelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		BasicSelectorLabel right = BasicSelectorLabel.getSelectorLabel("right");

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
		Type type = Settings.getInstance().factory().getType("node");
		TIntArrayList nodes = new TIntArrayList();

		BasicSelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		BasicSelectorLabel right = BasicSelectorLabel.getSelectorLabel("right");
		BasicSelectorLabel parent = BasicSelectorLabel.getSelectorLabel("parent");

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
		Type type = Settings.getInstance().factory().getType("node");
		TIntArrayList nodes = new TIntArrayList();

		BasicSelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		BasicSelectorLabel right = BasicSelectorLabel.getSelectorLabel("right");
		BasicSelectorLabel parent = BasicSelectorLabel.getSelectorLabel("parent");

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
		
		Type type = Settings.getInstance().factory().getType("List");
		Type intType = Settings.getInstance().factory().getType("int");

		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		
		return result.builder()
				.addNodes(intType, 2, nodes )
				.addNodes(type, 4, nodes )
				.addVariableEdge( Constants.ONE, nodes.get( 1 ) )
				.addVariableEdge( Constants.TRUE, nodes.get( 1 ) )
				.addVariableEdge( Constants.FALSE, nodes.get( 0 ) )
				.addVariableEdge( Constants.ZERO, nodes.get( 0 ) )
				.addVariableEdge( Constants.NULL, nodes.get( 5 ) )
				.addVariableEdge( "y", nodes.get( 2 ) )
				.addSelector( nodes.get( 2 ), next, nodes.get( 3 ) )
				.addSelector( nodes.get( 3 ), next, nodes.get( 4 ) )
				.addSelector( nodes.get( 4 ), next, nodes.get( 5 ) )
				.build();
	}

	public static HeapConfiguration getEmptyGraphWithConstants() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("node");
		Type booleanType = Settings.getInstance().factory().getType("int");
		
		return result.builder()
				.addNodes(booleanType, 2, nodes )
				.addNodes(type, 1, nodes )
				.addVariableEdge( Constants.ONE, nodes.get( 1 ) )
				.addVariableEdge( Constants.TRUE, nodes.get( 1 ) )
				.addVariableEdge( Constants.FALSE, nodes.get( 0 ) )
				.addVariableEdge( Constants.ZERO, nodes.get( 0 ) )
				.addVariableEdge( Constants.NULL, nodes.get( 2 ) )
				.build();
	}

	public static HeapConfiguration getList() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();

		Type type = Settings.getInstance().factory().getType("List");

		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");

		return result.builder()
				.addNodes(type, 4, nodes )
				.addVariableEdge( Constants.NULL, nodes.get( 3 ) )
				.addVariableEdge( "x", nodes.get( 0 ) )
				.addSelector( nodes.get( 0 ), next, nodes.get( 1 ) )
				.addSelector( nodes.get( 1 ), next, nodes.get( 2 ) )
				.addSelector( nodes.get( 2 ), next, nodes.get( 3 ) )
				.build();
	}

	public static HeapConfiguration getAbstractList() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();

		Type type = Settings.getInstance().factory().getType("List");

		Nonterminal nt = BasicNonterminal.getNonterminal("SLL", 2, new boolean[]{false, true});
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");

		return result.builder()
				.addNodes(type, 2, nodes )
				.addVariableEdge( Constants.NULL, nodes.get( 1 ) )
				.addVariableEdge( "x", nodes.get( 0 ) )
				.addNonterminalEdge(nt, nodes)
				.build();
	}




	public static HeapConfiguration getListAndConstantsWithChange() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");

		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		
		return result.builder()
				.addNodes(type, 4, nodes )
				.addVariableEdge( Constants.NULL, nodes.get( 3 ) )
				.addVariableEdge( "x", nodes.get( 0 ) )
				.addSelector( nodes.get( 0 ), next, nodes.get( 0 ) )
				.addSelector( nodes.get( 1 ), next, nodes.get( 2 ) )
				.addSelector( nodes.get( 2 ), next, nodes.get( 3 ) )
				.build();
	}

	public static HeapConfiguration expectedResultEasyList(){
		
		HeapConfiguration empty = getEmptyGraphWithConstants();
		TIntArrayList nodes = new TIntArrayList();
		
		int nullNode =  empty.targetOf(empty.variableWith(Constants.NULL));
		int trueNode =  empty.targetOf(empty.variableWith(Constants.TRUE));
		
		Type defaultType = Settings.getInstance().factory().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.EasyList");
		
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicSelectorLabel valSelector = BasicSelectorLabel.getSelectorLabel( "value" );

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
		
		int nullNode =  empty.targetOf(empty.variableWith(Constants.NULL));
		int trueNode =  empty.targetOf(empty.variableWith(Constants.TRUE));
		
		Type defaultType = Settings.getInstance().factory().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.EasyList");
		
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicSelectorLabel valSelector = BasicSelectorLabel.getSelectorLabel( "value" );

		return empty.builder()
				.addNodes(defaultType, 3, nodes )
				.addSelector( nodes.get( 2 ), nextSel, nullNode )
				.addSelector( nodes.get( 1 ), nextSel, nodes.get( 2 ) )
				.addSelector( nodes.get( 0 ), nextSel, nodes.get( 1 ) )
				.addSelector( nodes.get( 2 ), valSelector, trueNode )
				.addVariableEdge( "$r4", nodes.get( 2 ) )
				.addVariableEdge( "$r6", nodes.get( 0 ) )
				.addVariableEdge( "$r5", nodes.get( 1 ))
				.addVariableEdge( "r8", nodes.get( 2 ) )
				.addVariableEdge( "$r7", nullNode )
				.build();

	}

	public static HeapConfiguration	expectedResNormalList() {
		
		HeapConfiguration empty = getEmptyGraphWithConstants();
		TIntArrayList nodes = new TIntArrayList();
		
		int nullNode =  empty.targetOf(empty.variableWith(Constants.NULL));
		int trueNode =  empty.targetOf(empty.variableWith(Constants.TRUE));
		
		Type defaultType = Settings.getInstance().factory().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.NormalList");
		
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicSelectorLabel valSelector = BasicSelectorLabel.getSelectorLabel( "value" );

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
		
		int nullNode =  empty.targetOf(empty.variableWith(Constants.NULL));
		int trueNode =  empty.targetOf(empty.variableWith(Constants.TRUE));
		
		Type defaultType = Settings.getInstance().factory().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.BoolList");
		
		BasicSelectorLabel nextSelector = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicSelectorLabel valSelector = BasicSelectorLabel.getSelectorLabel( "value" );

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

		Type defaultType = Settings.getInstance().factory().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.NormalList");
		BasicSelectorLabel nextSelector = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicSelectorLabel valSelector = BasicSelectorLabel.getSelectorLabel( "value" );

		int nullNode =  empty.targetOf(empty.variableWith(Constants.NULL));
		int trueNode =  empty.targetOf(empty.variableWith(Constants.TRUE));
		int falseNode =  empty.targetOf(empty.variableWith(Constants.FALSE));
		
		TIntArrayList nodes = new TIntArrayList();

		return empty.builder()
				.addNodes(defaultType, 3, nodes)
				.addSelector( nodes.get(0), nextSelector, nullNode )
				.addSelector( nodes.get(0), valSelector, trueNode )
				.addSelector( nodes.get(1), nextSelector, nodes.get(0) )
				.addSelector( nodes.get(2), nextSelector, nodes.get(1) )
				.addVariableEdge( "$r4", nodes.get(0) )
				.addVariableEdge( "$r6", nodes.get(2) )
				.addVariableEdge( "$r5", nodes.get(1) )
				.addVariableEdge( "r7", nodes.get(0) )
				.addVariableEdge( "$z0", falseNode )
				.build();
	}

	public static HeapConfiguration expectedResStaticList() {
		
		HeapConfiguration empty = getEmptyGraphWithConstants();

		Type defaultType = Settings.getInstance().factory().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.EasyList");
		BasicSelectorLabel nextSelector = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicSelectorLabel valSelector = BasicSelectorLabel.getSelectorLabel( "value" );

		int nullNode =  empty.targetOf(empty.variableWith(Constants.NULL));
		int trueNode =  empty.targetOf(empty.variableWith(Constants.TRUE));
		int falseNode =  empty.targetOf(empty.variableWith(Constants.FALSE));
		
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

		Type defaultType = Settings.getInstance().factory().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.NormalList");
		BasicSelectorLabel nextSelector = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicSelectorLabel valSelector = BasicSelectorLabel.getSelectorLabel( "value" );

		int nullNode =  empty.targetOf(empty.variableWith(Constants.NULL));
		int trueNode =  empty.targetOf(empty.variableWith(Constants.TRUE));
		int falseNode =  empty.targetOf(empty.variableWith(Constants.FALSE));
		
		TIntArrayList nodes = new TIntArrayList();

		return empty.builder()
				.addNodes(defaultType, 3, nodes)
				.addSelector( nodes.get(0), nextSelector, nullNode )
				.addSelector( nodes.get(0), valSelector, falseNode )
				.addSelector( nodes.get(1), nextSelector, nodes.get(0) )
				.addSelector( nodes.get(1), valSelector, trueNode )
				.addSelector( nodes.get(2), nextSelector, nodes.get(1) )
				.addVariableEdge( "$z0", falseNode )
				.addVariableEdge( "r3", nodes.get(2) )
				.addVariableEdge( "r4", nodes.get(0) )
				.addVariableEdge( "r1", nodes.get(0) )
				.addVariableEdge( "r2", nodes.get(0) )
				.build();
	}
	
	public static HeapConfiguration getListRule1() {

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );

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

		Type listType = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );

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

		Type listType = Settings.getInstance().factory().getType("List");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean[]{false,true} );
			
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

		Type listType = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );
		
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

		Type listType = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );
		
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
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean[]{false,true} );
			
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
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean[]{false,true} );
		
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
		
		Type listType = Settings.getInstance().factory().getType("DLL");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel("n");
		BasicSelectorLabel prevSel = BasicSelectorLabel.getSelectorLabel("p");
		
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

		Type listType = Settings.getInstance().factory().getType("DLL");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel("n");
		BasicSelectorLabel prevSel = BasicSelectorLabel.getSelectorLabel("p");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal("List", 2, new boolean []{false,false});
		
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

		Type listType = Settings.getInstance().factory().getType("DLL");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel("n");
		BasicSelectorLabel prevSel = BasicSelectorLabel.getSelectorLabel("p");
		
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
		
		Type listType = Settings.getInstance().factory().getType("DLL");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel("n");
		BasicSelectorLabel prevSel = BasicSelectorLabel.getSelectorLabel("p");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal("DLL4", 4, new boolean []{false,false});

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
		
		Type listType = Settings.getInstance().factory().getType("DLL");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel("n");
		BasicSelectorLabel prevSel = BasicSelectorLabel.getSelectorLabel("p");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal("DLL4", 4, new boolean []{false,false});

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
		
		Type listType = Settings.getInstance().factory().getType("DLL");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal("DLL4", 4, new boolean []{false,false});

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
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean[] {false,true} );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.addVariableEdge("x", nodes.get(0))
				.build();
	}

	public static HeapConfiguration getMaterializationRes1(){

		HeapConfiguration result = new InternalHeapConfiguration();

		Type listType = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addVariableEdge("x", nodes.get(0))
				.build();
	}

	public static HeapConfiguration getMaterializationRes2() {

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = Settings.getInstance().factory().getType("List");

		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal("List", 2,new boolean[] {false,true} );

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 3, nodes)
				.addVariableEdge("x", nodes.get(0))
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
				.build();
	}
	
	/*
	 * results in res1 and needs only a single abstraction step
	 */
	public static HeapConfiguration getCanonizationTest1(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getCanonizationRes1(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean[] {false,true});

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getCanonizationRes3(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean[] {false,true});

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
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 3, nodes)
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.addSelector(nodes.get(1), nextSel, nodes.get(2))
				.build();
	}
	
	public static HeapConfiguration getCanonizationTest3() {

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );

		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addVariableEdge("x", nodes.get(0))
				.addSelector(nodes.get(0), nextSel, nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getOneElemWithVar(){
		
		HeapConfiguration result = getEmptyGraphWithConstants();
		
		Type listType = Settings.getInstance().factory().getType("List");
		
		TIntArrayList nodes = new TIntArrayList();
		
		return result.builder()
				.addNodes(listType, 1, nodes)
				.addVariableEdge("x", nodes.get(0))
				.build();
	}
	
	public static HeapConfiguration getLongListRule1() {

		HeapConfiguration result = new InternalHeapConfiguration();
		
		Type listType = Settings.getInstance().factory().getType("de.rwth.i2.attestor.abstraction.programs.LongList");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		
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

		Type listType = Settings.getInstance().factory().getType("de.rwth.i2.attestor.abstraction.programs.LongList");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );

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

		Type listType = Settings.getInstance().factory().getType("de.rwth.i2.attestor.abstraction.programs.LongList");

		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean[]{false,true} );
			
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

		Type listType = Settings.getInstance().factory().getType("RListNode");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel("n");
			
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

		Type listType = Settings.getInstance().factory().getType("RListNode");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel("n");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal("RList",2,new boolean[]{false,false});
			
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
		
		BasicNonterminal n2 = BasicNonterminal.getNonterminal( "ReductionTest_N2", 3, new boolean[]{false,false,false} );
		
		Type type = Settings.getInstance().factory().getType("node");
		BasicSelectorLabel sel1 = BasicSelectorLabel.getSelectorLabel("sel1");

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
		
		BasicNonterminal n1 = BasicNonterminal.getNonterminal( "ReductionTest_N1", 3, new boolean[]{false,false,false} );

		Type type = Settings.getInstance().factory().getType("node");

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
		
		Type type = Settings.getInstance().factory().getType("node");
		BasicSelectorLabel sel1 = BasicSelectorLabel.getSelectorLabel("sel1");
		BasicSelectorLabel sel2 = BasicSelectorLabel.getSelectorLabel("sel2");

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
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel( "next" );
		
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
		
		Type listType = Settings.getInstance().factory().getType("List");
		BasicNonterminal listLabel = BasicNonterminal.getNonterminal( "List", 2, new boolean[]{false,true} );
		
		return result.builder()
				.addNodes(listType, 2, nodes)
				.addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
				.build();
	}
	
	public static HeapConfiguration getExpectedResultTestGenerateNew(){
		
		HeapConfiguration result = getEmptyGraphWithConstants();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType( "type" );
		
		return result.builder()
				.addNodes(type, 1, nodes)
				.build();
	}
	
	public static HeapConfiguration getExepectedResultTestNewExprTest(){
		
		HeapConfiguration result = getThreeElementDLL();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("node");
		
		return result.builder()
				.addNodes(type, 1, nodes)
				.build();
	}
	
	public static HeapConfiguration getExpectedResult_AssignInvokeNonTrivial(){
		
		HeapConfiguration result = getListAndConstants();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("node");
		
		return result.builder()
				.addNodes(type, 1, nodes)
				.addVariableEdge("x", nodes.get(0))
				.build();
	}
	
	public static HeapConfiguration getExpectedResult_AssignStmt() {

		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();

		Type type = Settings.getInstance().factory().getType("node");

		BasicSelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		BasicSelectorLabel right = BasicSelectorLabel.getSelectorLabel("right");
		BasicSelectorLabel parent = BasicSelectorLabel.getSelectorLabel("parent");

		BasicNonterminal nT = BasicNonterminal.getNonterminal( "TLL", 4, new boolean[]{false,false,false,false} );
		
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
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		BasicSelectorLabel prev = BasicSelectorLabel.getSelectorLabel("prev");
		
		return result.builder()
				.addNodes(type, 3, nodes)
				.addSelector(nodes.get(0), next, nodes.get(1))
				.addSelector(nodes.get(1), next, nodes.get(2))
				.addSelector(nodes.get(1), prev, nodes.get(0))
				.addSelector(nodes.get(0), prev, nodes.get(2))
				.addVariableEdge(Constants.NULL, nodes.get(2))
				.addVariableEdge("x", nodes.get(0))
				.build();
	}
	
	public static HeapConfiguration getExpectedResult_InvokeWithEffect(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		BasicSelectorLabel prev = BasicSelectorLabel.getSelectorLabel("prev");
		
		return result.builder()
				.addNodes(type, 3, nodes)
				.addSelector(nodes.get(0), next, nodes.get(1))
				.addSelector(nodes.get(1), next, nodes.get(1))
				.addSelector(nodes.get(1), prev, nodes.get(0))
				.addSelector(nodes.get(0), prev, nodes.get(2))
				.addVariableEdge(Constants.NULL, nodes.get(2))
				.addVariableEdge("x", nodes.get(0))
				.addVariableEdge("y", nodes.get(1))
				.build();
	}
	
	public static HeapConfiguration getInput_changeSelectorLabel(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		AnnotatedSelectorLabel left = new AnnotatedSelectorLabel("left", "?");
		AnnotatedSelectorLabel right = new AnnotatedSelectorLabel("right", "?");
		Type type = Settings.getInstance().factory().getType("type");
		
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
		Type type = Settings.getInstance().factory().getType("type");
		
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
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		BasicSelectorLabel prev = BasicSelectorLabel.getSelectorLabel("prev");
		
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
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		BasicSelectorLabel prev = BasicSelectorLabel.getSelectorLabel("prev");
		BasicSelectorLabel parent = BasicSelectorLabel.getSelectorLabel("parent");
		
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
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicNonterminal dllLabel = BasicNonterminal.getNonterminal( "DLL", 3, new boolean[]{true, false, true} );
	
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
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
		BasicSelectorLabel prev = BasicSelectorLabel.getSelectorLabel("prev");
		BasicSelectorLabel list = BasicSelectorLabel.getSelectorLabel("list");
		BasicNonterminal dllLabel = BasicNonterminal.getNonterminal( "DLL", 3, new boolean[]{true, false, true} );
	
		return result.builder()
				.addNodes(type, 6, nodes)
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(3)}))
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(3), nodes.get(2)}))
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(2))
				.addVariableEdge("$r0", nodes.get(0))
				.addSelector(nodes.get(1), list, nodes.get(0))
				.addSelector(nodes.get(4), next, nodes.get(1))
				.addSelector(nodes.get(4), list, nodes.get(0))
				.addSelector(nodes.get(1), prev, nodes.get(4))
				.addVariableEdge("$r1", nodes.get(4))
				//.addSelector(nodes.get(4), list, nodes.get(5))
				.build();
	}
	
	public static HeapConfiguration getInput_testHash(){
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicNonterminal dllLabel = BasicNonterminal.getNonterminal( "DLL", 3, new boolean[]{true, false, true} );
	
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
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicNonterminal dllLabel = BasicNonterminal.getNonterminal( "DLL", 3, new boolean[]{true, false, true} );
	
		return result.builder()
				.addNodes(type, 4, nodes)
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(3), nodes.get(1), nodes.get(0)}))
				.addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(3), nodes.get(0), nodes.get(2)}))
				.setExternal(nodes.get(3))
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(2))
				.build();
	}

	public static HeapConfiguration getInput_DifferentIndices_1() {

		IndexSymbol abstractIndexSymbol = AbstractIndexSymbol.get("X");
		List<IndexSymbol> index = new ArrayList<>();
		index.add(abstractIndexSymbol);
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		IndexedNonterminal nt = new IndexedNonterminalImpl("DifferentIndices", 1, new boolean[]{false}, index);
	
		return result.builder()
				.addNodes(type, 1, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0)}))
				.build();
	}
	
	public static HeapConfiguration getInput_DifferentIndices_2() {

		IndexSymbol concreteIndexSymbol = ConcreteIndexSymbol.getIndexSymbol("s", false);
		IndexSymbol abstractIndexSymbol = AbstractIndexSymbol.get("X");
		List<IndexSymbol> index = new ArrayList<>();
		index.add(concreteIndexSymbol);
		index.add(abstractIndexSymbol);
		
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		IndexedNonterminal nt = new IndexedNonterminalImpl("DifferentIndices", 1, new boolean[]{false}, index);
	
		return result.builder()
				.addNodes(type, 1, nodes)
				.addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0)}))
				.build();
	}

	public static HeapConfiguration getInput_EnoughAbstractionDistance() {
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicNonterminal tree = BasicNonterminal.getNonterminal("tree", 2, new boolean[]{false,false});
		BasicNonterminal path = BasicNonterminal.getNonterminal("path", 3, new boolean[]{false,false});
		
		SelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		SelectorLabel right = BasicSelectorLabel.getSelectorLabel("right");
		
		return result.builder()
				.addNodes(type, 9, nodes)
				.addVariableEdge(Constants.NULL, nodes.get(0))
				.addVariableEdge("x", nodes.get(3))
				.addSelector(nodes.get(3), left, nodes.get(1))
				.addSelector(nodes.get(3), right, nodes.get(2))
				.addSelector(nodes.get(4), left, nodes.get(3))
				.addSelector(nodes.get(4), right, nodes.get(0))
				.addNonterminalEdge(tree)
					.addTentacle(nodes.get(1))
					.addTentacle(nodes.get(0))
					.build()
				.addNonterminalEdge(tree)
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(0))
					.build()
				.addNonterminalEdge(path)
					.addTentacle(nodes.get(4))
					.addTentacle(nodes.get(5))
					.addTentacle(nodes.get(0))
					.build()
				.addNonterminalEdge(path)
					.addTentacle(nodes.get(5))
					.addTentacle(nodes.get(6))
					.addTentacle(nodes.get(0))
					.build()
				.addNonterminalEdge(path)
					.addTentacle(nodes.get(6))
					.addTentacle(nodes.get(7))
					.addTentacle(nodes.get(0))
					.build()
				.build();
				
	}

	public static HeapConfiguration getPattern_PathAbstraction() {
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicNonterminal path = BasicNonterminal.getNonterminal("path", 3, new boolean[]{false,false});
		

		return result.builder()
			.addNodes(type, 4, nodes)
			.setExternal(nodes.get(3))
			.setExternal(nodes.get(1))
			.setExternal(nodes.get(0))
			.addNonterminalEdge(path)
				.addTentacle(nodes.get(3))
				.addTentacle(nodes.get(2))
				.addTentacle(nodes.get(0))
				.build()
			.addNonterminalEdge(path)
				.addTentacle(nodes.get(2))
				.addTentacle(nodes.get(1))
				.addTentacle(nodes.get(0))
				.build()
			.build();
				
	}

	public static HeapConfiguration getInput_NotEnoughAbstractionDistance() {
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		
		SelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		
		return result.builder()
				.addNodes(type, 2, nodes)
				.addVariableEdge(Constants.NULL, nodes.get(0))
				.addVariableEdge("y", nodes.get(1))
				.addSelector(nodes.get(1), left, nodes.get(0))
				.build();
	}

	public static HeapConfiguration getPattern_GraphAbstraction() {
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		
		SelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		
		return result.builder()
				.addNodes(type, 2, nodes)
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(0))
				.addSelector(nodes.get(1), left, nodes.get(0))
				.build();
	}

	public static HeapConfiguration getInput_OnlyNonterminalEdgesToAbstract() {
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicNonterminal path = BasicNonterminal.getNonterminal("path", 3, new boolean[]{false,false});
		
		SelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		return result.builder()
				.addNodes(type, 4, nodes)
				.addVariableEdge(Constants.NULL, nodes.get(0))
				.addVariableEdge("x", nodes.get(1))
				.addSelector(nodes.get(1), left, nodes.get(0))
				.addNonterminalEdge(path)
					.addTentacle(nodes.get(1))
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(0))
					.build()
				.addNonterminalEdge(path)
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(3))
					.addTentacle(nodes.get(0))
					.build()
				.build();
	}

	public static HeapConfiguration getInput_variableContains0() {
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicNonterminal tree = BasicNonterminal.getNonterminal("tree", 2, new boolean[]{false,false});
		
		SelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		SelectorLabel right = BasicSelectorLabel.getSelectorLabel("right");
				
		return result.builder().addNodes(type, 3, nodes)
				.addVariableEdge("some-variable0", nodes.get(1))
				.addVariableEdge(Constants.NULL, nodes.get(0))
				.addSelector(nodes.get(1), right, nodes.get(2))
				.addSelector(nodes.get(1), left, nodes.get(0))
				.addNonterminalEdge(tree)
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(0))
					.build()
				.build();
	}

	public static HeapConfiguration getPattern_variableContains0() {
		HeapConfiguration result = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		
		Type type = Settings.getInstance().factory().getType("List");
		BasicNonterminal tree = BasicNonterminal.getNonterminal("tree", 2, new boolean[]{false,false});
		
		SelectorLabel left = BasicSelectorLabel.getSelectorLabel("left");
		SelectorLabel right = BasicSelectorLabel.getSelectorLabel("right");
				
		return result.builder().addNodes(type, 3, nodes)
				.setExternal(nodes.get(1))
				.setExternal(nodes.get(0))
				.addSelector(nodes.get(1), right, nodes.get(2))
				.addSelector(nodes.get(1), left, nodes.get(0))
				.addNonterminalEdge(tree)
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(0))
					.build()
				.build();
	}
}
