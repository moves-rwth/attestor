package de.rwth.i2.attestor.grammar.canoncalization.moduleTest;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.AbstractionOptions;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingIndexChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexedCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.IndexedState;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.programState.indexedState.index.IndexCanonizationStrategy;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexVariable;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GeneralCanonicalizationStrategy_Indexed_Confluent {

    private static final String NT_LABEL = "GeneralCanonicalizationStrategyIC";
    private static final int RANK = 2;
    private static final boolean[] isReductionTentacle = new boolean[RANK];
    private static final int sizeOfChain = 10;
    private final SceneObject sceneObject = new MockupSceneObject();
    private final Type TYPE = sceneObject.scene().getType("type");
    private final SelectorLabel SEL = sceneObject.scene().getSelectorLabel("sel");
    private IndexedCanonicalizationHelper matchingHandler;

    @Before
    public void init() {

        IndexCanonizationStrategy fakeIndexStrategy = new FakeIndexCanonicalizationStrategy();

        AbstractionOptions options = new AbstractionOptions()
                .setAdmissibleAbstraction(true)
                .setAdmissibleConstants(
                        sceneObject.scene().options().isAdmissibleConstantsEnabled()
                );

        EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(options);

        IndexMaterializationStrategy materializer = new IndexMaterializationStrategy();
        DefaultIndexMaterialization indexGrammar = new DefaultIndexMaterialization();
        IndexMatcher indexMatcher = new IndexMatcher(indexGrammar);
        EmbeddingIndexChecker indexChecker =
                new EmbeddingIndexChecker(indexMatcher,
                        materializer);

        matchingHandler = new IndexedCanonicalizationHelper(fakeIndexStrategy, checkerProvider, indexChecker);

    }

    @Test
    public void test() {

        List<IndexSymbol> lhsIndex1 = makeInstantiable(getIndexPrefix());
        Nonterminal lhs1 = getNonterminal(lhsIndex1);
        HeapConfiguration rhs1 = getPattern1();
        HeapConfiguration rhs2 = getPattern2();
        Grammar grammar = Grammar.builder().addRule(lhs1, rhs1)
                .addRule(lhs1, rhs2)
                .build();

        GeneralCanonicalizationStrategy canonizer
                = new GeneralCanonicalizationStrategy(grammar, matchingHandler);

        ProgramState inputState = new DefaultProgramState(getInputGraph());
        ProgramState res = inputState.shallowCopyWithUpdateHeap(canonizer.canonicalize(inputState.getHeap()));

        assertEquals(expectedSimpleAbstraction().getHeap(), res.getHeap());
    }


    private List<IndexSymbol> getEmptyIndex() {

        List<IndexSymbol> index = new ArrayList<>();
        return index;
    }

    private List<IndexSymbol> getIndexPrefix() {

        List<IndexSymbol> index = getEmptyIndex();
        index.add(DefaultIndexMaterialization.SYMBOL_s);
        return index;
    }

    private List<IndexSymbol> makeConcrete(List<IndexSymbol> index) {

        List<IndexSymbol> indexCopy = new ArrayList<>(index);
        indexCopy.add(DefaultIndexMaterialization.SYMBOL_Z);
        return indexCopy;
    }

    private List<IndexSymbol> makeInstantiable(List<IndexSymbol> index) {

        List<IndexSymbol> indexCopy = new ArrayList<>(index);
        indexCopy.add(IndexVariable.getIndexVariable());
        return indexCopy;
    }


    private Nonterminal getNonterminal(List<IndexSymbol> index) {

        Nonterminal bnt = sceneObject.scene().createNonterminal(NT_LABEL, RANK, isReductionTentacle);
        return new IndexedNonterminalImpl(bnt, index);
    }


    private HeapConfiguration getPattern1() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(TYPE, 3, nodes)
                .addNonterminalEdge(getNonterminal(makeInstantiable(getEmptyIndex())))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addSelector(nodes.get(1), SEL, nodes.get(2))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .build();
    }


    private HeapConfiguration getPattern2() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(TYPE, 3, nodes)
                .addSelector(nodes.get(2), SEL, nodes.get(1))
                .addNonterminalEdge(getNonterminal(makeInstantiable(getEmptyIndex())))
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(2))
                .build()
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .build();
    }


    private HeapConfiguration getInputGraph() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        HeapConfigurationBuilder builder = hc.builder().addNodes(TYPE, sizeOfChain + 1, nodes);
        for (int i = 1; i < sizeOfChain; i++) {
            builder.addSelector(nodes.get(i), SEL, nodes.get(i + 1));
        }
        builder.addNonterminalEdge(getNonterminal(makeConcrete(getEmptyIndex())))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build();

        return builder.build();
    }

    private ProgramState expectedSimpleAbstraction() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        List<IndexSymbol> expectedIndex = getExpectedIndex();

        TIntArrayList nodes = new TIntArrayList();
        hc = hc.builder().addNodes(TYPE, 2, nodes)
                .addNonterminalEdge(getNonterminal(expectedIndex))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .build();

        return new IndexedState(hc);
    }

    private List<IndexSymbol> getExpectedIndex() {

        List<IndexSymbol> index = getEmptyIndex();
        for (int i = 0; i < sizeOfChain - 1; i++) {
            index.add(DefaultIndexMaterialization.SYMBOL_s);
        }
        return makeConcrete(index);
    }


}
