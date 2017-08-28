package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.testUtil.FakeViolationPointResolverForDefault;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.GeneralNonterminal;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DefaultMaterializationRuleManagerTest {


	@BeforeClass
	public static void init() {

        UnitTestGlobalSettings.reset();
    }


	@Test
	public void testDelegationOfRequest() {
		
		final int tentacle = 3;
		final boolean[] isReductionTentacle = new boolean[]{true, false};
		final String uniqueLabel = "testDelegationOfRequest";
		final Nonterminal toReplace = GeneralNonterminal.getNonterminal(uniqueLabel, tentacle + 2, isReductionTentacle);
		
		final String requestedSelector = "someSelector";
		
		ViolationPointResolver vioResolverMock = mock(ViolationPointResolver.class);
		DefaultMaterializationRuleManager ruleManager 
			= new DefaultMaterializationRuleManager( vioResolverMock );
		
		
		try {
			ruleManager.getRulesFor(toReplace, tentacle, requestedSelector);
		} catch (UnexpectedNonterminalTypeException e) {
			fail("Unexpected exception");
		}
		verify( vioResolverMock ).getRulesCreatingSelectorFor(toReplace, tentacle, requestedSelector);
	}
	
	@Test
	public void testWhetherResponseComplete(){
		
		ViolationPointResolver grammarLogic = new FakeViolationPointResolverForDefault();
		MaterializationRuleManager ruleManager = new DefaultMaterializationRuleManager(grammarLogic);
		
		GrammarResponse actualResponse;
		try {
			Nonterminal nonterminal = FakeViolationPointResolverForDefault.DEFAULT_NONTERMINAL;
			int tentacleForNext = 0;
			String requestLabel = "some label";
			actualResponse = ruleManager.getRulesFor(nonterminal, 
													tentacleForNext, 
													requestLabel);
		
			assertTrue( actualResponse instanceof DefaultGrammarResponse );
			DefaultGrammarResponse defaultResponse = (DefaultGrammarResponse) actualResponse;
			assertTrue(defaultResponse.getApplicableRules()
					.contains( FakeViolationPointResolverForDefault.RHS_CREATING_NEXT) );
			assertTrue( defaultResponse.getApplicableRules()
					.contains( FakeViolationPointResolverForDefault.RHS_CREATING_NEXT_PREV ));
			
		} catch (UnexpectedNonterminalTypeException e) {
			fail("Unexpected exception");
		}
	}
	
}
