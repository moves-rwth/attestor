package de.rwth.i2.attestor.grammar.materialization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.ConcreteStackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class ViolationPointResolverTest_ConcreteNonterminal_ConcreteRule {

	public static final String NONTERMINAL_LABEL = "GrammarLogikTest_ConcreteNonterminal_ConcreteRule";
	public static final Nonterminal NT_STACK_Z = createIndexedNonterminalWithStack_Z();
	public static final Nonterminal NT_STACK_sZ = createIndexedNonterminalWithStack_sZ();
	private static final HeapConfiguration RHS_CREATING_NEXT = getRule_createNext();
	private static final HeapConfiguration RHS_NOT_CREATING_NEXT = getRule_notCreateNext();
	private static final String SELECTOR_NAME_NEXT = "next";
	private static final String OTHER_SELECTOR_NAME = "prev";
	private static final int TENTACLE_WITH_NEXT = 0;
	private static final int TENTACLE_NOT_CREATING_NEXT = 1;


	@Test
	public void testGetRuleGraphsCreatingSelectors_Success(){
		Grammar testGrammar = Grammar.builder().addRule(NT_STACK_Z, RHS_CREATING_NEXT)
				.addRule( NT_STACK_Z, RHS_NOT_CREATING_NEXT)
				.build();
		ViolationPointResolver grammarLogik = new ViolationPointResolver( testGrammar );

		Map<Nonterminal, Collection<HeapConfiguration>> selectedRules = 
				grammarLogik.getRulesCreatingSelectorFor( NT_STACK_Z, TENTACLE_WITH_NEXT, SELECTOR_NAME_NEXT );
		
		assertThat( selectedRules.keySet(), hasSize(1) );
		assertThat( selectedRules.get( NT_STACK_Z ), contains( RHS_CREATING_NEXT) );
		
	}
	
	@Test
	public void testGetRuleGraphsCreatingSelectors_OtherStack(){
		Grammar testGrammar = Grammar.builder().addRule(NT_STACK_Z, RHS_CREATING_NEXT)
				.addRule( NT_STACK_Z, RHS_NOT_CREATING_NEXT)
				.build();
		ViolationPointResolver grammarLogik = new ViolationPointResolver( testGrammar );

		Map<Nonterminal, Collection<HeapConfiguration> > selectedRules = 
				grammarLogik.getRulesCreatingSelectorFor(NT_STACK_sZ, TENTACLE_WITH_NEXT, SELECTOR_NAME_NEXT);
		assertThat( selectedRules.keySet(), hasSize(1) );
		assertThat( selectedRules.get( NT_STACK_Z ), contains( RHS_CREATING_NEXT) );
	}

	@Test
	public void testGetRuleGraphsCreatingSelectors_WrongTentacle(){
		Grammar testGrammar = Grammar.builder().addRule(NT_STACK_Z, RHS_CREATING_NEXT)
				.build();
		ViolationPointResolver grammarLogik = new ViolationPointResolver( testGrammar );

		Map<Nonterminal, Collection<HeapConfiguration> > selectedRules = 
				grammarLogik.getRulesCreatingSelectorFor(NT_STACK_Z, TENTACLE_NOT_CREATING_NEXT, SELECTOR_NAME_NEXT);
		assertThat( selectedRules.keySet(),
				empty() );
	}

	@Test
	public void testGetRuleGraphsCreatingSelectors_WrongSelector(){
		Grammar testGrammar = Grammar.builder().addRule(NT_STACK_Z, RHS_CREATING_NEXT)
				.build();
		ViolationPointResolver grammarLogik = new ViolationPointResolver( testGrammar );

		Map<Nonterminal, Collection<HeapConfiguration> > selectedRules = 
				grammarLogik.getRulesCreatingSelectorFor(NT_STACK_Z, TENTACLE_WITH_NEXT, OTHER_SELECTOR_NAME);
		assertThat( selectedRules.keySet(),	
				empty() );
	}

	private static HeapConfiguration getRule_notCreateNext() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		Type nodeType = TypeFactory.getInstance().getType("type");
		
		SelectorLabel next = GeneralSelectorLabel.getSelectorLabel( SELECTOR_NAME_NEXT );
		SelectorLabel prev = GeneralSelectorLabel.getSelectorLabel( OTHER_SELECTOR_NAME );
		
		Nonterminal nonterminal = NT_STACK_sZ;
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder()
				.addNodes( nodeType, 3, nodes )
				.setExternal( nodes.get(0) )
				.setExternal( nodes.get(1) )
				.addSelector( TENTACLE_NOT_CREATING_NEXT, next, nodes.get(1) )
				.addSelector( nodes.get( TENTACLE_WITH_NEXT), prev, nodes.get(2) )
				.addNonterminalEdge( nonterminal )
					.addTentacle( nodes.get(0) )
					.addTentacle( nodes.get(2) )
					.build()
				.build();
	}

	private static HeapConfiguration getRule_createNext() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		Type nodeType = TypeFactory.getInstance().getType("type");
		
		SelectorLabel next = GeneralSelectorLabel.getSelectorLabel( SELECTOR_NAME_NEXT );
		SelectorLabel prev = GeneralSelectorLabel.getSelectorLabel( OTHER_SELECTOR_NAME );
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder()
				.addNodes( nodeType, 2, nodes )
				.setExternal( nodes.get(0) )
				.setExternal( nodes.get(1) )
				.addSelector( nodes.get( TENTACLE_WITH_NEXT ), next, nodes.get(1) )
				.addSelector( nodes.get( TENTACLE_NOT_CREATING_NEXT ), prev, nodes.get(0) )
				.build();
	}



	private static Nonterminal createIndexedNonterminalWithStack_sZ() {
		StackSymbol s = ConcreteStackSymbol.getStackSymbol( "s", false );
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);

		List<StackSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(bottom);

		return new IndexedNonterminal(NONTERMINAL_LABEL, 2, new boolean[]{false, false}, stack );
	}

	private static Nonterminal createIndexedNonterminalWithStack_Z() {
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);

		List<StackSymbol> stack = new ArrayList<>();
		stack.add(bottom);

		return new IndexedNonterminal(NONTERMINAL_LABEL, 2, new boolean[]{false, false}, stack);
	}


}
