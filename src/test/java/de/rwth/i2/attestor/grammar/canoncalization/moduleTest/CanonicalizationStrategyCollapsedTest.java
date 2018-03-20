package de.rwth.i2.attestor.grammar.canoncalization.moduleTest;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.AbstractionOptions;
import de.rwth.i2.attestor.grammar.CollapsedHeapConfiguration;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultCanonicalizationHelper;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import gnu.trove.list.array.TIntArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CanonicalizationStrategyCollapsedTest {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger("CanonicalizationStrategyTest");

    private SceneObject sceneObject;
    private ExampleHcImplFactory hcFactory;

    private GeneralCanonicalizationStrategy canonicalizationStrategy;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        hcFactory = new ExampleHcImplFactory(sceneObject);

        Nonterminal listLabel = sceneObject.scene().createNonterminal("List", 2, new boolean[]{false, true});

        Grammar grammar = Grammar.builder()
                .addRule(listLabel, hcFactory.getListRule1())
                .addRule(listLabel, hcFactory.getListRule2())
                .addRule(listLabel, hcFactory.getListRule3())
                .addCollapsedRule(listLabel, getCollapsedRule())
                .build();

        AbstractionOptions options = new AbstractionOptions()
                .setAdmissibleConstants(
                        sceneObject.scene().options().isAdmissibleConstantsEnabled()
                );

        EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(options);
        CanonicalizationHelper canonicalizationHelper = new DefaultCanonicalizationHelper(checkerProvider);

        canonicalizationStrategy = new GeneralCanonicalizationStrategy(grammar, canonicalizationHelper);
    }

    private CollapsedHeapConfiguration getCollapsedRule() {


        HeapConfiguration original = hcFactory.getListRule1();
        HeapConfiguration collapsed = hcFactory.getTrivialCyclicSLL().builder().setExternal(0).build();

        TIntArrayList extMapping = new TIntArrayList();
        extMapping.add(0);
        extMapping.add(0);

        return new CollapsedHeapConfiguration(original, collapsed, extMapping);

    }

    @Test
    public void testTrivialSLL() {

        HeapConfiguration test = hcFactory.getTrivialCyclicSLL();

        HeapConfiguration result = canonicalizationStrategy.canonicalize(test);

        assertEquals("result not as expected", hcFactory.getCyclicListHandle(), result);
    }
}
