package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.MaterializationAndRuleResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.MaterializationRuleManager;
import de.rwth.i2.attestor.grammar.testUtil.FakeIndexMatcher;
import de.rwth.i2.attestor.grammar.testUtil.FakeViolationPointResolver;
import de.rwth.i2.attestor.grammar.testUtil.FakeViolationPointResolverForDefault;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexVariable;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class IndexedMaterializationRuleManagerTest {

    public static final int RANK = 3;
    public static final String UNIQUE_NT_LABEL = "FakeViolationPointResolver";
    public static final boolean[] REDUCTION_TENTACLES = new boolean[]{false, false};
    IndexedNonterminal requestNonterminal;
    private SceneObject sceneObject;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        requestNonterminal = createRequestNonterminal();
    }


    @Test
    public void checkOnRhsWithoutNonterminals() {

        Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();

        List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
        hardCodedViolationPointResolverResult.add(uninstantiatedRhsWithoutNonterminal());

        List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
        expectedInstantiatedGraphs.add(instantiatedRhsWihtoutNonterminal());

        performCheckFor(expectedViolationPointResultLhs,
                hardCodedViolationPointResolverResult,
                expectedInstantiatedGraphs);
    }

    @Test
    public void checkInRhsWithConcreteIndex() {

        Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();

        List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
        hardCodedViolationPointResolverResult.add(uninstantiatedRhs_OneNonterminal_ConcreteIndex());

        List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
        expectedInstantiatedGraphs.add(instantiatedRhs_OneNonterminal_ConcreteIndex());

        performCheckFor(expectedViolationPointResultLhs,
                hardCodedViolationPointResolverResult,
                expectedInstantiatedGraphs);
    }

    @Test
    public void checkOnRhsWithInstantiableEmptyIndex() {

        Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();

        List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
        hardCodedViolationPointResolverResult.add(uninstantiatedRhs_OneNonterminal_EmptyIndex());

        List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
        expectedInstantiatedGraphs.add(instantiatedRhs_OneNonterminal_EmptyIndex());

        performCheckFor(expectedViolationPointResultLhs,
                hardCodedViolationPointResolverResult,
                expectedInstantiatedGraphs);
    }

    @Test
    public void checkOnRhsWithInstantiableNonEmptyIndex() {

        Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();

        List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
        hardCodedViolationPointResolverResult.add(uninstantiatedRhs_OneNonterminal_NonEmptyIndex());

        List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
        expectedInstantiatedGraphs.add(instantiatedRhs_OneNonterminal_NonEmptyIndex());

        performCheckFor(expectedViolationPointResultLhs,
                hardCodedViolationPointResolverResult,
                expectedInstantiatedGraphs);
    }

    @Test
    public void checkOnRhsWithTwoNonterminals() {

        Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();

        List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
        hardCodedViolationPointResolverResult.add(uninstantiatedRhs_TwoNonterminals());

        List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
        expectedInstantiatedGraphs.add(instantiatedRhs_TwoNonterminals());

        performCheckFor(expectedViolationPointResultLhs,
                hardCodedViolationPointResolverResult,
                expectedInstantiatedGraphs);
    }

    @Test
    public void checkOnMultipleRhs() {

        Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();

        List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
        List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();

        hardCodedViolationPointResolverResult.add(uninstantiatedRhsWithoutNonterminal());
        expectedInstantiatedGraphs.add(instantiatedRhsWihtoutNonterminal());
        hardCodedViolationPointResolverResult.add(uninstantiatedRhs_OneNonterminal_EmptyIndex());
        expectedInstantiatedGraphs.add(instantiatedRhs_OneNonterminal_EmptyIndex());
        hardCodedViolationPointResolverResult.add(uninstantiatedRhs_OneNonterminal_ConcreteIndex());
        expectedInstantiatedGraphs.add(instantiatedRhs_OneNonterminal_ConcreteIndex());

        performCheckFor(expectedViolationPointResultLhs,
                hardCodedViolationPointResolverResult,
                expectedInstantiatedGraphs);
    }

    @Test
    public void testDefaultCaseOnIndexedManager() {

        FakeViolationPointResolverForDefault grammarLogik = new FakeViolationPointResolverForDefault(sceneObject);
        MaterializationRuleManager ruleManager = new IndexedMaterializationRuleManager(grammarLogik, null);

        GrammarResponse actualResponse;
        try {
            Nonterminal nonterminal = grammarLogik.DEFAULT_NONTERMINAL;
            int tentacleForNext = 0;
            String requestLabel = "some label";
            actualResponse = ruleManager.getRulesFor(nonterminal,
                    tentacleForNext,
                    requestLabel);

            assertTrue(actualResponse instanceof DefaultGrammarResponse);
            DefaultGrammarResponse defaultResponse = (DefaultGrammarResponse) actualResponse;
            assertTrue(defaultResponse.getApplicableRules()
                    .contains(grammarLogik.RHS_CREATING_NEXT));
            assertTrue(defaultResponse.getApplicableRules()
                    .contains(grammarLogik.RHS_CREATING_NEXT_PREV));

        } catch (UnexpectedNonterminalTypeException e) {
            fail("Unexpected exception");
        }
    }


    private void performCheckFor(Collection<Nonterminal> expectedViolationPointResultLhs,
                                 Collection<HeapConfiguration> hardCodedViolationPointResoverResult,
                                 Collection<HeapConfiguration> expectedInstantiatedGraphs) {

        FakeViolationPointResolver fakeVioResolver = new FakeViolationPointResolver();
        fakeVioResolver.defineReturnedLhsForTest(expectedViolationPointResultLhs);
        fakeVioResolver.defineRhsForAllNonterminals(hardCodedViolationPointResoverResult);


        IndexMatcher fakeIndexMatcher = new FakeIndexMatcher();

        IndexedMaterializationRuleManager ruleManager =
                new IndexedMaterializationRuleManager(fakeVioResolver, fakeIndexMatcher);

        GrammarResponse actualGrammarResponse;
        try {
            actualGrammarResponse = ruleManager.getRulesFor(requestNonterminal, 0, "some_label");


            assertTrue(actualGrammarResponse instanceof MaterializationAndRuleResponse);
            MaterializationAndRuleResponse indexedResponse = (MaterializationAndRuleResponse) actualGrammarResponse;

            final List<IndexSymbol> expectedMaterialization = FakeIndexMatcher.MATERIALIZATION;
            assertTrue(indexedResponse.getPossibleMaterializations().contains(expectedMaterialization));
            for (HeapConfiguration expectedInstantiatedRhs : expectedInstantiatedGraphs) {
                assertTrue(indexedResponse.getRulesForMaterialization(expectedMaterialization)
                        .contains(expectedInstantiatedRhs));
            }

        } catch (UnexpectedNonterminalTypeException e) {
            fail("Unexpected Exception");
        }
    }


    private IndexedNonterminal createRequestNonterminal() {

        final ArrayList<IndexSymbol> index = new ArrayList<>();

        final IndexSymbol someAbstractIndexSymbol = AbstractIndexSymbol.get("SomeAbstractIndexSymbol");
        index.add(someAbstractIndexSymbol);
        Nonterminal bnt = sceneObject.scene().createNonterminal(UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES);
        return new IndexedNonterminalImpl(bnt, index);
    }

    private Collection<Nonterminal> createExampleNts() {

        IndexSymbol a = ConcreteIndexSymbol.getIndexSymbol("a", false);
        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol bottom1 = ConcreteIndexSymbol.getIndexSymbol("Z", true);
        IndexSymbol var = IndexVariable.getIndexVariable();

        List<IndexSymbol> index1 = new ArrayList<>();
        index1.add(a);
        index1.add(s);
        index1.add(bottom1);
        Nonterminal bnt = sceneObject.scene().createNonterminal(UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES);
        IndexedNonterminal nt1 = new IndexedNonterminalImpl(bnt, index1);

        List<IndexSymbol> index2 = new ArrayList<>();
        index2.add(s);
        index2.add(var);
        IndexedNonterminal nt2 = new IndexedNonterminalImpl(bnt, index2);

        Set<Nonterminal> result = new LinkedHashSet<>();
        result.add(nt1);
        result.add(nt2);

        return result;
    }

