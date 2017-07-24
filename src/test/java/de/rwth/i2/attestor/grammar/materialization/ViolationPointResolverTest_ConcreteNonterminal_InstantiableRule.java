package de.rwth.i2.attestor.grammar.materialization;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.ConcreteStackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.StackVariable;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class ViolationPointResolverTest_ConcreteNonterminal_InstantiableRule {


	private static final int NONTERMINAL_RANK = 2;
	private static final String NONTERMINAL_LABEL = "GrammarLogikText_Concrete_Instatiable";
	
	private static final Nonterminal NONTERMINAL_STACK_Z = createNonterminal_Z();
	private static final Nonterminal NONTERMINAL_INSTANTIABLE = createNonterminal_Instantiable();
	private static final Nonterminal NONTERMINAL_NON_MATCHING = createNonterminal_s();
	
	private static final HeapConfiguration INSTANTIABLE_RULE_CREATING_NEXT = getInstantiableRuleCreatingNext();
	//private static final HeapConfiguration INSTANTIATED_RULE_CREATING_NEXT = getInstantiatedRuleCreatingNext();
	private static final HeapConfiguration INSTANTIABLE_RULE_NOT_CREATING_NEXT = getInstantiableRuleNotCreatingNext();
	
	
	private static final int TENTACLE_WITH_NEXT = 0;
	private static final String SELECTOR_NEXT_NAME = "next";
	private static final String OTHER_SELECTOR_NAME = "prev";
	private static final int TENTACLE_NOT_CREATING_NEXT = 1;

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}



	@Test
	public void testGetRuleGraphsConstructionSelector_OneElementStack() {
		
		Grammar testGrammar = Grammar.builder()
									.addRule( NONTERMINAL_INSTANTIABLE, INSTANTIABLE_RULE_CREATING_NEXT )
									.addRule( NONTERMINAL_INSTANTIABLE, INSTANTIABLE_RULE_NOT_CREATING_NEXT)
									.addRule( NONTERMINAL_NON_MATCHING, INSTANTIABLE_RULE_CREATING_NEXT )
									.build();
		ViolationPointResolver grammarLogik = new ViolationPointResolver(testGrammar);
		
		Map<Nonterminal, Collection<HeapConfiguration>> selectedRules
 				= grammarLogik.getRulesCreatingSelectorFor(NONTERMINAL_STACK_Z, 
						  TENTACLE_WITH_NEXT, 
						  SELECTOR_NEXT_NAME);
		assertThat( selectedRules.keySet(),
				   containsInAnyOrder( NONTERMINAL_INSTANTIABLE, NONTERMINAL_NON_MATCHING ) );
		assertThat( selectedRules.get(NONTERMINAL_INSTANTIABLE),
				   containsInAnyOrder(INSTANTIABLE_RULE_CREATING_NEXT) );
		assertThat( selectedRules.get( NONTERMINAL_NON_MATCHING ),
				containsInAnyOrder(INSTANTIABLE_RULE_CREATING_NEXT) );
		
	}
	




	private static Nonterminal createNonterminal_Z() {
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);
		List<StackSymbol> stack = SingleElementUtil.createList( bottom );
				
		boolean[] reductionTentacles = new boolean[]{false,false};
		return new IndexedNonterminal(NONTERMINAL_LABEL, NONTERMINAL_RANK, reductionTentacles , stack);
	}

	private static Nonterminal createNonterminal_Instantiable() {
		StackSymbol var = StackVariable.getGlobalInstance();
		List<StackSymbol> stack = SingleElementUtil.createList( var );
		
		boolean[] reductionTentacles = new boolean[]{false,false};
		return new IndexedNonterminal(NONTERMINAL_LABEL, NONTERMINAL_RANK, reductionTentacles , stack);
	}
	
	private static Nonterminal createNonterminal_s() {
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		StackSymbol var = StackVariable.getGlobalInstance();
		List<StackSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(var);
		
		boolean[] reductionTentacles = new boolean[]{false,false};
		return new IndexedNonterminal(NONTERMINAL_LABEL, NONTERMINAL_RANK, reductionTentacles , stack);
	}

	private static HeapConfiguration getInstantiableRuleCreatingNext() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		Type nodeType = TypeFactory.getInstance().getType("type");
		
		SelectorLabel next = GeneralSelectorLabel.getSelectorLabel( SELECTOR_NEXT_NAME );
		SelectorLabel prev = GeneralSelectorLabel.getSelectorLabel( OTHER_SELECTOR_NAME );
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder()
				.addNodes( nodeType, 3, nodes )
				.setExternal( nodes.get(0) )
				.setExternal( nodes.get(1) )
				.addSelector( nodes.get( TENTACLE_WITH_NEXT ), next, nodes.get(1) )
				.addSelector( nodes.get( TENTACLE_NOT_CREATING_NEXT ), prev, nodes.get(0) )
				.addNonterminalEdge(NONTERMINAL_INSTANTIABLE)
					.addTentacle( nodes.get(1) )
					.addTentacle( nodes.get(2) )
					.build()
				.build();
	}
	
	private static HeapConfiguration getInstantiableRuleNotCreatingNext() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		Type nodeType = TypeFactory.getInstance().getType("type");
		
		SelectorLabel next = GeneralSelectorLabel.getSelectorLabel( SELECTOR_NEXT_NAME );
		SelectorLabel prev = GeneralSelectorLabel.getSelectorLabel( OTHER_SELECTOR_NAME );
		
		Nonterminal nonterminal = NONTERMINAL_INSTANTIABLE;
		
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


}
