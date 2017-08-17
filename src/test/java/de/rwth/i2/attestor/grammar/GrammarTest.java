package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.BalancedTreeGrammar;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.graph.GeneralNonterminal;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GrammarTest {

	public static final Nonterminal DEFAULT_NONTERMINAL = constructDefaultNonterminal();
	public static final Nonterminal CONCRETE_INDEXED_NONTERMINAL = constructConcreteIndexedNonterminal();
	public static final Nonterminal INSTANTIABLE_INDEXED_NONTERMINAL = constructInstantiableIndexedNonterminal();
	public static final HeapConfiguration RHS_FOR_DEFAULT_NONTERMINAL_1 = ExampleHcImplFactory.getListRule1();
	public static final HeapConfiguration RHS_FOR_DEFAULT_NONTERMINAL_2 = ExampleHcImplFactory.getListRule2();
	public static final Set<HeapConfiguration> RHS_FOR_DEFAULT_NONTERMINAL =
			constructRhsForDefaultNonterminal();
	public static final HeapConfiguration RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1 = 
			BalancedTreeGrammar.createBalancedLeafRule();
	public static final Set<HeapConfiguration> RHS_FOR_CONCRETE_INDEXED_NONTERMINAL =
			constructRhsForConcreteIndexedNonterminal();
	public static final HeapConfiguration RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_1 = 
			BalancedTreeGrammar.createUnbalancedRuleLeft();
	public static final HeapConfiguration RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_2 = 
			BalancedTreeGrammar.createUnbalancedRuleRight();
	public static final Set<HeapConfiguration> RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL = 
			constructRhsForInstantiableIndexedNonterminal();

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}



	@Test
	public void testGrammarOnDefaultNonterminal(){
		Grammar testGrammar = Grammar.builder().addRules( DEFAULT_NONTERMINAL, RHS_FOR_DEFAULT_NONTERMINAL )
				.build();

		assertEquals( RHS_FOR_DEFAULT_NONTERMINAL, testGrammar.getRightHandSidesFor( DEFAULT_NONTERMINAL) );
		assertThat( testGrammar.getAllLeftHandSides(), contains( DEFAULT_NONTERMINAL ) );
	}

	@Test
	public void testGrammarOnIndexedNonterminals(){
		Grammar testGrammar = Grammar.builder()
				.addRules(CONCRETE_INDEXED_NONTERMINAL, 
						RHS_FOR_CONCRETE_INDEXED_NONTERMINAL)
				.addRules(INSTANTIABLE_INDEXED_NONTERMINAL, 
						RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL)
				.build();

		assertEquals( RHS_FOR_CONCRETE_INDEXED_NONTERMINAL, 
				testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL) );
		assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
				testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL) );
		assertThat( testGrammar.getAllLeftHandSides(),
				containsInAnyOrder( CONCRETE_INDEXED_NONTERMINAL, 
						INSTANTIABLE_INDEXED_NONTERMINAL) );
	}

	@Test
	public void testGrammarOnMixedNonterminals(){
		Grammar testGrammar = Grammar.builder()
				.addRules(DEFAULT_NONTERMINAL, RHS_FOR_DEFAULT_NONTERMINAL)
				.addRules(CONCRETE_INDEXED_NONTERMINAL, RHS_FOR_CONCRETE_INDEXED_NONTERMINAL)
				.addRules(INSTANTIABLE_INDEXED_NONTERMINAL, RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL)
				.build();

		assertEquals( RHS_FOR_CONCRETE_INDEXED_NONTERMINAL, 
				testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL) );
		assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
				testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL) );
		assertEquals( RHS_FOR_DEFAULT_NONTERMINAL, 
				testGrammar.getRightHandSidesFor( DEFAULT_NONTERMINAL) );
		assertThat( testGrammar.getAllLeftHandSides(),
				containsInAnyOrder(DEFAULT_NONTERMINAL,
						CONCRETE_INDEXED_NONTERMINAL,
						INSTANTIABLE_INDEXED_NONTERMINAL)
				);

	}
	
	@Test
	public void testAddRulesAsMap(){
		Map<Nonterminal, Collection<HeapConfiguration>> rules = new HashMap<>();
		rules.put(DEFAULT_NONTERMINAL, RHS_FOR_DEFAULT_NONTERMINAL);
		rules.put(CONCRETE_INDEXED_NONTERMINAL, RHS_FOR_CONCRETE_INDEXED_NONTERMINAL);
		rules.put(INSTANTIABLE_INDEXED_NONTERMINAL, RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL );
		
		Grammar testGrammar = Grammar.builder().addRules( rules ).build();
		
		assertEquals( RHS_FOR_CONCRETE_INDEXED_NONTERMINAL, 
				testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL) );
		assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
				testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL) );
		assertEquals( RHS_FOR_DEFAULT_NONTERMINAL, 
				testGrammar.getRightHandSidesFor( DEFAULT_NONTERMINAL) );
		assertThat( testGrammar.getAllLeftHandSides(),
				containsInAnyOrder(DEFAULT_NONTERMINAL,
						CONCRETE_INDEXED_NONTERMINAL,
						INSTANTIABLE_INDEXED_NONTERMINAL)
				);
		
	}
	
	@Test
	public void testBuildGrammarWithMapAndMultipleRule(){
		Map<Nonterminal, Collection<HeapConfiguration>> rules = new HashMap<>();
		rules.put(DEFAULT_NONTERMINAL, RHS_FOR_DEFAULT_NONTERMINAL);
		rules.put(INSTANTIABLE_INDEXED_NONTERMINAL, RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL );
		
		Grammar testGrammar = Grammar.builder().addRules( rules )
												.addRules(CONCRETE_INDEXED_NONTERMINAL, 
														RHS_FOR_CONCRETE_INDEXED_NONTERMINAL)
												.build();
		
		assertEquals( RHS_FOR_CONCRETE_INDEXED_NONTERMINAL, 
				testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL) );
		assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
				testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL) );
		assertEquals( RHS_FOR_DEFAULT_NONTERMINAL, 
				testGrammar.getRightHandSidesFor( DEFAULT_NONTERMINAL) );
		assertThat( testGrammar.getAllLeftHandSides(),
				containsInAnyOrder(DEFAULT_NONTERMINAL,
						CONCRETE_INDEXED_NONTERMINAL,
						INSTANTIABLE_INDEXED_NONTERMINAL)
				);
	}
	
	@Test
	public void testBuildGrammarWithSingleRuleAndMap(){
		Map<Nonterminal, Collection<HeapConfiguration>> rules = new HashMap<>();
		rules.put(DEFAULT_NONTERMINAL, RHS_FOR_DEFAULT_NONTERMINAL);
		rules.put(INSTANTIABLE_INDEXED_NONTERMINAL, RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL );
		
		Grammar testGrammar = Grammar.builder().addRule(CONCRETE_INDEXED_NONTERMINAL, 
														RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1)
												.addRules( rules )
												.build();
		
		assertEquals( RHS_FOR_CONCRETE_INDEXED_NONTERMINAL, 
				testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL) );
		assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
				testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL) );
		assertEquals( RHS_FOR_DEFAULT_NONTERMINAL, 
				testGrammar.getRightHandSidesFor( DEFAULT_NONTERMINAL) );
		assertThat( testGrammar.getAllLeftHandSides(),
				containsInAnyOrder(DEFAULT_NONTERMINAL,
						CONCRETE_INDEXED_NONTERMINAL,
						INSTANTIABLE_INDEXED_NONTERMINAL)
				);
	}
	
	
	
	@Test
	public void testBuildGrammarWithMultipleSingleRules(){
		Grammar testGrammar = Grammar.builder()
				.addRule(CONCRETE_INDEXED_NONTERMINAL, 
						RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1)
				.addRule(INSTANTIABLE_INDEXED_NONTERMINAL, 
						RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_1)
				.addRule( INSTANTIABLE_INDEXED_NONTERMINAL, 
						RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_2 )
				.build();

		assertEquals( RHS_FOR_CONCRETE_INDEXED_NONTERMINAL, 
				testGrammar.getRightHandSidesFor(CONCRETE_INDEXED_NONTERMINAL) );
		assertEquals(RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL,
				testGrammar.getRightHandSidesFor(INSTANTIABLE_INDEXED_NONTERMINAL) );
		assertThat( testGrammar.getAllLeftHandSides(),
				containsInAnyOrder( CONCRETE_INDEXED_NONTERMINAL, 
						INSTANTIABLE_INDEXED_NONTERMINAL) );
	}
	
	@Test
	public void testGetRuleGraphsForNonExistingNonterminal(){
		Grammar testGrammar = Grammar.builder()
				.addRule(CONCRETE_INDEXED_NONTERMINAL, 
						RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1)
				.build();
		
		assertThat( testGrammar.getRightHandSidesFor(DEFAULT_NONTERMINAL), empty() );
	}

	private static Nonterminal constructDefaultNonterminal() {
		final boolean[] reductionTentacles = new boolean[]{false,true};
		final int rank = 2;
		final String label = "List";
		return GeneralNonterminal.getNonterminal(label, rank, reductionTentacles);
	}

	private static Set<HeapConfiguration> constructRhsForDefaultNonterminal(){
		Set<HeapConfiguration> rhs = new HashSet<>();
		rhs.add( RHS_FOR_DEFAULT_NONTERMINAL_1 );
		rhs.add( RHS_FOR_DEFAULT_NONTERMINAL_2 );
		return rhs;
	}

	private static IndexedNonterminal constructConcreteIndexedNonterminal() {
		ConcreteIndexSymbol bottom = ConcreteIndexSymbol.getStackSymbol("Z", true);
		ArrayList<IndexSymbol> lhsStack = new ArrayList<>();
		lhsStack.add( bottom );
		return new IndexedNonterminalImpl("B", 2, new boolean[]{false, true}, lhsStack );
	}

	private static Set<HeapConfiguration> constructRhsForConcreteIndexedNonterminal(){
		Set<HeapConfiguration> rhs = new HashSet<>();
		rhs.add( RHS_FOR_CONCRETE_INDEXED_NONTERMINAL_1 );
		return rhs;
	}

	private static Nonterminal constructInstantiableIndexedNonterminal() {
		final IndexVariable var = IndexVariable.getGlobalInstance();
		final ConcreteIndexSymbol s = ConcreteIndexSymbol.getStackSymbol("s", false);
		ArrayList<IndexSymbol> lhsStack = new ArrayList<>();
		lhsStack.add( s);
		lhsStack.add(var);
		return new IndexedNonterminalImpl("B", 2, new boolean[]{false, true}, lhsStack );
	}

	private static Set<HeapConfiguration> constructRhsForInstantiableIndexedNonterminal() {
		Set<HeapConfiguration> rhs = new HashSet<>();
		rhs.add( RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_1 );
		rhs.add( RHS_FOR_INSTANTIABLE_INDEXED_NONTERMINAL_2 );
		return rhs;
	}
}
