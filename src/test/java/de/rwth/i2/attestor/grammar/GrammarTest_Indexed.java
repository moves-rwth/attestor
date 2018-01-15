package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.materialization.communication.MaterializationAndRuleResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.MaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPointResolver;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.BalancedTreeGrammar;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import gnu.trove.iterator.TIntIterator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GrammarTest_Indexed {

    SceneObject sceneObject;
    BalancedTreeGrammar treeGrammar;
    private MaterializationRuleManager grammarManager;
    private Nonterminal nonterminal;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        treeGrammar = new BalancedTreeGrammar(sceneObject);

        Grammar grammar = treeGrammar.getGrammar();
        ViolationPointResolver vioResolver = new ViolationPointResolver(grammar);

        IndexMatcher indexMatcher = new IndexMatcher(new DefaultIndexMaterialization());
        grammarManager =
                new IndexedMaterializationRuleManager(vioResolver, indexMatcher);
    }

    @Test
    public void testGetRuleGraphsCreatingSelectorNonterminalIntString_Z()
            throws UnexpectedNonterminalTypeException {

        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
        ArrayList<IndexSymbol> index = new ArrayList<>();
        index.add(bottom);
        Nonterminal nt = sceneObject.scene().createNonterminal("B", 2, new boolean[]{false, true});
        nonterminal = new IndexedNonterminalImpl(nt, index);

        MaterializationAndRuleResponse response =
                (MaterializationAndRuleResponse)
                        grammarManager.getRulesFor(nonterminal, 0, "left");

        final ArrayList<IndexSymbol> emptyMaterialization = new ArrayList<>();
        Collection<HeapConfiguration> result =
                response.getRulesForMaterialization(emptyMaterialization);

        assertEquals(1, result.size());
        assertTrue(result.contains(treeGrammar.createBalancedLeafRule()));
    }

    @Test
    public void testGetRuleGraphsCreatingSelectorNonterminalIntString_sZ()
            throws UnexpectedNonterminalTypeException {

        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        ArrayList<IndexSymbol> index = new ArrayList<>();
        index.add(s);
        index.add(bottom);
        Nonterminal nt = sceneObject.scene().createNonterminal("B", 2, new boolean[]{false, true});
        nonterminal = new IndexedNonterminalImpl(nt, index);

        MaterializationAndRuleResponse response =
                (MaterializationAndRuleResponse)
                        grammarManager.getRulesFor(nonterminal, 0, "left");

        final ArrayList<IndexSymbol> emptyMaterialization = new ArrayList<>();
        Collection<HeapConfiguration> result =
                response.getRulesForMaterialization(emptyMaterialization);

        assertEquals(3, result.size());
        assertTrue(result.contains(treeGrammar.createLeftLeafRule()));
        for (HeapConfiguration ruleInResult : result) {
            TIntIterator ntIterator = ruleInResult.nonterminalEdges().iterator();
            while (ntIterator.hasNext()) {
                int ntId = ntIterator.next();
                IndexedNonterminal indexedNonterminal = (IndexedNonterminal) ruleInResult.labelOf(ntId);
                assertTrue("leftLeafRule not instantiatied", indexedNonterminal.getIndex().hasConcreteIndex());
            }
            assertTrue(result.contains(treeGrammar.createRightLeafRule()));
        }

    }

}
