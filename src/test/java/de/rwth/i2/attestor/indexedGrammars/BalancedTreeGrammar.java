package de.rwth.i2.attestor.indexedGrammars;

import java.util.ArrayList;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.GrammarBuilder;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.stack.*;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import gnu.trove.list.array.TIntArrayList;

public class BalancedTreeGrammar{

	public static final AnnotatedSelectorLabel SELECTOR_RIGHT_0 = new AnnotatedSelectorLabel("right", "0");
	public static final AnnotatedSelectorLabel SELECTOR_RIGHT_1 = new AnnotatedSelectorLabel("right", "1");
	public static final AnnotatedSelectorLabel SELECTOR_LEFT_M1 = new AnnotatedSelectorLabel("left", "-1");
	public static final AnnotatedSelectorLabel SELECTOR_PARENT = new AnnotatedSelectorLabel("parent", "");
	public static final AnnotatedSelectorLabel SELECTOR_RIGHT_M1 = new AnnotatedSelectorLabel("right", "-1");
	public static final AnnotatedSelectorLabel SELECTOR_LEFT_1 = new AnnotatedSelectorLabel("left", "1");
	public static final AnnotatedSelectorLabel SELECTOR_LEFT_0 = new AnnotatedSelectorLabel("left", "0");
	public static final Type TYPE = TypeFactory.getInstance().getType("AVLTree");
	public static final boolean[] IS_REDUCTION_TENTACLE = new boolean[]{false, true};
	public static final int NT_RANK = 2;
	public static final String NT_LABEL = "B";

	public static  Grammar getGrammar(){
		GrammarBuilder builder = Grammar.builder();
		addRuleBalanced( builder );
		addUnbalancedRuleLeft( builder );
		addUnbalancedRuleRight( builder );
		addLeftLeafRule( builder );
		addRightLeafRule( builder );
		addBalancedLeafRule( builder );
		return builder.build();
	}
	
	private static void addRuleBalanced(GrammarBuilder builder){
		StackVariable var = StackVariable.getGlobalInstance();
		ArrayList<StackSymbol> lhsStack = new ArrayList<>();
		lhsStack.add( ConcreteStackSymbol.getStackSymbol("s", false));
		lhsStack.add(var);
		IndexedNonterminal lhs = new IndexedNonterminalImpl(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE, lhsStack );
		
		HeapConfiguration rhs = createRuleBalanced();
		
		builder.addRule(lhs, rhs);
	}

	public static HeapConfiguration createRuleBalanced() {
		
		StackVariable var =  StackVariable.getGlobalInstance();
		
		ArrayList<StackSymbol> r = new ArrayList<>();
		r.add(var);
		IndexedNonterminal rightNt = new IndexedNonterminalImpl(NT_LABEL, r);
		ArrayList<StackSymbol> l = new ArrayList<>();
		l.add(var);
		IndexedNonterminal leftNt = new IndexedNonterminalImpl(NT_LABEL,l);
		AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_0;
		AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_0;
		AnnotatedSelectorLabel parentLabel = SELECTOR_PARENT;
		
		HeapConfiguration rhs = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		rhs = rhs.builder().addNodes(TYPE, 4, nodes)
						.setExternal( nodes.get(0))
						.setExternal(nodes.get(3))
						.addSelector(nodes.get(0), leftLabel, nodes.get(1))
						.addSelector(nodes.get(1), parentLabel, nodes.get(0))
						.addSelector(nodes.get(0), rightLabel, nodes.get(2))
						.addSelector(nodes.get(2), parentLabel, nodes.get(0))
						.addNonterminalEdge(leftNt, new TIntArrayList(new int[]{nodes.get(1), nodes.get(3)}))
						.addNonterminalEdge(rightNt, new TIntArrayList(new int[]{nodes.get(2), nodes.get(3)}))
						.build();
		return rhs;
	}
	
	private static void addUnbalancedRuleLeft(GrammarBuilder builder){
		StackVariable var = StackVariable.getGlobalInstance();
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		
		ArrayList<StackSymbol> lhsStack = new ArrayList<>();
		lhsStack.add( s );
		lhsStack.add( s );
		lhsStack.add(var);
		IndexedNonterminal lhs = new IndexedNonterminalImpl(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE, lhsStack );
		
		HeapConfiguration rhs = createUnbalancedRuleLeft();
		
		builder.addRule(lhs, rhs);
	}

	public static HeapConfiguration createUnbalancedRuleLeft() {
		
		StackVariable var = StackVariable.getGlobalInstance();
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		
		ArrayList<StackSymbol> r = new ArrayList<>();
		r.add(var);
		IndexedNonterminal rightNt = new IndexedNonterminalImpl(NT_LABEL, r);
		ArrayList<StackSymbol> l = new ArrayList<>();
		l.add(s);
		l.add(var);
		IndexedNonterminal leftNt = new IndexedNonterminalImpl(NT_LABEL,l);
		AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_1;
		AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_M1;
		AnnotatedSelectorLabel parentLabel = SELECTOR_PARENT;
		
		HeapConfiguration rhs = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		rhs = rhs.builder().addNodes(TYPE, 4, nodes)
						.setExternal( nodes.get(0))
						.setExternal(nodes.get(3))
						.addSelector(nodes.get(0), leftLabel, nodes.get(1))
						.addSelector(nodes.get(1), parentLabel, nodes.get(0))
						.addSelector(nodes.get(0), rightLabel, nodes.get(2))
						.addSelector(nodes.get(2), parentLabel, nodes.get(0))
						.addNonterminalEdge(leftNt, new TIntArrayList(new int[]{nodes.get(1), nodes.get(3)}))
						.addNonterminalEdge(rightNt, new TIntArrayList(new int[]{nodes.get(2), nodes.get(3)}))
						.build();
		return rhs;
	}
	
