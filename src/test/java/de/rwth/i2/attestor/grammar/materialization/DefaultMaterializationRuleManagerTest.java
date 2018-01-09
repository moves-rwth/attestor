package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.MaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPointResolver;
import de.rwth.i2.attestor.grammar.testUtil.FakeViolationPointResolverForDefault;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.main.scene.SceneObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DefaultMaterializationRuleManagerTest {

    private SceneObject sceneObject;

    @Before
    public void init() {

        sceneObject = new MockupSceneObject();
    }


    @Test
    public void testDelegationOfRequest() {

        final int tentacle = 3;
        final boolean[] isReductionTentacle = new boolean[]{true, false};
        final String uniqueLabel = "testDelegationOfRequest";
        final Nonterminal toReplace = sceneObject.scene().createNonterminal(uniqueLabel, tentacle + 2, isReductionTentacle);

        final String requestedSelector = "someSelector";

        ViolationPointResolver vioResolverMock = mock(ViolationPointResolver.class);
        DefaultMaterializationRuleManager ruleManager
                = new DefaultMaterializationRuleManager(vioResolverMock);


        try {
            ruleManager.getRulesFor(toReplace, tentacle, requestedSelector);
        } catch (UnexpectedNonterminalTypeException e) {
            fail("Unexpected exception");
        }
        verify(vioResolverMock).getRulesCreatingSelectorFor(toReplace, tentacle, requestedSelector);
    }

    @Test
    public void testWhetherResponseComplete() {

        FakeViolationPointResolverForDefault grammarLogic = new FakeViolationPointResolverForDefault(sceneObject);
        MaterializationRuleManager ruleManager = new DefaultMaterializationRuleManager(grammarLogic);

        GrammarResponse actualResponse;
        try {

            int tentacleForNext = 0;
            String requestLabel = "some label";
            actualResponse = ruleManager.getRulesFor(grammarLogic.DEFAULT_NONTERMINAL,
                    tentacleForNext,
                    requestLabel);

            assertTrue(actualResponse instanceof DefaultGrammarResponse);
            DefaultGrammarResponse defaultResponse = (DefaultGrammarResponse) actualResponse;
            assertTrue(defaultResponse.getApplicableRules()
                    .contains(grammarLogic.RHS_CREATING_NEXT));
            assertTrue(defaultResponse.getApplicableRules()
                    .contains(grammarLogic.RHS_CREATING_NEXT_PREV));

        } catch (UnexpectedNonterminalTypeException e) {
            fail("Unexpected exception");
        }
    }

}
