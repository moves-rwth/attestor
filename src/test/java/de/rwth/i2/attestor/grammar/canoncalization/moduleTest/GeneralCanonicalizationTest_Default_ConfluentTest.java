package de.rwth.i2.attestor.grammar.canoncalization.moduleTest;


import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.AbstractionOptions;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultCanonicalizationHelper;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.NonterminalEdgeBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeneralCanonicalizationTest_Default_ConfluentTest {

    private static int RANK = 2;
    private final SceneObject sceneObject = new MockupSceneObject();
    private final Type TYPE = sceneObject.scene().getType("type");
    private final SelectorLabel SEL = sceneObject.scene().getSelectorLabel("sel");
    CanonicalizationHelper canonicalizationHelper;

    @Before
    public void setUp() throws Exception {

        AbstractionOptions options = new AbstractionOptions()
                .setAdmissibleAbstraction(true)
                .setAdmissibleConstants(
                        sceneObject.scene().options().isAdmissibleConstantsEnabled()
                );

        EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(options);
        canonicalizationHelper = new DefaultCanonicalizationHelper(checkerProvider);
    }

    @Test
    public void test() {

        Nonterminal lhs = getNonterminal();
        HeapConfiguration rhs1 = getPattern1();
        HeapConfiguration rhs2 = getPattern2();
        Grammar grammar = Grammar.builder().addRule(lhs, rhs1)
                .addRule(lhs, rhs2)
                .build();

        GeneralCanonicalizationStrategy canonizer
                = new GeneralCanonicalizationStrategy(grammar, canonicalizationHelper);

        ProgramState inputState = sceneObject.scene().createProgramState(getInputGraph());
        ProgramState res = inputState.shallowCopyWithUpdateHeap(canonizer.canonicalize(inputState.getHeap()));
        assertEquals(expectedFullAbstraction(lhs), res);
    }


    private Nonterminal getNonterminal() {

        boolean[] isReductionTentacle = new boolean[RANK];
        return sceneObject.scene().createNonterminal("some label", RANK, isReductionTentacle);
    }

    private HeapConfiguration getPattern1() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        HeapConfigurationBuilder builder = hc.builder().addNodes(TYPE, RANK, nodes)
                .addSelector(nodes.get(0), SEL, nodes.get(1));
        for (int i = 0; i < RANK; i++) {
            builder.setExternal(nodes.get(i));
        }
        return builder.build();
    }

    private HeapConfiguration getPattern2() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        HeapConfigurationBuilder builder = hc.builder().addNodes(TYPE, RANK + 1, nodes)
                .addNonterminalEdge(getNonterminal())
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(getNonterminal())
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(2))
                .build()
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2));
        return builder.build();
    }

    private HeapConfiguration getInputGraph() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        int sizeOfChain = 10;

        TIntArrayList nodes = new TIntArrayList();
        HeapConfigurationBuilder builder = hc.builder().addNodes(TYPE, sizeOfChain + 1, nodes);
        for (int i = 0; i < sizeOfChain; i++) {
            builder.addSelector(nodes.get(i), SEL, nodes.get(i + 1));
        }
        return builder.build();
    }

    private ProgramState expectedFullAbstraction(Nonterminal lhs) {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        NonterminalEdgeBuilder builder = hc.builder().addNodes(TYPE, RANK, nodes)
                .addNonterminalEdge(lhs);
        for (int i = 0; i < RANK; i++) {
            builder.addTentacle(nodes.get(i));
        }
        hc = builder.build().build();
        return sceneObject.scene().createProgramState(hc);
    }

}
