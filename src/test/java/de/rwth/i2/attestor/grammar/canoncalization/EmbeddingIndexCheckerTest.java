package de.rwth.i2.attestor.grammar.canoncalization;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.CannotMatchException;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingIndexChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexEmbeddingResult;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.EmbeddingChecker;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.BalancedTreeGrammar;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.*;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EmbeddingIndexCheckerTest {

    EmbeddingIndexChecker checker;
    SceneObject sceneObject;
    BalancedTreeGrammar treeGrammar;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        treeGrammar = new BalancedTreeGrammar(sceneObject);
        IndexMatcher stackMatcher = new IndexMatcher(new DefaultIndexMaterialization());
        IndexMaterializationStrategy stackMaterializer = new IndexMaterializationStrategy();
        checker = new EmbeddingIndexChecker(stackMatcher, stackMaterializer);
    }

    /**
     * This test uses graphs without any nonterminals as inputs,
     * i.e. it doesn't test any logic.
     *
     * @throws CannotMatchException unexpected (means the indexEmbeddingChecker has found a false negative)
     */
    @Test
    public void testSimple() throws CannotMatchException {

        HeapConfiguration toAbstract = getSimpleInput();
        HeapConfiguration pattern = getSimpleInput();
        Nonterminal lhs = getInstantiableNonterminal();
        Matching embedding = new EmbeddingChecker(pattern, toAbstract).getMatching();

        IndexEmbeddingResult res = checker.getIndexEmbeddingResult(toAbstract, embedding, lhs);

        assertEquals(getSimpleInput(), res.getMaterializedToAbstract());
        assertEquals(getInstantiableNonterminal(), res.getInstantiatedLhs());
    }

    /**
     * This tests verifies that the graphs are not modified, if the stacks match directly
     *
     * @throws CannotMatchException unexpected (means the indexEmbeddingChecker has found a false negative)
     */
    @Test
    public void testWithIdenticalStacks() throws CannotMatchException {

        List<IndexSymbol> concreteStack = getConcreteIndex();
        HeapConfiguration toAbstract = getInputWithIndex(concreteStack);
        HeapConfiguration pattern = getInputWithIndex(concreteStack);
        Nonterminal lhs = getMatchingNonterminalWithIndex(concreteStack);
        Matching embedding = new EmbeddingChecker(pattern, toAbstract).getMatching();

        IndexEmbeddingResult res = checker.getIndexEmbeddingResult(toAbstract, embedding, lhs);

        assertEquals(getInputWithIndex(concreteStack), res.getMaterializedToAbstract());
        assertEquals(getMatchingNonterminalWithIndex(concreteStack), res.getInstantiatedLhs());

    }

    /**
     * This test verifies that materialization is applied correctly in a simple case
     * (No instantiation, no different abstract symbols)
     *
     * @throws CannotMatchException unexpected (means the indexEmbeddingChecker has found a false negative)
     */
    @Test
    public void testOnlyMaterialization() throws CannotMatchException {

        List<IndexSymbol> somePrefix = getIndexPrefix();
        List<IndexSymbol> otherPrefix = getOtherIndexPrefix();

        List<IndexSymbol> toMatch = makeAbstract(somePrefix);
        List<IndexSymbol> reference = makeAbstract(otherPrefix);
        HeapConfiguration toAbstract = getInputWithIndices(toMatch, reference);

        List<IndexSymbol> concreteStack = makeConcrete(somePrefix);
        HeapConfiguration pattern = getInputWithIndex(concreteStack);
        Nonterminal lhs = getMatchingNonterminalWithIndex(concreteStack);
        Matching embedding = new EmbeddingChecker(pattern, toAbstract).getMatching();

        IndexEmbeddingResult res = checker.getIndexEmbeddingResult(toAbstract, embedding, lhs);

        assertEquals(getInputWithIndices(makeConcrete(somePrefix), makeConcrete(otherPrefix)),
                res.getMaterializedToAbstract());
        assertEquals(getMatchingNonterminalWithIndex(concreteStack), res.getInstantiatedLhs());
    }

    /**
     * still requires no instantiation, but uses embeds two nonterminals
     * which have different abstract symbols (ensures that they are materialized
     * independently)
     *
     * @throws CannotMatchException unexpected (means the indexEmbeddingChecker has found a false negative)
     */
    @Test
    public void testMaterializationWithDifferentAbstractSymbols() throws CannotMatchException {

        List<IndexSymbol> somePrefix = getIndexPrefix();
        List<IndexSymbol> otherPrefix = getOtherIndexPrefix();

        List<IndexSymbol> toMatch1 = makeAbstract(somePrefix);
        List<IndexSymbol> reference1 = makeAbstract(otherPrefix);

        List<IndexSymbol> toMatch2 = makeOtherAbstract(somePrefix);
        List<IndexSymbol> reference2 = makeOtherAbstract(otherPrefix);
        HeapConfiguration toAbstract = getInputWithIndices(toMatch1, toMatch2,
                reference1, reference2);

        List<IndexSymbol> concreteStack1 = makeConcrete(somePrefix);
        List<IndexSymbol> concreteStack2 = makeOtherConcrete(somePrefix);
        HeapConfiguration pattern = getPatternWithIndices(concreteStack1, concreteStack2);
        Nonterminal lhs = getMatchingNonterminalWithIndex(concreteStack1);
        Matching embedding = new EmbeddingChecker(pattern, toAbstract).getMatching();

        IndexEmbeddingResult res = checker.getIndexEmbeddingResult(toAbstract, embedding, lhs);

        assertEquals(getInputWithIndices(makeConcrete(somePrefix), makeOtherConcrete(somePrefix),
                makeConcrete(otherPrefix), makeOtherConcrete(otherPrefix)),
                res.getMaterializedToAbstract());
        assertEquals(getMatchingNonterminalWithIndex(concreteStack1), res.getInstantiatedLhs());
    }

    /**
     * Input: X -- s()<br>
     * X -- ss()
     * <p>
     * &#8594; should not be able to match
     */
    @Test
    public void testIncopatibleMaterialization() {

        List<IndexSymbol> somePrefix = getIndexPrefix();
        List<IndexSymbol> longerPrefix = getLongerIndexPrefix(somePrefix);

        List<IndexSymbol> toMatch1 = makeAbstract(getEmptyIndex());
        List<IndexSymbol> toMatch2 = makeAbstract(getEmptyIndex());
        List<IndexSymbol> reference1 = makeAbstract(getEmptyIndex());
        List<IndexSymbol> reference2 = makeOtherAbstract(getEmptyIndex());
        HeapConfiguration toAbstract = getInputWithIndices(toMatch1, toMatch2,
                reference1, reference2);

        List<IndexSymbol> instantiable1 = makeInstantiable(somePrefix);
        List<IndexSymbol> instantiable2 = makeInstantiable(longerPrefix);
        HeapConfiguration pattern = getPatternWithIndices(instantiable1, instantiable2);
        Nonterminal lhs = getMatchingNonterminalWithIndex(instantiable1);
        Matching embedding = new EmbeddingChecker(pattern, toAbstract).getMatching();

        try {
            checker.getIndexEmbeddingResult(toAbstract, embedding, lhs);
            fail("Expected CannotMatchException");
        } catch (CannotMatchException e) {
            //expected
        }
    }

    @Test
    public void testInstantiation() throws CannotMatchException {

        List<IndexSymbol> somePrefix = getIndexPrefix();

        List<IndexSymbol> toMatch = makeConcrete(somePrefix);
        HeapConfiguration toAbstract = getInputWithIndex(toMatch);

        List<IndexSymbol> matching = makeInstantiable(somePrefix);
        HeapConfiguration pattern = getInputWithIndex(matching);
        Nonterminal lhs = getReferenceNonterminalWithIndex(makeInstantiable(getEmptyIndex()));
        Matching embedding = new EmbeddingChecker(pattern, toAbstract).getMatching();

        IndexEmbeddingResult res = checker.getIndexEmbeddingResult(toAbstract, embedding, lhs);

        assertEquals(getInputWithIndex(toMatch), res.getMaterializedToAbstract());
        assertEquals(getReferenceNonterminalWithIndex(makeConcrete(getEmptyIndex())),
                res.getInstantiatedLhs());
    }

    @Test
    public void testIncompatibleInstantiation() {

        List<IndexSymbol> somePrefix = getIndexPrefix();

        List<IndexSymbol> toMatch1 = makeConcrete(somePrefix);
        List<IndexSymbol> toMatch2 = makeOtherConcrete(somePrefix);
        List<IndexSymbol> reference = makeAbstract(somePrefix);
        HeapConfiguration toAbstract = getInputWithIndices(toMatch1, toMatch2, reference, reference);

        List<IndexSymbol> matching = makeInstantiable(somePrefix);
        HeapConfiguration pattern = getPatternWithIndices(matching, matching);
        Nonterminal lhs = getReferenceNonterminalWithIndex(makeInstantiable(getEmptyIndex()));

        Matching embedding = new EmbeddingChecker(pattern, toAbstract).getMatching();

        try {
            checker.getIndexEmbeddingResult(toAbstract, embedding, lhs);
            fail("Expected CannotMatchException");
        } catch (CannotMatchException e) {
            // expected
        }
    }

    @Test
    public void testTwoIdenticalInstantiations() throws CannotMatchException {

        List<IndexSymbol> somePrefix = getIndexPrefix();

        List<IndexSymbol> toMatch1 = makeConcrete(somePrefix);
        List<IndexSymbol> toMatch2 = makeConcrete(somePrefix);
        List<IndexSymbol> reference = makeAbstract(somePrefix);
        HeapConfiguration toAbstract = getInputWithIndices(toMatch1, toMatch2, reference, reference);

        List<IndexSymbol> matching = makeInstantiable(somePrefix);
        HeapConfiguration pattern = getPatternWithIndices(matching, matching);
        Nonterminal lhs = getReferenceNonterminalWithIndex(makeInstantiable(getEmptyIndex()));

        Matching embedding = new EmbeddingChecker(pattern, toAbstract).getMatching();


        IndexEmbeddingResult res = checker.getIndexEmbeddingResult(toAbstract, embedding, lhs);

        assertEquals(toAbstract,
                res.getMaterializedToAbstract());
        assertEquals(getReferenceNonterminalWithIndex(makeConcrete(getEmptyIndex())),
                res.getInstantiatedLhs());

    }

    @Test
    public void testMixedInstantiationAndMaterialization() throws CannotMatchException {

        List<IndexSymbol> somePrefix = getIndexPrefix();

        List<IndexSymbol> toMatch = makeAbstract(somePrefix);
        List<IndexSymbol> reference = toMatch;
        HeapConfiguration toAbstract = getInputWithIndices(toMatch, toMatch, reference, reference);

        List<IndexSymbol> matching1 = makeInstantiable(somePrefix);
        List<IndexSymbol> matching2 = makeConcrete(somePrefix);
        HeapConfiguration pattern = getPatternWithIndices(matching1, matching2);
        Nonterminal lhs = getReferenceNonterminalWithIndex(makeInstantiable(getEmptyIndex()));

        Matching embedding = new EmbeddingChecker(pattern, toAbstract).getMatching();

        IndexEmbeddingResult res = checker.getIndexEmbeddingResult(toAbstract, embedding, lhs);

        assertEquals(getInputWithIndices(matching2, matching2, matching2, matching2),
                res.getMaterializedToAbstract());
        assertEquals(getReferenceNonterminalWithIndex(makeConcrete(getEmptyIndex())),
                res.getInstantiatedLhs());
    }


    private ArrayList<IndexSymbol> getEmptyIndex() {

        return new ArrayList<>();
    }

    private List<IndexSymbol> getIndexPrefix() {

        IndexSymbol s = DefaultIndexMaterialization.SYMBOL_s;
        ArrayList<IndexSymbol> prefix = getEmptyIndex();
        prefix.add(s);
        prefix.add(s);
        return prefix;
    }

    private List<IndexSymbol> getOtherIndexPrefix() {

        IndexSymbol s = DefaultIndexMaterialization.SYMBOL_s;
        IndexSymbol other = ConcreteIndexSymbol.getIndexSymbol("other", false);

        ArrayList<IndexSymbol> prefix = getEmptyIndex();
        prefix.add(s);
        prefix.add(other);
        return prefix;
    }

    private List<IndexSymbol> getLongerIndexPrefix(List<IndexSymbol> prefix) {

        IndexSymbol s = DefaultIndexMaterialization.SYMBOL_s;
        return addSymbol(prefix, s);
    }

    private List<IndexSymbol> makeConcrete(List<IndexSymbol> prefix) {

        IndexSymbol bottom = DefaultIndexMaterialization.SYMBOL_Z;
        return addSymbol(prefix, bottom);
    }

    private List<IndexSymbol> makeOtherConcrete(List<IndexSymbol> prefix) {

        IndexSymbol bottom = DefaultIndexMaterialization.SYMBOL_C;
        return addSymbol(prefix, bottom);
    }

    private List<IndexSymbol> makeAbstract(List<IndexSymbol> prefix) {

        AbstractIndexSymbol abs = DefaultIndexMaterialization.SYMBOL_X;
        return addSymbol(prefix, abs);
    }

    private List<IndexSymbol> makeInstantiable(List<IndexSymbol> prefix) {

        IndexSymbol var = IndexVariable.getIndexVariable();
        return addSymbol(prefix, var);
    }

    private List<IndexSymbol> makeOtherAbstract(List<IndexSymbol> prefix) {

        AbstractIndexSymbol abs = DefaultIndexMaterialization.SYMBOL_Y;
        return addSymbol(prefix, abs);
    }

    private List<IndexSymbol> addSymbol(List<IndexSymbol> prefix, IndexSymbol abs) {

        ArrayList<IndexSymbol> index = new ArrayList<>(prefix);
        index.add(abs);
        return index;
    }

    private List<IndexSymbol> getConcreteIndex() {

        List<IndexSymbol> index = getIndexPrefix();
        return makeConcrete(index);
    }

    private Nonterminal getInstantiableNonterminal() {

        List<IndexSymbol> index = getIndexWithIndexVariable();
        return getMatchingNonterminalWithIndex(index);
    }

    private Nonterminal getMatchingNonterminalWithIndex(List<IndexSymbol> index) {

        String label = "matching_EmbeddingIndexChecker";
        int rank = 2;
        boolean[] isReductionTentacle = new boolean[rank];
        Nonterminal bnt = sceneObject.scene().createNonterminal(label, rank, isReductionTentacle);
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);
        return nt;
    }

    private Nonterminal getOtherMatchingNonterminalWithIndex(List<IndexSymbol> index) {

        String label = "matching2_EmbeddingIndexChecker";
        int rank = 2;
        boolean[] isReductionTentacle = new boolean[rank];
        Nonterminal bnt = sceneObject.scene().createNonterminal(label, rank, isReductionTentacle);
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);
        return nt;
    }

    private Nonterminal getReferenceNonterminalWithIndex(List<IndexSymbol> index) {

        String label = "reference_EmbeddingIndexChecker";
        int rank = 2;
        boolean[] isReductionTentacle = new boolean[rank];
        Nonterminal bnt = sceneObject.scene().createNonterminal(label, rank, isReductionTentacle);
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);
        return nt;
    }

    private List<IndexSymbol> getIndexWithIndexVariable() {

        List<IndexSymbol> index = getEmptyIndex();
        index.add(IndexVariable.getIndexVariable());
        return index;
    }

    private HeapConfiguration getSimpleInput() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = treeGrammar.TYPE;
        SelectorLabel label = sceneObject.scene().getSelectorLabel("label");


        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .addSelector(nodes.get(0), label, nodes.get(1))
                .build();
    }

    private HeapConfiguration getInputWithIndex(List<IndexSymbol> index) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = treeGrammar.TYPE;
        SelectorLabel label = sceneObject.scene().getSelectorLabel("label");

        Nonterminal nt = getMatchingNonterminalWithIndex(index);


        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), label, nodes.get(1))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .build();
    }

    private HeapConfiguration getInputWithIndices(List<IndexSymbol> toMatch, List<IndexSymbol> reference) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = treeGrammar.TYPE;
        SelectorLabel label = sceneObject.scene().getSelectorLabel("label");

        Nonterminal matchingNt = getMatchingNonterminalWithIndex(toMatch);
        Nonterminal referenceNt = getReferenceNonterminalWithIndex(reference);


        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 4, nodes)
                .addSelector(nodes.get(0), label, nodes.get(1))
                .addNonterminalEdge(matchingNt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(referenceNt)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .build();
    }


    private HeapConfiguration getInputWithIndices(
            List<IndexSymbol> toMatch1, List<IndexSymbol> toMatch2,
            List<IndexSymbol> reference1, List<IndexSymbol> reference2) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = treeGrammar.TYPE;
        SelectorLabel label = sceneObject.scene().getSelectorLabel("label");

        Nonterminal matchingNt1 = getMatchingNonterminalWithIndex(toMatch1);
        Nonterminal referenceNt1 = getReferenceNonterminalWithIndex(reference1);

        Nonterminal matchingNt2 = getOtherMatchingNonterminalWithIndex(toMatch2);
        Nonterminal referenceNt2 = getReferenceNonterminalWithIndex(reference2);


        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 4, nodes)
                .addSelector(nodes.get(0), label, nodes.get(1))
                .addNonterminalEdge(matchingNt1)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(matchingNt2)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(referenceNt1)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .addNonterminalEdge(referenceNt2)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .build();
    }

    private HeapConfiguration getPatternWithIndices(
            List<IndexSymbol> index1, List<IndexSymbol> index2) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = treeGrammar.TYPE;
        SelectorLabel label = sceneObject.scene().getSelectorLabel("label");

        Nonterminal matchingNt1 = getMatchingNonterminalWithIndex(index1);

        Nonterminal matchingNt2 = getOtherMatchingNonterminalWithIndex(index2);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), label, nodes.get(1))
                .addNonterminalEdge(matchingNt1)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(matchingNt2)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .build();
    }


}
