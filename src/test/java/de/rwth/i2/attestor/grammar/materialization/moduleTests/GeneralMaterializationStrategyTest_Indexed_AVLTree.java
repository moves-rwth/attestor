package de.rwth.i2.attestor.grammar.materialization.moduleTests;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.strategies.GeneralMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.util.*;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.BalancedTreeGrammar;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.IndexedState;
import de.rwth.i2.attestor.programState.indexedState.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
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
import static org.hamcrest.Matchers.containsInAnyOrder;

public class GeneralMaterializationStrategyTest_Indexed_AVLTree {

    private static final AbstractIndexSymbol ABSTRACT_INDEX_SYMBOL = AbstractIndexSymbol.get("X");
    private static final ConcreteIndexSymbol INDEX_SYMBOL_Z = ConcreteIndexSymbol.getIndexSymbol("Z", true);
    private static final ConcreteIndexSymbol INDEX_SYMBOL_S = ConcreteIndexSymbol.getIndexSymbol("s", false);
    private static final String VIOLATIONPOINT_VARIABLE = "x";
    private SceneObject sceneObject;
    private BalancedTreeGrammar treeGrammar;
    private GeneralMaterializationStrategy materializer;


    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        treeGrammar = new BalancedTreeGrammar(sceneObject);

        Grammar balancedTreeGrammar = treeGrammar.getGrammar();
        ViolationPointResolver vioResolver = new ViolationPointResolver(balancedTreeGrammar);

        IndexMatcher indexMatcher = new IndexMatcher(new DefaultIndexMaterialization());
        MaterializationRuleManager ruleManager =
                new IndexedMaterializationRuleManager(vioResolver, indexMatcher);