	private static void addUnbalancedRuleRight(GrammarBuilder builder){
		StackVariable var = StackVariable.getGlobalInstance();
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		
		ArrayList<StackSymbol> lhsStack = new ArrayList<>();
		lhsStack.add( s );
		lhsStack.add( s );
		lhsStack.add(var);
		IndexedNonterminal lhs = new IndexedNonterminalImpl(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE, lhsStack );
		
		HeapConfiguration rhs = createUnbalancedRuleRight();
		
		builder.addRule(lhs, rhs);
	}

	public static HeapConfiguration createUnbalancedRuleRight() {
		StackVariable var = StackVariable.getGlobalInstance();
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		
		ArrayList<StackSymbol> r = new ArrayList<>();
		r.add(s);
		r.add(var);
		IndexedNonterminal rightNt = new IndexedNonterminalImpl(NT_LABEL, r);
		ArrayList<StackSymbol> l = new ArrayList<>();
		l.add(var);
		IndexedNonterminal leftNt = new IndexedNonterminalImpl(NT_LABEL,l);
		AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_M1;
		AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_1;
		AnnotatedSelectorLabel parentLabel = SELECTOR_PARENT;
		
		HeapConfiguration rhs = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		rhs = rhs.builder().addNodes(TYPE, 4, nodes)
						.setExternal( nodes.get(0))
						.setExternal(nodes.get(3))
						.addSelector(nodes.get(0), leftLabel, nodes.get(1))
						.addSelector(nodes.get(1), parentLabel, nodes.get(0))
						.addSelector(nodes.get(0), rightLabel, nodes.get(2))
						.addSelector(nodes.get(2), parentLabel, nodes.get(0))
						.addNonterminalEdge(leftNt, new TIntArrayList(new int[]{nodes.get(1), nodes.get(3)}))
						.addNonterminalEdge(rightNt, new TIntArrayList(new int[]{nodes.get(2), nodes.get(3)}))
						.build();
		return rhs;
	}

	private static void addBalancedLeafRule(GrammarBuilder builder){
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);

		ArrayList<StackSymbol> lhsStack = new ArrayList<>();
		lhsStack.add( bottom );
		IndexedNonterminal lhs = new IndexedNonterminalImpl(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE, lhsStack );
		
		HeapConfiguration rhs = createBalancedLeafRule();
		
		builder.addRule(lhs, rhs);
	}

	public static HeapConfiguration createBalancedLeafRule() {
		AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_0;
		AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_0;
		
		HeapConfiguration rhs = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		rhs = rhs.builder().addNodes(TYPE, 2, nodes)
						.setExternal( nodes.get(0))
						.setExternal(nodes.get(1))
						.addSelector(nodes.get(0), leftLabel, nodes.get(1))
						.addSelector(nodes.get(0), rightLabel, nodes.get(1))
						.build();
		return rhs;
	}
	
	private static void addLeftLeafRule(GrammarBuilder builder){
		ArrayList<StackSymbol> lhsStack = new ArrayList<>();
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);
		lhsStack.add( s );
		lhsStack.add(bottom);
		IndexedNonterminal lhs = new IndexedNonterminalImpl(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE, lhsStack );

		HeapConfiguration rhs = createLeftLeafRule();
		
		builder.addRule(lhs, rhs);
	}

	public static HeapConfiguration createLeftLeafRule() {
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);
		
		ArrayList<StackSymbol> l = new ArrayList<>();
		l.add(bottom);
		IndexedNonterminal leftNt = new IndexedNonterminalImpl(NT_LABEL,l);
		AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_1;
		AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_M1;
		AnnotatedSelectorLabel parentLabel = SELECTOR_PARENT;
		
		HeapConfiguration rhs = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		rhs = rhs.builder().addNodes(TYPE, 3, nodes)
						.setExternal( nodes.get(0))
						.setExternal(nodes.get(2))
						.addSelector(nodes.get(0), leftLabel, nodes.get(1))
						.addSelector(nodes.get(1), parentLabel, nodes.get(0))
						.addSelector(nodes.get(0), rightLabel, nodes.get(2))
						.addNonterminalEdge(leftNt, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
						.build();
		return rhs;
	}

	private static void addRightLeafRule(GrammarBuilder builder){
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);
		
		ArrayList<StackSymbol> lhsStack = new ArrayList<>();
		lhsStack.add( s );
		lhsStack.add(bottom);
		IndexedNonterminal lhs = new IndexedNonterminalImpl(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE, lhsStack );

		HeapConfiguration rhs = createRightLeafRule();
		
		builder.addRule(lhs, rhs);
	}

	public static HeapConfiguration createRightLeafRule() {
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);
		
		ArrayList<StackSymbol> r = new ArrayList<>();
		r.add(bottom);
		IndexedNonterminal rightNt = new IndexedNonterminalImpl(NT_LABEL,r);
		AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_M1;
		AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_1;
		AnnotatedSelectorLabel parentLabel = SELECTOR_PARENT;
		
		HeapConfiguration rhs = new InternalHeapConfiguration();
		TIntArrayList nodes = new TIntArrayList();
		rhs = rhs.builder().addNodes(TYPE, 3, nodes)
						.setExternal( nodes.get(0))
						.setExternal(nodes.get(2))
						.addSelector(nodes.get(0), rightLabel, nodes.get(1))
						.addSelector(nodes.get(1), parentLabel, nodes.get(0))
						.addSelector(nodes.get(0), leftLabel, nodes.get(2))
						.addNonterminalEdge(rightNt, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
						.build();
		return rhs;
	}

}
