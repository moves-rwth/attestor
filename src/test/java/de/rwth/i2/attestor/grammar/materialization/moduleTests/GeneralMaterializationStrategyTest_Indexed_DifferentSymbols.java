package de.rwth.i2.attestor.grammar.materialization.moduleTests;


import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.strategies.GeneralMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.util.GraphMaterializer;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPointResolver;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;


public class GeneralMaterializationStrategyTest_Indexed_DifferentSymbols {

    private static final boolean[] REDUCTION_TENTACLEs = new boolean[]{false, false};
    private static final int RANK = 2;
    private static final String LABEL = "TestDifferentSymbols";
    private static final String VIOLATION_POINT_VARIABLE = "x";
    private static final String VIOLATION_POINT_SELECTOR = "next";
    MaterializationStrategy materializer;
    AbstractIndexSymbol oneAbstractSymbol;
    AbstractIndexSymbol otherAbstractSymbol;
    SceneObject sceneObject;


    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        sceneObject.scene().options().setIndexedModeEnabled(true);

        oneAbstractSymbol = DefaultIndexMaterialization.SYMBOL_X;
        otherAbstractSymbol = DefaultIndexMaterialization.SYMBOL_Y;
        Grammar grammar = buildSimpleGrammarWithTwoIndicesGrammars();
        ViolationPointResolver vioResolver = new ViolationPointResolver(grammar);
        IndexMatcher indexMatcher = new IndexMatcher(new DefaultIndexMaterialization());
        IndexedMaterializationRuleManager grammarManager =
                new IndexedMaterializationRuleManager(vioResolver, indexMatcher);

        IndexedGrammarResponseApplier ruleApplier =
                new IndexedGrammarResponseApplier(new IndexMaterializationStrategy(), new GraphMaterializer());

        materializer = new GeneralMaterializationStrategy(grammarManager, ruleApplier);
    }


    @Test
    public void test() {

        ViolationPoints inputViolationPoint = new ViolationPoints();
        inputViolationPoint.add(VIOLATION_POINT_VARIABLE, VIOLATION_POINT_SELECTOR);


        HeapConfiguration inputGraph = getInput();
        ProgramState inputState = sceneObject.scene().createProgramState(inputGraph).prepareHeap();

        HeapConfiguration expectedGraph = getExpected();
        ProgramState expectedState = sceneObject.scene().createProgramState(expectedGraph).prepareHeap();

        Collection<HeapConfiguration> result = materializer.materialize(inputState.getHeap(), inputViolationPoint);

        assertThat(result, contains(expectedState.getHeap()));

    }


    private HeapConfiguration getInput() {

        Type someType = sceneObject.scene().getType("type");

        List<IndexSymbol> indexWithOneIndexSymbol = SingleElementUtil.createList(oneAbstractSymbol);
        Nonterminal toReplace = getNonterminalWithIndex(indexWithOneIndexSymbol);
        Nonterminal controlWithSameIndex = getNonterminalWithIndex(indexWithOneIndexSymbol);
        Nonterminal controlWithOtherIndex = getNonterminalWithIndex(SingleElementUtil.createList(otherAbstractSymbol));

        TIntArrayList nodes = new TIntArrayList();
        return new InternalHeapConfiguration().builder()
                .addNodes(someType, 4, nodes)
                .addVariableEdge(VIOLATION_POINT_VARIABLE, nodes.get(0))
                .addNonterminalEdge(toReplace)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(controlWithSameIndex)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .addNonterminalEdge(controlWithOtherIndex)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .build();
    }

    private HeapConfiguration getExpected() {

        Type someType = sceneObject.scene().getType("type");
        SelectorLabel selectorLabel = sceneObject.scene().getSelectorLabel(VIOLATION_POINT_SELECTOR);

        List<IndexSymbol> materializedIndex = indexForLhs();
        materializedIndex.add(oneAbstractSymbol);
        Nonterminal controlWithSameIndex = getNonterminalWithIndex(materializedIndex);
        Nonterminal controlWithOtherIndex = getNonterminalWithIndex(SingleElementUtil.createList(otherAbstractSymbol));

        TIntArrayList nodes = new TIntArrayList();
        return new InternalHeapConfiguration().builder()
                .addNodes(someType, 4, nodes)
                .addVariableEdge(VIOLATION_POINT_VARIABLE, nodes.get(0))
                .addSelector(nodes.get(0), selectorLabel, nodes.get(1))
                .addNonterminalEdge(controlWithSameIndex)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .addNonterminalEdge(controlWithOtherIndex)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .build();
    }


    private Grammar buildSimpleGrammarWithTwoIndicesGrammars() {

        return Grammar.builder()
                .addRule(getNonterminalWithIndexVariable(), someRhs())
                .build();
    }


    private List<IndexSymbol> indexForLhs() {

        List<IndexSymbol> index = new ArrayList<>();
        index.add(ConcreteIndexSymbol.getIndexSymbol("s", true));
        return index;
    }

    private Nonterminal getNonterminalWithIndexVariable() {

        List<IndexSymbol> index = indexForLhs();
        index.add(IndexVariable.getIndexVariable());
        return getNonterminalWithIndex(index);
    }

    private Nonterminal getNonterminalWithIndex(List<IndexSymbol> index) {

        Nonterminal bnt = sceneObject.scene().createNonterminal(LABEL, RANK, REDUCTION_TENTACLEs);
        return new IndexedNonterminalImpl(bnt, index);
    }

    private HeapConfiguration someRhs() {

        Type someType = sceneObject.scene().getType("type");
        SelectorLabel selectorLabel = sceneObject.scene().getSelectorLabel(VIOLATION_POINT_SELECTOR);

        TIntArrayList nodes = new TIntArrayList();
        return new InternalHeapConfiguration().builder()
                .addNodes(someType, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), selectorLabel, nodes.get(1))
                .build();
    }

}