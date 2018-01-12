package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPointResolver;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ViolationPointResolverTest_ConcreteNonterminal_ConcreteRule {

    public static final String NONTERMINAL_LABEL = "GrammarLogikTest_ConcreteNonterminal_ConcreteRule";
    private static final String SELECTOR_NAME_NEXT = "next";
    private static final String OTHER_SELECTOR_NAME = "prev";
    private static final int TENTACLE_WITH_NEXT = 0;
    private static final int TENTACLE_NOT_CREATING_NEXT = 1;
    public Nonterminal NT_INDEX_Z;
    public Nonterminal NT_INDEX_sZ;
    private HeapConfiguration RHS_CREATING_NEXT;
    private HeapConfiguration RHS_NOT_CREATING_NEXT;
    private SceneObject sceneObject;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        NT_INDEX_Z = createIndexedNonterminalWithIndex_Z();
        NT_INDEX_sZ = createIndexedNonterminalWithIndex_sZ();
        RHS_CREATING_NEXT = getRule_createNext();
        RHS_NOT_CREATING_NEXT = getRule_notCreateNext();
    }

    @Test
    public void testGetRuleGraphsCreatingSelectors_Success() {

        Grammar testGrammar = Grammar.builder().addRule(NT_INDEX_Z, RHS_CREATING_NEXT)
                .addRule(NT_INDEX_Z, RHS_NOT_CREATING_NEXT)
                .build();
        ViolationPointResolver grammarLogik = new ViolationPointResolver(testGrammar);

        Map<Nonterminal, Collection<HeapConfiguration>> selectedRules =
                grammarLogik.getRulesCreatingSelectorFor(NT_INDEX_Z, TENTACLE_WITH_NEXT, SELECTOR_NAME_NEXT);

        assertThat(selectedRules.keySet(), hasSize(1));
        assertThat(selectedRules.get(NT_INDEX_Z), contains(RHS_CREATING_NEXT));

    }

    @Test
    public void testGetRuleGraphsCreatingSelectors_OtherIndex() {

        Grammar testGrammar = Grammar.builder().addRule(NT_INDEX_Z, RHS_CREATING_NEXT)
                .addRule(NT_INDEX_Z, RHS_NOT_CREATING_NEXT)
                .build();
        ViolationPointResolver grammarLogik = new ViolationPointResolver(testGrammar);

        Map<Nonterminal, Collection<HeapConfiguration>> selectedRules =
                grammarLogik.getRulesCreatingSelectorFor(NT_INDEX_sZ, TENTACLE_WITH_NEXT, SELECTOR_NAME_NEXT);
        assertThat(selectedRules.keySet(), hasSize(1));
        assertThat(selectedRules.get(NT_INDEX_Z), contains(RHS_CREATING_NEXT));
    }

    @Test
    public void testGetRuleGraphsCreatingSelectors_WrongTentacle() {

        Grammar testGrammar = Grammar.builder().addRule(NT_INDEX_Z, RHS_CREATING_NEXT)
                .build();
        ViolationPointResolver grammarLogik = new ViolationPointResolver(testGrammar);

        Map<Nonterminal, Collection<HeapConfiguration>> selectedRules =
                grammarLogik.getRulesCreatingSelectorFor(NT_INDEX_Z, TENTACLE_NOT_CREATING_NEXT, SELECTOR_NAME_NEXT);
        assertThat(selectedRules.keySet(),
                empty());
    }

    @Test
    public void testGetRuleGraphsCreatingSelectors_WrongSelector() {

        Grammar testGrammar = Grammar.builder().addRule(NT_INDEX_Z, RHS_CREATING_NEXT)
                .build();
        ViolationPointResolver grammarLogik = new ViolationPointResolver(testGrammar);

        Map<Nonterminal, Collection<HeapConfiguration>> selectedRules =
                grammarLogik.getRulesCreatingSelectorFor(NT_INDEX_Z, TENTACLE_WITH_NEXT, OTHER_SELECTOR_NAME);
        assertThat(selectedRules.keySet(),
                empty());
    }

    private HeapConfiguration getRule_notCreateNext() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type nodeType = sceneObject.scene().getType("type");

        SelectorLabel next = sceneObject.scene().getSelectorLabel(SELECTOR_NAME_NEXT);
        SelectorLabel prev = sceneObject.scene().getSelectorLabel(OTHER_SELECTOR_NAME);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder()
                .addNodes(nodeType, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(TENTACLE_NOT_CREATING_NEXT, next, nodes.get(1))
                .addSelector(nodes.get(TENTACLE_WITH_NEXT), prev, nodes.get(2))
                .addNonterminalEdge(NT_INDEX_sZ)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(2))
                .build()
                .build();
    }

    private HeapConfiguration getRule_createNext() {

        HeapConfiguration hc = new InternalHeapConfiguration();
        Type nodeType = sceneObject.scene().getType("type");

        SelectorLabel next = sceneObject.scene().getSelectorLabel(SELECTOR_NAME_NEXT);
        SelectorLabel prev = sceneObject.scene().getSelectorLabel(OTHER_SELECTOR_NAME);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder()
                .addNodes(nodeType, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(TENTACLE_WITH_NEXT), next, nodes.get(1))
                .addSelector(nodes.get(TENTACLE_NOT_CREATING_NEXT), prev, nodes.get(0))
                .build();
    }


    private Nonterminal createIndexedNonterminalWithIndex_sZ() {

        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        List<IndexSymbol> index = new ArrayList<>();
        index.add(s);
        index.add(bottom);

        Nonterminal bnt = sceneObject.scene().createNonterminal(NONTERMINAL_LABEL, 2, new boolean[]{false, false});
        return new IndexedNonterminalImpl(bnt, index);
    }

    private Nonterminal createIndexedNonterminalWithIndex_Z() {

        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        List<IndexSymbol> index = new ArrayList<>();
        index.add(bottom);

        Nonterminal bnt = sceneObject.scene().createNonterminal(NONTERMINAL_LABEL, 2, new boolean[]{false, false});
        return new IndexedNonterminalImpl(bnt, index);
    }


}
