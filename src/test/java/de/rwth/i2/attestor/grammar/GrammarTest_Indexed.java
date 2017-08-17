package de.rwth.i2.attestor.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.grammar.materialization.communication.MaterializationAndRuleResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.BalancedTreeGrammar;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.*;
import gnu.trove.iterator.TIntIterator;

public class GrammarTest_Indexed {

	private MaterializationRuleManager grammarManager;
	
	private Nonterminal nonterminal;


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}


	@Before
	public void setUp() throws Exception {
		Grammar grammar = BalancedTreeGrammar.getGrammar();
		ViolationPointResolver vioResolver = new ViolationPointResolver( grammar );
		IndexMatcher stackMatcher = new IndexMatcher( new DefaultStackMaterialization() );
		grammarManager = 
				new IndexedMaterializationRuleManager(vioResolver, stackMatcher);
	}
	
	@Test
	public void testGetRuleGraphsCreatingSelectorNonterminalIntString_Z() 
												throws UnexpectedNonterminalTypeException {
		
		IndexSymbol bottom = ConcreteIndexSymbol.getStackSymbol("Z", true);
		ArrayList<IndexSymbol> stack = new ArrayList<>();
		stack.add(bottom);
		nonterminal = new IndexedNonterminalImpl("B", 2, new boolean[]{false,true}, stack);
		 
			MaterializationAndRuleResponse response = 
					(MaterializationAndRuleResponse) 
					grammarManager.getRulesFor( nonterminal, 0, "left");

			final ArrayList<IndexSymbol> emptyMaterialization = new ArrayList<>();
			 Collection<HeapConfiguration> result = 
					 response.getRulesForMaterialization(emptyMaterialization);
		 
		 assertEquals( 1, result.size() );
		 assertTrue( result.contains( BalancedTreeGrammar.createBalancedLeafRule() ) );
	}

	@Test
	public void testGetRuleGraphsCreatingSelectorNonterminalIntString_sZ() 
			throws UnexpectedNonterminalTypeException {
				
		IndexSymbol s = ConcreteIndexSymbol.getStackSymbol("s", false);
		IndexSymbol bottom = ConcreteIndexSymbol.getStackSymbol("Z", true);
		
		ArrayList<IndexSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(bottom);
		nonterminal = new IndexedNonterminalImpl("B", 2, new boolean[]{false,true}, stack);
		
		MaterializationAndRuleResponse response = 
				(MaterializationAndRuleResponse) 
				grammarManager.getRulesFor( nonterminal, 0, "left");

		final ArrayList<IndexSymbol> emptyMaterialization = new ArrayList<>();
		 Collection<HeapConfiguration> result = 
				 response.getRulesForMaterialization(emptyMaterialization);
		 
		 assertEquals( 3, result.size() );
		 assertTrue( result.contains( BalancedTreeGrammar.createLeftLeafRule()) );
		 for( HeapConfiguration ruleInResult : result ){
		 TIntIterator ntIterator = ruleInResult.nonterminalEdges().iterator();
		 while( ntIterator.hasNext() ){
			 int ntId = ntIterator.next();
			 IndexedNonterminal nt = (IndexedNonterminal) ruleInResult.labelOf( ntId );
			 assertTrue("leftLeafRule not instantiatied", nt.getStack().hasConcreteStack() );
		 }
		 assertTrue( result.contains( BalancedTreeGrammar.createRightLeafRule()) );
		 }
		
	}

}
