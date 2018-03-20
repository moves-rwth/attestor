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
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
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

public class GeneralCanonicalizationStrategy_Indexed_Simple {

    private static final String NT_LABEL = "GeneralCanonicalizationStrategyIS";
    private static final int RANK = 2;
    private static final boolean[] isReductionTentacle = new boolean[RANK];
    private final SceneObject sceneObject = new MockupSceneObject();
    private final Type TYPE = sceneObject.scene().getType("type");
    private final SelectorLabel SEL = sceneObject.scene().getSelectorLabel("sel");

    private IndexedCanonicalizationHelper matchingHandler;

    @Before
    public void init() {

        sceneObject.scene().options().setIndexedModeEnabled(true);

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

        List<IndexSymbol> lhsIndex = makeInstantiable(getIndexPrefix());
        Nonterminal lhs = getNonterminal(lhsIndex);
        HeapConfiguration rhs = getPattern();
        Grammar grammar = Grammar.builder().addRule(lhs, rhs).build();

        GeneralCanonicalizationStrategy canonizer
                = new GeneralCanonicalizationStrategy(grammar, matchingHandler);

        ProgramState inputState = sceneObject.scene().createProgramState(getSimpleGraph());
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


    private HeapConfiguration getPattern() {

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


    private HeapConfiguration getSimpleGraph() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(TYPE, 3, nodes)
                .addNonterminalEdge(getNonterminal(makeConcrete(getEmptyIndex())))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addSelector(nodes.get(1), SEL, nodes.get(2))
                .build();
    }

    private ProgramState expectedSimpleAbstraction() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        hc = hc.builder().addNodes(TYPE, 2, nodes)
                .addNonterminalEdge(getNonterminal(makeConcrete(getIndexPrefix())))
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .build();

        return sceneObject.scene().createProgramState(hc);
    }


}