//##### No Nonterminal ####

    private HeapConfiguration uninstantiatedRhsWithoutNonterminal() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = sceneObject.scene().getType("type");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .build();
    }

    private HeapConfiguration instantiatedRhsWihtoutNonterminal() {

        return uninstantiatedRhsWithoutNonterminal();
    }

//##### One Nonterminal #######

    private HeapConfiguration graphWithOneNonterminalWithIndex(List<IndexSymbol> index) {

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

    //----- Empty Instantiable Index ----------

    private List<IndexSymbol> emptyIndex() {

        return new ArrayList<>();
    }

    private HeapConfiguration uninstantiatedRhs_OneNonterminal_EmptyIndex() {

        List<IndexSymbol> uninstantiatedEmptyIndex = emptyIndex();
        uninstantiatedEmptyIndex.add(IndexVariable.getIndexVariable());
        return graphWithOneNonterminalWithIndex(uninstantiatedEmptyIndex);
    }

    private HeapConfiguration instantiatedRhs_OneNonterminal_EmptyIndex() {

        List<IndexSymbol> instantiatedEmptyIndex = emptyIndex();
        instantiatedEmptyIndex.addAll(FakeIndexMatcher.INSTANTIATION);
        return graphWithOneNonterminalWithIndex(instantiatedEmptyIndex);
    }

    //------ Non-Empty Instantiable Index ------

    private List<IndexSymbol> nonEmptyIndex() {

        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol a = ConcreteIndexSymbol.getIndexSymbol("a", false);

        List<IndexSymbol> index = new ArrayList<>();
        index.add(a);
        index.add(s);
        index.add(s);

        return index;
    }

    private HeapConfiguration uninstantiatedRhs_OneNonterminal_NonEmptyIndex() {

        List<IndexSymbol> uninstantiatedNonEmptyIndex = nonEmptyIndex();
        uninstantiatedNonEmptyIndex.add(IndexVariable.getIndexVariable());
        return graphWithOneNonterminalWithIndex(uninstantiatedNonEmptyIndex);
    }

    private HeapConfiguration instantiatedRhs_OneNonterminal_NonEmptyIndex() {

        List<IndexSymbol> instantiatedNonEmptyIndex = nonEmptyIndex();
        instantiatedNonEmptyIndex.addAll(FakeIndexMatcher.INSTANTIATION);
        return graphWithOneNonterminalWithIndex(instantiatedNonEmptyIndex);
    }

    //---------- Concrete Index --------------------------

    private List<IndexSymbol> concreteIndex() {

        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        List<IndexSymbol> index = new ArrayList<>();
        index.add(s);
        index.add(s);
        index.add(bottom);

        return index;
    }

    private HeapConfiguration uninstantiatedRhs_OneNonterminal_ConcreteIndex() {

        List<IndexSymbol> concreteIndex = concreteIndex();
        return graphWithOneNonterminalWithIndex(concreteIndex);
    }

    private HeapConfiguration instantiatedRhs_OneNonterminal_ConcreteIndex() {

        return uninstantiatedRhs_OneNonterminal_ConcreteIndex();
    }

    //=============== Two Nonterminals ======================

    private HeapConfiguration graphWithTwoNonterminalsWithIndices(List<IndexSymbol> index1,
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

    private HeapConfiguration uninstantiatedRhs_TwoNonterminals() {

        List<IndexSymbol> index1 = emptyIndex();
        index1.add(IndexVariable.getIndexVariable());
        List<IndexSymbol> index2 = nonEmptyIndex();
        index2.add(IndexVariable.getIndexVariable());
        return graphWithTwoNonterminalsWithIndices(index1, index2);
    }

    private HeapConfiguration instantiatedRhs_TwoNonterminals() {

        List<IndexSymbol> index1 = emptyIndex();
        index1.addAll(FakeIndexMatcher.INSTANTIATION);
        List<IndexSymbol> index2 = nonEmptyIndex();
        index2.addAll(FakeIndexMatcher.INSTANTIATION);
        return graphWithTwoNonterminalsWithIndices(index1, index2);
    }

}
