package de.rwth.i2.attestor.exampleFactories;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;

import java.util.List;

public abstract class AbstractExampleFactory {

    public abstract List<Nonterminal> getNonterminals();
    public abstract List<SelectorLabel> getSelectorLabels();
    public abstract Type getNodeType();
    public abstract Grammar getGrammar();
    public abstract HeapConfiguration getInput();

    public MaterializationStrategy getMaterialization() {
        ViolationPointResolver vioResolver = new ViolationPointResolver( getGrammar() );
        MaterializationRuleManager grammarManager =
                new DefaultMaterializationRuleManager(vioResolver);
        GrammarResponseApplier ruleApplier =
                new DefaultGrammarResponseApplier( new GraphMaterializer() );
        return new GeneralMaterializationStrategy( grammarManager, ruleApplier );
    }

    public CanonicalizationStrategy getCanonicalization() {
        EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(
                0,
                0,
                false
        );
        CanonicalizationHelper canonicalizationHelper = new DefaultCanonicalizationHelper( checkerProvider );
        return new GeneralCanonicalizationStrategy(getGrammar(), canonicalizationHelper);
    }

    public ProgramState getInitialState() {
        return new DefaultProgramState(getInput());
    }

}