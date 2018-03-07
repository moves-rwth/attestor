package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.materialization.communication.CannotMaterializeException;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.MaterializationAndRuleResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.WrongResponseTypeException;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.util.GraphMaterializer;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.mockito.Mockito.*;

public class IndexedGrammarResponseApplierTest {

    private static final int EDGE_ID = -1;
    private static final String UNIQUE_NT_LABEL = "IndexedGrammarResponseApplierTest";
    private static final int RANK = 2;
    private static final boolean[] REDUCTION_TENTACLES = new boolean[]{true, false};

    private SceneObject sceneObject;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
    }

    @Test
    public void testDelegation() throws WrongResponseTypeException, CannotMaterializeException {

        IndexMaterializationStrategy indexMaterializationStrategyMock = mock(IndexMaterializationStrategy.class);
        GraphMaterializer graphMaterializerMock = mock(GraphMaterializer.class);

        IndexedGrammarResponseApplier ruleApplier =
                new IndexedGrammarResponseApplier(indexMaterializationStrategyMock, graphMaterializerMock);

        HeapConfiguration inputGraph = createInputGraph();

        Map<List<IndexSymbol>, Collection<HeapConfiguration>> rules = new LinkedHashMap<>();
        List<IndexSymbol> materialization1 = createEmptyMaterialization();
        rules.put(materialization1, new ArrayList<>());
        HeapConfiguration mat1_rule1 = createSimpleRule();
        rules.get(materialization1).add(mat1_rule1);
        List<IndexSymbol> materialization2 = createNonEmptyMaterialization();
        rules.put(materialization2, new ArrayList<>());
        HeapConfiguration mat2_rule1 = createSimpleRule();
        rules.get(materialization2).add(mat2_rule1);
        HeapConfiguration mat2_rule2 = createBigRule();
        rules.get(materialization2).add(mat2_rule2);

        AbstractIndexSymbol symbolToMaterialize = AbstractIndexSymbol.get("SYMBOL TO MATERIALIZE");

        GrammarResponse grammarResponse = new MaterializationAndRuleResponse(rules, symbolToMaterialize);

        ruleApplier.applyGrammarResponseTo(inputGraph, EDGE_ID, grammarResponse);
        verify(indexMaterializationStrategyMock).getMaterializedCloneWith(inputGraph, symbolToMaterialize, materialization1);
        verify(indexMaterializationStrategyMock).getMaterializedCloneWith(inputGraph, symbolToMaterialize, materialization2);
        verify(graphMaterializerMock, times(2)).getMaterializedCloneWith(anyObject(), eq(EDGE_ID), eq(mat1_rule1));
        verify(graphMaterializerMock).getMaterializedCloneWith(anyObject(), eq(EDGE_ID), eq(mat2_rule2));
    }

    private HeapConfiguration createInputGraph() {

        List<IndexSymbol> someIndex = new ArrayList<>();

        InternalHeapConfiguration hc = new InternalHeapConfiguration();
        Type type = sceneObject.scene().getType("type");
        Nonterminal bnt = sceneObject.scene().createNonterminal(UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES);
        Nonterminal nt = new IndexedNonterminalImpl(bnt, someIndex);

        TIntArrayList nodes = new TIntArrayList();

        return hc.builder()
                .addNodes(type, 2, nodes)
                .addNonterminalEdge(nt, nodes)
                .build();
    }

    private List<IndexSymbol> createEmptyMaterialization() {

        return new ArrayList<>();
    }

    private List<IndexSymbol> createNonEmptyMaterialization() {

        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        return SingleElementUtil.createList(bottom);
    }

    private HeapConfiguration createSimpleRule() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = sceneObject.scene().getType("type");
        SelectorLabel sel = sceneObject.scene().getSelectorLabel("someSelectorLabel");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();
    }

    private HeapConfiguration createBigRule() {

        List<IndexSymbol> someIndex = new ArrayList<>();

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = sceneObject.scene().getType("type");
        Nonterminal bnt = sceneObject.scene().createNonterminal(UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES);
        Nonterminal nt = new IndexedNonterminalImpl(bnt, someIndex);
        SelectorLabel sel = sceneObject.scene().getSelectorLabel("someSelectorLabel");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addSelector(nodes.get(1), sel, nodes.get(2))
                .build();
    }

}
