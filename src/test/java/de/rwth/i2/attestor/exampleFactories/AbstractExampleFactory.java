package de.rwth.i2.attestor.exampleFactories;

import de.rwth.i2.attestor.grammar.AbstractionOptions;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.strategies.GeneralMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.util.GrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.util.GraphMaterializer;
import de.rwth.i2.attestor.grammar.materialization.util.MaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPointResolver;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;

import java.util.List;

public abstract class AbstractExampleFactory extends SceneObject {

    public AbstractExampleFactory(SceneObject sceneObject) {

        super(sceneObject);
    }

    public abstract List<Nonterminal> getNonterminals();

    public abstract List<SelectorLabel> getSelectorLabels();

    public abstract Type getNodeType();

    public abstract Grammar getGrammar();

    public abstract HeapConfiguration getInput();

    public MaterializationStrategy getMaterialization() {

        ViolationPointResolver vioResolver = new ViolationPointResolver(getGrammar());
        MaterializationRuleManager grammarManager =
                new DefaultMaterializationRuleManager(vioResolver);
        GrammarResponseApplier ruleApplier =
                new DefaultGrammarResponseApplier(new GraphMaterializer());
        return new GeneralMaterializationStrategy(grammarManager, ruleApplier);
    }

    public CanonicalizationStrategy getCanonicalization() {

        AbstractionOptions options = new AbstractionOptions()
                .setAdmissibleConstants(
                        scene().options().isAdmissibleConstantsEnabled()
                );

        EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(options);
        CanonicalizationHelper canonicalizationHelper = new DefaultCanonicalizationHelper(checkerProvider);
        return new GeneralCanonicalizationStrategy(getGrammar(), canonicalizationHelper);
    }

    public ProgramState getInitialState() {

        return scene().createProgramState(getInput());
    }

}