        GrammarResponseApplier ruleApplier =
                new IndexedGrammarResponseApplier(new IndexMaterializationStrategy(), new GraphMaterializer());
        this.materializer = new GeneralMaterializationStrategy(ruleManager, ruleApplier);
    }

    /**
     * Only one rule applicable. index of lhs of this rule exactly matches the
     * index of the nonterminal in the heap.<br>
     * Instantiation: No <br>
     * Materialization: No
     */
    @Test
    public void testMaterialize_ConcreteIndex_OneRule() {

        List<IndexSymbol> indexForReferenceNt = getIndex_sX();

        final HeapConfiguration inputHeap =
                getInputWithIndexZ(indexForReferenceNt);
        ProgramState inputState = new IndexedState(inputHeap).prepareHeap();
        final HeapConfiguration expectedHeap =
                getAppliedBalancedLeafRule_WithReferenceIndex(indexForReferenceNt);
        ProgramState expectedState = new IndexedState(expectedHeap).prepareHeap();

        ViolationPoints vioPoints = new ViolationPoints();
        vioPoints.add(VIOLATIONPOINT_VARIABLE, "left");

        Collection<HeapConfiguration> materializedStates = materializer.materialize(inputState.getHeap(), vioPoints);

        assertThat(materializedStates, containsInAnyOrder(expectedState.getHeap()));
    }

    /**
     * Several Rules applicable. In two cases the index of the lhs exactly matches
     * the nonterminal in the heap. In one case it is instantiated.<br>
     * Instantiation: Yes <br>
     * Materialization: No <br>
     */
    @Test
    public void testMaterialize_ConcreteIndex_MoreRules() {

        List<IndexSymbol> indexForReferenceNt = getIndex_sZ();

        final HeapConfiguration inputHeap =
                getInputWithIndex_sZ(indexForReferenceNt);
        ProgramState inputState = new IndexedState(inputHeap).prepareHeap();

        final HeapConfiguration expectedHeap1 =
                getExpected_sZ_BalancedRule(indexForReferenceNt);
        ProgramState expectedState1 = new IndexedState(expectedHeap1).prepareHeap();
        final HeapConfiguration expectedHeap2 =
                getAppliedLeftLeafRuleWithReferenceIndex(indexForReferenceNt);
        ProgramState expectedState2 = new IndexedState(expectedHeap2).prepareHeap();
        final HeapConfiguration expectedHeap3 =
                getAppliedRightLeafRuleWithReferenceIndex(indexForReferenceNt);
        ProgramState expectedState3 = new IndexedState(expectedHeap3).prepareHeap();

        ViolationPoints vioPoints = new ViolationPoints();
        vioPoints.add(VIOLATIONPOINT_VARIABLE, "left");

        Collection<HeapConfiguration> materializedStates = materializer.materialize(inputState.getHeap(), vioPoints);

        assertThat(materializedStates, containsInAnyOrder(expectedState1.getHeap(),
                expectedState2.getHeap(),
                expectedState3.getHeap()));
    }

    /**
     * Several rules appliable. In each case, the rule needs instantiation, but
     * the graph no materialization <br>
     * Instantiation: yes <br>
     * Materialization: no
     */
    @Test
    public void testMaterialize_AbstractIndex_OnlyInstantiation() {

        List<IndexSymbol> indexForReferenceNt = getIndex_sX();

        final HeapConfiguration inputHeap =
                getInputWithIndex_ssX(indexForReferenceNt);
        ProgramState inputState = new IndexedState(inputHeap).prepareHeap();

        final HeapConfiguration expectedHeap1 =
                getExpectedOneNonterminal_ssX_BalancedRule(indexForReferenceNt);
        ProgramState expectedState1 = new IndexedState(expectedHeap1).prepareHeap();
        final HeapConfiguration expectedHeap2 =
                getExpected_ssX_LeftRule(indexForReferenceNt);
        ProgramState expectedState2 = new IndexedState(expectedHeap2).prepareHeap();
        final HeapConfiguration expectedHeap3 =
                getExpected_ssX_RightRule(indexForReferenceNt);
        ProgramState expectedState3 = new IndexedState(expectedHeap3).prepareHeap();

        ViolationPoints vioPoints = new ViolationPoints();
        vioPoints.add(VIOLATIONPOINT_VARIABLE, "left");

        Collection<HeapConfiguration> materializedStates = materializer.materialize(inputState.getHeap(), vioPoints);

        assertThat(materializedStates, containsInAnyOrder(expectedState1.getHeap(),
                expectedState2.getHeap(),
                expectedState3.getHeap()));
    }

    /**
     * Several rules appliable. In each case, the rule needs instantiation and the graph
     * different materializations to match the different rules
     * Instantiation: yes <br>
     * Materialization: yes
     */
    @Test
    public void testMaterialize_AbstractIndex_WithMaterializationApplicable() {

        List<IndexSymbol> indexForReferenceNt = getIndex_X();

        final HeapConfiguration inputHeap =
                getInputWithIndex_sX(indexForReferenceNt);
        ProgramState inputState = new IndexedState(inputHeap).prepareHeap();

        final HeapConfiguration expectedHeap1 =
                getExpected_sX_BalancedRule(getIndex_X());//no materialization
        ProgramState expectedState1 = new IndexedState(expectedHeap1).prepareHeap();
        final HeapConfiguration expectedHeap2 =
                getExpected_ssX_LeftRule(getIndex_sX());//materialization: X -> sX
        ProgramState expectedState2 = new IndexedState(expectedHeap2).prepareHeap();
        final HeapConfiguration expectedHeap3 =
                getExpected_ssX_RightRule(getIndex_sX());//materialization X -> sX
        ProgramState expectedState3 = new IndexedState(expectedHeap3).prepareHeap();
        final HeapConfiguration expectedHeap4 =
                getAppliedLeftLeafRuleWithReferenceIndex(getIndex_Z());//materialization X -> Z
        ProgramState expectedState4 = new IndexedState(expectedHeap4).prepareHeap();
        final HeapConfiguration expectedHeap5 =
                getAppliedRightLeafRuleWithReferenceIndex(getIndex_Z());//materialization X->Z
        ProgramState expectedState5 = new IndexedState(expectedHeap5).prepareHeap();

        ViolationPoints vioPoints = new ViolationPoints();
        vioPoints.add(VIOLATIONPOINT_VARIABLE, "left");

        Collection<HeapConfiguration> materializedStates = materializer.materialize(inputState.getHeap(), vioPoints);

        assertThat(materializedStates, containsInAnyOrder(expectedState1.getHeap(),
                expectedState2.getHeap(),
                expectedState3.getHeap(),
                expectedState4.getHeap(),
                expectedState5.getHeap()));
    }


    private HeapConfiguration getExpectedOneNonterminal_ssX_BalancedRule(List<IndexSymbol> indexForReferenceNt) {

        return getAppliedBalancedRuleWithIndex(getIndex_sX(), indexForReferenceNt);
    }


    private List<IndexSymbol> getIndex_Z() {

        return SingleElementUtil.createList(INDEX_SYMBOL_Z);
    }

    private List<IndexSymbol> getIndex_sZ() {

        List<IndexSymbol> index = new ArrayList<>();
        index.add(INDEX_SYMBOL_S);
        index.add(INDEX_SYMBOL_Z);
        return index;
    }

    private List<IndexSymbol> getIndex_X() {

        List<IndexSymbol> index = new ArrayList<>();
        index.add(ABSTRACT_INDEX_SYMBOL);
        return index;
    }

    private List<IndexSymbol> getIndex_sX() {

        List<IndexSymbol> index = new ArrayList<>();
        index.add(INDEX_SYMBOL_S);
        index.add(ABSTRACT_INDEX_SYMBOL);
        return index;
    }

    private List<IndexSymbol> getIndex_ssX() {

        List<IndexSymbol> index = new ArrayList<>();
        index.add(INDEX_SYMBOL_S);
        index.add(INDEX_SYMBOL_S);
        index.add(ABSTRACT_INDEX_SYMBOL);
        return index;

    }


    private HeapConfiguration getInputWithIndexZ(List<IndexSymbol> indexForReferenceNt) {

        return getGraphWithReferenzNonterminalWithIndex(getIndex_Z(), indexForReferenceNt);
    }

    private HeapConfiguration getInputWithIndex_sZ(List<IndexSymbol> indexForReferenceNt) {

        return getGraphWithReferenzNonterminalWithIndex(getIndex_sZ(), indexForReferenceNt);
    }


    private HeapConfiguration getInputWithIndex_sX(List<IndexSymbol> indexForReferenceNt) {

        return getGraphWithReferenzNonterminalWithIndex(getIndex_sX(), indexForReferenceNt);
    }

    private HeapConfiguration getInputWithIndex_ssX(List<IndexSymbol> indexForReferenceNt) {

        return getGraphWithReferenzNonterminalWithIndex(getIndex_ssX(), indexForReferenceNt);
    }


    private HeapConfiguration getExpected_sZ_BalancedRule(List<IndexSymbol> indexForReferenceNt) {

        List<IndexSymbol> index_Z = getIndex_Z();
        return getAppliedBalancedRuleWithIndex(index_Z, indexForReferenceNt);
    }

    private HeapConfiguration getExpected_sX_BalancedRule(List<IndexSymbol> indexForReferenceNt) {

        return getAppliedBalancedRuleWithIndex(getIndex_X(), indexForReferenceNt);
    }

    private HeapConfiguration getExpected_ssX_LeftRule(List<IndexSymbol> indexForReferenceNt) {

        return getAppliedLeftRuleWithIndices(getIndex_sX(), getIndex_X(), indexForReferenceNt);
    }

    private HeapConfiguration getExpected_ssX_RightRule(List<IndexSymbol> indicesForReferenceNt) {

        return getAppliedRightRuleWithIndices(getIndex_X(), getIndex_sX(), indicesForReferenceNt);
    }

    private HeapConfiguration getGraphWithReferenzNonterminalWithIndex(List<IndexSymbol> index,
                                                                       List<IndexSymbol> indexForReferenceNt) {

        HeapConfiguration hc = new InternalHeapConfiguration();
        String label = treeGrammar.NT_LABEL;
        int rank = treeGrammar.NT_RANK;
        boolean[] isReductionTentacle = treeGrammar.IS_REDUCTION_TENTACLE;
        Nonterminal bnt = sceneObject.scene().createNonterminal(label, rank, isReductionTentacle);
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);

        IndexedNonterminal referenceNt = new IndexedNonterminalImpl(bnt, indexForReferenceNt);
        Type type = treeGrammar.TYPE;

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 4, nodes)
                .addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(referenceNt)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .build();
    }

    private HeapConfiguration getAppliedBalancedLeafRule_WithReferenceIndex(List<IndexSymbol> indexForReferenceNt) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        String label = treeGrammar.NT_LABEL;
        IndexedNonterminal referenceNt = new IndexedNonterminalImpl(sceneObject.scene().getNonterminal(label)
                , indexForReferenceNt);

        Type type = treeGrammar.TYPE;
        SelectorLabel leftLabel = treeGrammar.SELECTOR_LEFT_0;
        SelectorLabel rightLabel = treeGrammar.SELECTOR_RIGHT_0;

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 4, nodes)
                .addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0))
                .addSelector(nodes.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes.get(0), rightLabel, nodes.get(1))
                .addNonterminalEdge(referenceNt)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .build();
    }


    private HeapConfiguration getAppliedLeftLeafRuleWithReferenceIndex(List<IndexSymbol> indexForReferenceNt) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = treeGrammar.TYPE;
        SelectorLabel leftLabel = treeGrammar.SELECTOR_LEFT_1;
        SelectorLabel rightLabel = treeGrammar.SELECTOR_RIGHT_M1;
        SelectorLabel parentLabel = treeGrammar.SELECTOR_PARENT;

        String label = treeGrammar.NT_LABEL;
        int rank = treeGrammar.NT_RANK;
        boolean[] isReductionTentacle = treeGrammar.IS_REDUCTION_TENTACLE;
        List<IndexSymbol> index_Z = getIndex_Z();

        Nonterminal bnt = sceneObject.scene().createNonterminal(label, rank, isReductionTentacle);
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index_Z);
        IndexedNonterminal referenceNt = new IndexedNonterminalImpl(bnt, indexForReferenceNt);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 5, nodes)
                .addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0))
                .addSelector(nodes.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes.get(1), parentLabel, nodes.get(0))
                .addSelector(nodes.get(0), rightLabel, nodes.get(2))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(2))
                .build()
                .addNonterminalEdge(referenceNt)
                .addTentacle(nodes.get(3))
                .addTentacle(nodes.get(4))
                .build()
                .build();
    }

    private HeapConfiguration getAppliedRightLeafRuleWithReferenceIndex(
            List<IndexSymbol> indexForReferenceNonterminal) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = treeGrammar.TYPE;
        SelectorLabel leftLabel = treeGrammar.SELECTOR_LEFT_M1;
        SelectorLabel rightLabel = treeGrammar.SELECTOR_RIGHT_1;
        SelectorLabel parentLabel = treeGrammar.SELECTOR_PARENT;

        String label = treeGrammar.NT_LABEL;
        int rank = treeGrammar.NT_RANK;
        boolean[] isReductionTentacle = treeGrammar.IS_REDUCTION_TENTACLE;
        List<IndexSymbol> index_Z = getIndex_Z();

        Nonterminal bnt = sceneObject.scene().createNonterminal(label, rank, isReductionTentacle);
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index_Z);
        IndexedNonterminal referenceNt =
                new IndexedNonterminalImpl(bnt, indexForReferenceNonterminal);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 5, nodes)
                .addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0))
                .addSelector(nodes.get(0), leftLabel, nodes.get(2))
                .addSelector(nodes.get(0), rightLabel, nodes.get(1))
                .addSelector(nodes.get(1), parentLabel, nodes.get(0))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(2))
                .build()
                .addNonterminalEdge(referenceNt)
                .addTentacle(nodes.get(3))
                .addTentacle(nodes.get(4))
                .build()
                .build();
    }

    private HeapConfiguration getAppliedBalancedRuleWithIndex(List<IndexSymbol> index,
                                                              List<IndexSymbol> indexForReferenceNonterminal) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = treeGrammar.TYPE;
        SelectorLabel leftLabel = treeGrammar.SELECTOR_LEFT_0;
        SelectorLabel rightLabel = treeGrammar.SELECTOR_RIGHT_0;
        SelectorLabel parentLabel = treeGrammar.SELECTOR_PARENT;

        String label = treeGrammar.NT_LABEL;
        int rank = treeGrammar.NT_RANK;
        boolean[] isReductionTentacle = treeGrammar.IS_REDUCTION_TENTACLE;

        Nonterminal bnt = sceneObject.scene().createNonterminal(label, rank, isReductionTentacle);
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);
        IndexedNonterminal referenceNt = new IndexedNonterminalImpl(bnt, indexForReferenceNonterminal);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 6, nodes)
                .addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0))
                .addSelector(nodes.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes.get(1), parentLabel, nodes.get(0))
                .addSelector(nodes.get(0), rightLabel, nodes.get(2))
                .addSelector(nodes.get(2), parentLabel, nodes.get(0))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(3))
                .build()
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .addNonterminalEdge(referenceNt)
                .addTentacle(nodes.get(4))
                .addTentacle(nodes.get(5))
                .build()
                .build();
    }

    private HeapConfiguration getAppliedLeftRuleWithIndices(List<IndexSymbol> leftIndex,
                                                            List<IndexSymbol> rightIndex,
                                                            List<IndexSymbol> indexForReferenceNonterminal) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = treeGrammar.TYPE;
        SelectorLabel leftLabel = treeGrammar.SELECTOR_LEFT_1;
        SelectorLabel rightLabel = treeGrammar.SELECTOR_RIGHT_M1;
        SelectorLabel parentLabel = treeGrammar.SELECTOR_PARENT;

        String label = treeGrammar.NT_LABEL;
        int rank = treeGrammar.NT_RANK;
        boolean[] isReductionTentacle = treeGrammar.IS_REDUCTION_TENTACLE;

        Nonterminal bnt = sceneObject.scene().createNonterminal(label, rank, isReductionTentacle);
        IndexedNonterminal ntLeft =
                new IndexedNonterminalImpl(bnt, leftIndex);
        IndexedNonterminal ntRight =
                new IndexedNonterminalImpl(bnt, rightIndex);
        IndexedNonterminal referenceNt =
                new IndexedNonterminalImpl(bnt, indexForReferenceNonterminal);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 6, nodes)
                .addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0))
                .addSelector(nodes.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes.get(1), parentLabel, nodes.get(0))
                .addSelector(nodes.get(0), rightLabel, nodes.get(2))
                .addSelector(nodes.get(2), parentLabel, nodes.get(0))
                .addNonterminalEdge(ntLeft)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(3))
                .build()
                .addNonterminalEdge(ntRight)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .addNonterminalEdge(referenceNt)
                .addTentacle(nodes.get(4))
                .addTentacle(nodes.get(5))
                .build()
                .build();
    }

    private HeapConfiguration getAppliedRightRuleWithIndices(List<IndexSymbol> leftIndex,
                                                             List<IndexSymbol> rightIndex, List<IndexSymbol> indexForReferenceNonterminal) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        Type type = treeGrammar.TYPE;
        SelectorLabel leftLabel = treeGrammar.SELECTOR_LEFT_M1;
        SelectorLabel rightLabel = treeGrammar.SELECTOR_RIGHT_1;
        SelectorLabel parentLabel = treeGrammar.SELECTOR_PARENT;

        String label = treeGrammar.NT_LABEL;
        int rank = treeGrammar.NT_RANK;
        boolean[] isReductionTentacle = treeGrammar.IS_REDUCTION_TENTACLE;

        Nonterminal bnt = sceneObject.scene().createNonterminal(label, rank, isReductionTentacle);
        IndexedNonterminal ntLeft =
                new IndexedNonterminalImpl(bnt, leftIndex);
        IndexedNonterminal ntRight =
                new IndexedNonterminalImpl(bnt, rightIndex);
        IndexedNonterminal referenceNt =
                new IndexedNonterminalImpl(bnt, indexForReferenceNonterminal);

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 6, nodes)
                .addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0))
                .addSelector(nodes.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes.get(1), parentLabel, nodes.get(0))
                .addSelector(nodes.get(0), rightLabel, nodes.get(2))
                .addSelector(nodes.get(2), parentLabel, nodes.get(0))
                .addNonterminalEdge(ntLeft)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(3))
                .build()
                .addNonterminalEdge(ntRight)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .build()
                .addNonterminalEdge(referenceNt)
                .addTentacle(nodes.get(4))
                .addTentacle(nodes.get(5))
                .build()
                .build();
    }
}
