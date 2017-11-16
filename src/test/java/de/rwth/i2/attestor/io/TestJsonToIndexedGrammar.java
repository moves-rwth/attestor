package de.rwth.i2.attestor.io;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.io.jsonImport.JsonToIndexedGrammar;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexVariable;
import org.json.JSONArray;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestJsonToIndexedGrammar {


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
		Settings.getInstance().options().setIndexedMode(true);
	}

	@Test
	public void testParseForwardGrammar() {
		String grammarEncoding = "[	\n"
				+"  {\n"
				+"	\"nonterminal\":\"TestJson\",\n"
				+"	\"rank\":2,\n"
				+"	\"redundandTentacles\":[false,false],\n"
				+"	\"index\":[\"s\",\"()\"],\n"
				+"	\"rules\":[\n"
				+"		{\n"
				+"			\"nodes\":[{\n"
				+"				\"type\":\"type\",\n"
				+"				\"number\":2\n"
				+"			}],\n"
				+"			\"externals\":[0,1],\n"
				+"			\"variables\":[],\n"
				+"			\"selectors\":[],\n"
				+"			\"hyperedges\":[\n"
				+"				{\n"
				+"					\"label\":\"TestJson\",\n"
				+"					\"tentacles\":[0,1],\n"
				+"					\"index\":[\"()\"]\n"
				+"				}\n"
				+"			]\n"
				+"		}]\n"
				+"  },\n"
				+"  {\n"
				+"	\"nonterminal\":\"TestJson\",\n"
				+"	\"rank\":2,\n"
				+"	\"redundandTentacles\":[false,false],\n"
				+"	\"index\":[\"Z\"],\n"
				+"	\"rules\":[\n"
				+"		{\n"
				+"			\"nodes\":[{\n"
				+"				\"type\":\"type\",\n"
				+"				\"number\":2\n"
				+"			}],\n"
				+"			\"externals\":[0,1],\n"
				+"			\"variables\":[],\n"
				+"			\"selectors\":[\n"
				+"				{\n"
				+"					\"label\":\"label\",\n"
				+"					\"annotation\":\"ann\",\n"
				+"					\"origin\":0,\n"
				+"					\"target\":1\n"
				+"				}\n"
				+"			],\n"
				+"			\"hyperedges\":[]\n"
				+"		}\n"
				+"	]\n"
				+"  }\n"
				+"]\n";
		
		
		JSONArray jsonArray = new JSONArray( grammarEncoding );
		Grammar grammar = Grammar.builder()
							.addRules( JsonToIndexedGrammar.parseForwardGrammar( jsonArray ) )
							.build();
		
		assertEquals( 2, grammar.getAllLeftHandSides().size() );
		IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
		IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
		IndexSymbol var = IndexVariable.getIndexVariable();
		List<IndexSymbol> index1 = new ArrayList<>();
		index1.add(s);
		index1.add(var);
		IndexedNonterminal nt1 = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,true}, index1);
		List<IndexSymbol> index2 = new ArrayList<>();
		index2.add(bottom);
		IndexedNonterminal nt2 = new IndexedNonterminalImpl("TestJson", 2, new boolean[]{false,true}, index2);
		
		assertTrue( grammar.getAllLeftHandSides().contains(nt1) );
		assertTrue( grammar.getAllLeftHandSides().contains(nt2) );
		assertNotNull( grammar.getRightHandSidesFor(nt1) );
		assertEquals( 1, grammar.getRightHandSidesFor(nt1).size() );
		assertTrue("rule graph of nt1", grammar.getRightHandSidesFor(nt1).contains(ExpectedHCs.getExpected_Rule1() ));
		assertNotNull( grammar.getRightHandSidesFor(nt2) );
		assertEquals( 1, grammar.getRightHandSidesFor(nt2).size() );
		assertTrue("rule graph of nt2", grammar.getRightHandSidesFor(nt2).contains( ExpectedHCs.getExpected_Rule2() ));
	}

}
