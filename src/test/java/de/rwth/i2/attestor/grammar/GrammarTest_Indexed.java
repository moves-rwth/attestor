package de.rwth.i2.attestor.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.grammar.materialization.communication.MaterializationAndRuleResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.BalancedTreeGrammar;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.*;
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
		StackMatcher stackMatcher = new StackMatcher( new DefaultStackMaterialization() );
		grammarManager = 
				new IndexedMaterializationRuleManager(vioResolver, stackMatcher);
	}
	
	@Test
	public void testGetRuleGraphsCreatingSelectorNonterminalIntString_Z() 
												throws UnexpectedNonterminalTypeException {
		
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);
		ArrayList<StackSymbol> stack = new ArrayList<>();
		stack.add(bottom);
		nonterminal = new IndexedNonterminal("B", 2, new boolean[]{false,true}, stack);
		 
			MaterializationAndRuleResponse response = 
					(MaterializationAndRuleResponse) 
					grammarManager.getRulesFor( nonterminal, 0, "left");

			final ArrayList<StackSymbol> emptyMaterialization = new ArrayList<>();
			 Collection<HeapConfiguration> result = 
					 response.getRulesForMaterialization(emptyMaterialization);
		 
		 assertEquals( 1, result.size() );
		 assertTrue( result.contains( BalancedTreeGrammar.createBalancedLeafRule() ) );
	}

	@Test
	public void testGetRuleGraphsCreatingSelectorNonterminalIntString_sZ() 
			throws UnexpectedNonterminalTypeException {
				
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
		StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);
		
		ArrayList<StackSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(bottom);
		nonterminal = new IndexedNonterminal("B", 2, new boolean[]{false,true}, stack);
		
		MaterializationAndRuleResponse response = 
				(MaterializationAndRuleResponse) 
				grammarManager.getRulesFor( nonterminal, 0, "left");

		final ArrayList<StackSymbol> emptyMaterialization = new ArrayList<>();
		 Collection<HeapConfiguration> result = 
				 response.getRulesForMaterialization(emptyMaterialization);
		 
		 assertEquals( 3, result.size() );
		 assertTrue( result.contains( BalancedTreeGrammar.createLeftLeafRule()) );
		 for( HeapConfiguration ruleInResult : result ){
		 TIntIterator ntIterator = ruleInResult.nonterminalEdges().iterator();
		 while( ntIterator.hasNext() ){
			 int ntId = ntIterator.next();
			 IndexedNonterminal nt = (IndexedNonterminal) ruleInResult.labelOf( ntId );
			 assertTrue("leftLeafRule not instantiatied", nt.hasConcreteStack() );
		 }
		 assertTrue( result.contains( BalancedTreeGrammar.createRightLeafRule()) );
		 }
		
	}

}
