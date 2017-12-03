package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.materialization.communication.CannotMaterializeException;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IndexMaterializationStrategyTest {

    private static final String UNIQUE_NT_LABEL = "IndexMaterializationStrategyTest";
    private static final int RANK = 3;
    private static final boolean[] REDUCTION_TENTACLES = new boolean[]{true, false, true};

    private SceneObject sceneObject;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
    }

    @Test
    public void testOn_Materializable_EmptyIndex_EmptyMaterialization() throws CannotMaterializeException {

        IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

        AbstractIndexSymbol symbolToMaterialize = someAbstractSymbol();
        HeapConfiguration inputGraph = unmaterializedGraphWithEmptyIndex(symbolToMaterialize);
        List<IndexSymbol> inputMaterializationPostfix = emptyMaterialization();
        HeapConfiguration expected = materializedGraph_EmptyIndex_EmptyMaterialization(symbolToMaterialize);
        HeapConfiguration actual =
                indexMaterializationStrategy.getMaterializedCloneWith(inputGraph, symbolToMaterialize, inputMaterializationPostfix);

        assertEquals("materialization not as expected", expected, actual);
        assertEquals("input graph has changed", unmaterializedGraphWithEmptyIndex(symbolToMaterialize),
                inputGraph);
    }

    @Test
    public void testOn_Materializable_EmptyIndex_AbstractMaterialization() throws CannotMaterializeException {

        IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

        AbstractIndexSymbol symbolToMaterialize = someAbstractSymbol();

        HeapConfiguration inputGraph = unmaterializedGraphWithEmptyIndex(symbolToMaterialize);
        List<IndexSymbol> inputMaterializationPostfix = abstractMaterialization();
        HeapConfiguration expected = materializedGraph_EmptyIndex_AbstractMaterialization();
        HeapConfiguration actual =
                indexMaterializationStrategy.getMaterializedCloneWith(inputGraph, symbolToMaterialize, inputMaterializationPostfix);

        assertEquals("materialization not as expected", expected, actual);
        assertEquals("input graph has changed", unmaterializedGraphWithEmptyIndex(symbolToMaterialize), inputGraph);
    }

    @Test
    public void testOn_Materializable_EmptyIndex_ConcreteMaterialization() throws CannotMaterializeException {

        IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

        AbstractIndexSymbol symbolToMaterialize = otherAbstractSymbol();

        HeapConfiguration inputGraph = unmaterializedGraphWithEmptyIndex(symbolToMaterialize);
        List<IndexSymbol> inputMaterializationPostfix = concreteMaterialization();
        HeapConfiguration expected = materializedGraph_EmptyIndex_concreteMaterialization();
        HeapConfiguration actual =
                indexMaterializationStrategy.getMaterializedCloneWith(inputGraph,
                        symbolToMaterialize, inputMaterializationPostfix);

        assertEquals("materialization not as expected", expected, actual);
        assertEquals("input graph has changed", unmaterializedGraphWithEmptyIndex(symbolToMaterialize),
                inputGraph);
    }

    @Test
    public void testOn_Materializable_NonEmptyIndex_ConcreteMaterialization() throws CannotMaterializeException {

        IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

        AbstractIndexSymbol symbolToMaterialize = someAbstractSymbol();
        HeapConfiguration inputGraph = unmaterializedGraph_NonEmptyIndex(symbolToMaterialize);
        List<IndexSymbol> inputMaterializationPostfix = concreteMaterialization();
        HeapConfiguration expected = materializedGraph_NonEmptyIndex_concreteMaterialization();
        HeapConfiguration actual =
                indexMaterializationStrategy.getMaterializedCloneWith(inputGraph, symbolToMaterialize, inputMaterializationPostfix);

        assertEquals("materialization not as expected", expected, actual);
        assertEquals("input graph has changed", unmaterializedGraph_NonEmptyIndex(symbolToMaterialize),
                inputGraph);
    }

    /**
     * We assume that there might be independent indexs (i.e. using different abstract symbols).
     * The index materializer should only materialize those with the given symbol.
     * Any other indexs (including concrete ones should simply be ignored)
     *
     * @throws CannotMaterializeException is not expected and thus indicates an error.
     */
    @Test
    public void test_ConcreteIndex_NonEmptyMaterialiation_ExpectNoException() throws CannotMaterializeException {

        IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

        HeapConfiguration inputGraph = unmaterializedGraph_ConcreteIndex();
        List<IndexSymbol> inputMaterializationPostfix = abstractMaterialization();

        indexMaterializationStrategy.getMaterializedCloneWith(inputGraph, someAbstractSymbol(), inputMaterializationPostfix);

        assertEquals("input graph has changed", unmaterializedGraph_ConcreteIndex(), inputGraph);
    }

    @Test
    public void testOn_ConcreteIndex_EmptyMaterialization() throws CannotMaterializeException {

        IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

        HeapConfiguration inputGraph = unmaterializedGraph_ConcreteIndex();
        List<IndexSymbol> inputMaterializationPostfix = emptyMaterialization();
        HeapConfiguration expected = materializedGraph_ConcreteIndex_emptyMaterialization();
        HeapConfiguration actual =
                indexMaterializationStrategy.getMaterializedCloneWith(inputGraph, null, inputMaterializationPostfix);

        assertEquals("materialization not as expected", expected, actual);
        assertEquals("input graph has changed", unmaterializedGraph_ConcreteIndex(), inputGraph);
    }

    @Test
    public void testOn_GraphWithTwoNonterminals_canMaterialize() throws CannotMaterializeException {

        IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

        AbstractIndexSymbol symbolToMaterialize = otherAbstractSymbol();
        HeapConfiguration inputGraph = unmaterializedGraph_TwoNonterminals_canMaterialize(symbolToMaterialize);
        List<IndexSymbol> inputMaterializationPostfix = abstractMaterialization();
        HeapConfiguration expected = materializedGraph_TwoNonterminals_abstractMaterialization();
        HeapConfiguration actual =
                indexMaterializationStrategy.getMaterializedCloneWith(inputGraph, symbolToMaterialize, inputMaterializationPostfix);

        assertEquals("materialization not as expected", expected, actual);
        assertEquals("input graph has changed",
                unmaterializedGraph_TwoNonterminals_canMaterialize(symbolToMaterialize),
                inputGraph);
    }

    @Test
    public void testOn_GraphWithDefaultNonterminal() throws CannotMaterializeException {

        IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

        AbstractIndexSymbol symbolToMaterialize = someAbstractSymbol();
        HeapConfiguration inputGraph = unmaterializedGraph_TwoNonterminals_oneDefault(symbolToMaterialize);
        List<IndexSymbol> inputMaterializationPostfix = abstractMaterialization();
        HeapConfiguration expected =
                materializedGraph_TwoNonterminals_oneDefault_abstractMaterialization();
        HeapConfiguration actual =
                indexMaterializationStrategy.getMaterializedCloneWith(inputGraph, symbolToMaterialize, inputMaterializationPostfix);

        assertEquals("materialization not as expected", expected, actual);
        assertEquals("input graph has changed",
                unmaterializedGraph_TwoNonterminals_oneDefault(symbolToMaterialize), inputGraph);
    }

    @Test
    public void testOn_GraphWithTwoNonterminals_differentAbstractSymbols() throws CannotMaterializeException {

        IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

        AbstractIndexSymbol symbolToMaterialize = someAbstractSymbol();
        HeapConfiguration inputGraph =
                inputTwoNonterminalsDifferentAbstractSymbols(symbolToMaterialize);
        List<IndexSymbol> inputMaterializationPostFix = concreteMaterialization();
        HeapConfiguration expected =
                expectedTwoNonterminalsDifferentAbstractSymbols(inputMaterializationPostFix);

        HeapConfiguration actual =
                indexMaterializationStrategy.getMaterializedCloneWith(inputGraph,
                        symbolToMaterialize,
                        inputMaterializationPostFix);

        assertEquals("materialization not as expected", expected, actual);
        assertEquals("input graph has changed",
                inputTwoNonterminalsDifferentAbstractSymbols(symbolToMaterialize), inputGraph);

    }

    private AbstractIndexSymbol someAbstractSymbol() {

        return AbstractIndexSymbol.get("X");
    }

    private AbstractIndexSymbol otherAbstractSymbol() {

        return AbstractIndexSymbol.get("Y");
    }

    private List<IndexSymbol> emptyMaterialization() {

        return new ArrayList<>();
    }

    private List<IndexSymbol> abstractMaterialization() {

        IndexSymbol someConcreteIndexSymbol = ConcreteIndexSymbol.getIndexSymbol("b", false);
        IndexSymbol someAbstractIndexSymbol = AbstractIndexSymbol.get("Y");

        List<IndexSymbol> abstractMaterialization = new ArrayList<>();
        abstractMaterialization.add(someConcreteIndexSymbol);
        abstractMaterialization.add(someAbstractIndexSymbol);
        abstractMaterialization.add(someAbstractIndexSymbol);
        return abstractMaterialization;
    }

    private List<IndexSymbol> concreteMaterialization() {

        IndexSymbol someConcreteIndexSymbol = ConcreteIndexSymbol.getIndexSymbol("b", false);
        IndexSymbol someBottomIndexSymbol = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        List<IndexSymbol> abstractMaterialization = new ArrayList<>();
        abstractMaterialization.add(someConcreteIndexSymbol);
        abstractMaterialization.add(someBottomIndexSymbol);
        return abstractMaterialization;
    }

    private List<IndexSymbol> emptyIndex() {

        return new ArrayList<>();
    }

    private List<IndexSymbol> nonEmptyIndex() {

        IndexSymbol someIndexSymbol = ConcreteIndexSymbol.getIndexSymbol("c", false);
        IndexSymbol otherIndexSymbol = ConcreteIndexSymbol.getIndexSymbol("b", false);

        List<IndexSymbol> nonEmptyIndex = new ArrayList<>();
        nonEmptyIndex.add(someIndexSymbol);
        nonEmptyIndex.add(otherIndexSymbol);
        nonEmptyIndex.add(someIndexSymbol);

        return nonEmptyIndex;
    }

    private List<IndexSymbol> concreteIndex() {

        IndexSymbol someIndexSymbol = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol someBottomSymbol = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        List<IndexSymbol> concreteIndex = new ArrayList<>();
        concreteIndex.add(someIndexSymbol);
        concreteIndex.add(someBottomSymbol);

        return concreteIndex;
    }


    private HeapConfiguration unmaterializedGraphWithEmptyIndex(AbstractIndexSymbol symbolToMaterialize) {

        List<IndexSymbol> materializableEmptyIndex = emptyIndex();
        materializableEmptyIndex.add(symbolToMaterialize);
        return graphWithOneNonterminalAndIndex(materializableEmptyIndex);
    }

    private HeapConfiguration materializedGraph_EmptyIndex_EmptyMaterialization(AbstractIndexSymbol symbolToMaterialize) {

        return unmaterializedGraphWithEmptyIndex(symbolToMaterialize);
    }

    private HeapConfiguration materializedGraph_EmptyIndex_AbstractMaterialization() {

        List<IndexSymbol> materializedEmptyIndex = emptyIndex();
        materializedEmptyIndex.addAll(abstractMaterialization());
        return graphWithOneNonterminalAndIndex(materializedEmptyIndex);
    }

    private HeapConfiguration materializedGraph_EmptyIndex_concreteMaterialization() {

        List<IndexSymbol> materializedEmptyIndex = emptyIndex();
        materializedEmptyIndex.addAll(concreteMaterialization());
        return graphWithOneNonterminalAndIndex(materializedEmptyIndex);
    }


    private HeapConfiguration unmaterializedGraph_NonEmptyIndex(AbstractIndexSymbol abstractIndexSymbol) {

        List<IndexSymbol> materializableNonEmptyIndex = nonEmptyIndex();
        materializableNonEmptyIndex.add(abstractIndexSymbol);
        return graphWithOneNonterminalAndIndex(materializableNonEmptyIndex);
    }

    private HeapConfiguration materializedGraph_NonEmptyIndex_concreteMaterialization() {

        List<IndexSymbol> materializedNonEmptyIndex = nonEmptyIndex();
        materializedNonEmptyIndex.addAll(concreteMaterialization());
        return graphWithOneNonterminalAndIndex(materializedNonEmptyIndex);
    }


    private HeapConfiguration unmaterializedGraph_ConcreteIndex() {

        return graphWithOneNonterminalAndIndex(concreteIndex());
    }

    private HeapConfiguration materializedGraph_ConcreteIndex_emptyMaterialization() {

        return unmaterializedGraph_ConcreteIndex();
    }


    private HeapConfiguration unmaterializedGraph_TwoNonterminals_canMaterialize(
            AbstractIndexSymbol abstractIndexSymbol) {

        List<IndexSymbol> index1 = emptyIndex();
        index1.add(abstractIndexSymbol);
        List<IndexSymbol> index2 = nonEmptyIndex();
        index2.add(abstractIndexSymbol);

        return graphWithTwoNonterminalsWithIndexs(index1, index2);
    }


    private HeapConfiguration materializedGraph_TwoNonterminals_abstractMaterialization() {

        List<IndexSymbol> materializedIndex1 = emptyIndex();
        materializedIndex1.addAll(abstractMaterialization());
        List<IndexSymbol> materializedIndex2 = nonEmptyIndex();
        materializedIndex2.addAll(abstractMaterialization());

        return graphWithTwoNonterminalsWithIndexs(materializedIndex1, materializedIndex2);
    }

    private HeapConfiguration unmaterializedGraph_TwoNonterminals_oneDefault(
            AbstractIndexSymbol abstractIndexSymbol) {

        List<IndexSymbol> indexForIndexedNonterminal = emptyIndex();
        indexForIndexedNonterminal.add(abstractIndexSymbol);

        return graphWithDefaultNonterminalAndIndexedWithIndex(indexForIndexedNonterminal);
    }


    private HeapConfiguration materializedGraph_TwoNonterminals_oneDefault_abstractMaterialization() {

        List<IndexSymbol> materializedIndexForIndexedNonterminal = emptyIndex();
        materializedIndexForIndexedNonterminal.addAll(abstractMaterialization());

        return graphWithDefaultNonterminalAndIndexedWithIndex(materializedIndexForIndexedNonterminal);
    }

    private HeapConfiguration inputTwoNonterminalsDifferentAbstractSymbols(AbstractIndexSymbol symbolToMaterialize) {

        List<IndexSymbol> indexToMaterialize = emptyIndex();
        indexToMaterialize.add(symbolToMaterialize);
        List<IndexSymbol> indexNotToMaterialize = emptyIndex();
        indexNotToMaterialize.add(otherAbstractSymbol());

        return graphWithTwoNonterminalsWithIndexs(indexToMaterialize, indexNotToMaterialize);
    }

    private HeapConfiguration expectedTwoNonterminalsDifferentAbstractSymbols(
            List<IndexSymbol> inputMaterializationPostFix) {

        List<IndexSymbol> indexToMaterialize = emptyIndex();
        indexToMaterialize.addAll(inputMaterializationPostFix);
        List<IndexSymbol> indexNotToMaterialize = emptyIndex();
        indexNotToMaterialize.add(otherAbstractSymbol());

        return graphWithTwoNonterminalsWithIndexs(indexToMaterialize, indexNotToMaterialize);
    }


    private HeapConfiguration graphWithOneNonterminalAndIndex(List<IndexSymbol> index) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = sceneObject.scene().getType("type");

        Nonterminal bnt = sceneObject.scene().createNonterminal(UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES);
        Nonterminal nt = new IndexedNonterminalImpl(bnt, index);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .addNonterminalEdge(nt)
                .addTentacle(0)
                .addTentacle(1)
                .addTentacle(1)
                .build()
                .build();

    }

    private HeapConfiguration graphWithTwoNonterminalsWithIndexs(List<IndexSymbol> index1,
                                                                 List<IndexSymbol> index2) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = sceneObject.scene().getType("type");

        Nonterminal bnt = sceneObject.scene().createNonterminal(UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES);
        Nonterminal nt1 = new IndexedNonterminalImpl(bnt, index1);
        Nonterminal nt2 = new IndexedNonterminalImpl(bnt, index2);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .addNonterminalEdge(nt1)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(nt2)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .build();
    }

    private HeapConfiguration graphWithDefaultNonterminalAndIndexedWithIndex(
            List<IndexSymbol> indexForIndexedNonterminal) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = sceneObject.scene().getType("type");

        Nonterminal bnt = sceneObject.scene().createNonterminal(UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES);
        Nonterminal nt = new IndexedNonterminalImpl(bnt, indexForIndexedNonterminal);
        Nonterminal defaultNt = sceneObject.scene().getNonterminal(UNIQUE_NT_LABEL);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .addNonterminalEdge(nt)
                .addTentacle(0)
                .addTentacle(1)
                .addTentacle(1)
                .build()
                .addNonterminalEdge(defaultNt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(1))
                .build()
                .build();
    }
}
