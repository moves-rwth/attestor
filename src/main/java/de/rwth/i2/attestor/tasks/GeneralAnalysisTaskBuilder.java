package de.rwth.i2.attestor.tasks;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AnalysisTaskBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultCanonicalizationStrategy;

/**
 * Implementation of most common functionality to create and customize an
 * {@link AnalysisTaskBuilder}.
 *
 * @author Christoph
 */
public abstract class GeneralAnalysisTaskBuilder implements AnalysisTaskBuilder {

    /**
     * The initial heap configuration that -- together with the initial program location --
     * determines the initial state of the heap configuration.
     */
    protected HeapConfiguration input = null;

    /**
     * The program that should be analyzed.
     */
    protected Program program = null;

    /**
     * The initial depth of the scope.
     */
    protected int scopeDepth = 0;

    /**
     * The strategy determining when to give up the state space generation.
     */
    private AbortStrategy abortStrategy = null;

    /**
     * The strategy determining how materialization is performed.
     */
    private MaterializationStrategy materializationStrategy = null;

    /**
     * The strategy determining how canonicalization is performed.
     */
    private CanonicalizationStrategy canonicalizationStrategy = null;

    /**
     * The strategy determining how the inclusion problem is discharged.
     */
    private InclusionStrategy inclusionStrategy = null;

    /**
     * The strategy determining how states are labeled with atomic propositions.
     */
    private StateLabelingStrategy stateLabelingStrategy = null;

    protected GeneralAnalysisTaskBuilder() {

    }

    @Override
    public AnalysisTaskBuilder setInput(HeapConfiguration input) {
        this.input = input;
        return this;
    }

    @Override
    public AnalysisTaskBuilder setProgram(Program program) {
        this.program = program;
        return this;
    }

    @Override
    public AnalysisTaskBuilder setScopeDepth(int scopeDepth) {
        this.scopeDepth = scopeDepth;
        return this;
    }

    @Override
    public AnalysisTaskBuilder setAbortStrategy(AbortStrategy abortStrategy) {
        this.abortStrategy = abortStrategy;
        return this;
    }

    @Override
    public AnalysisTaskBuilder setMaterializationStrategy(MaterializationStrategy materializationStrategy) {
        this.materializationStrategy = materializationStrategy;
        return this;
    }

    @Override
    public AnalysisTaskBuilder setCanonicalizationStrategy(CanonicalizationStrategy canonicalizationStrategy) {
        this.canonicalizationStrategy = canonicalizationStrategy;
        return this;
    }

    @Override
    public AnalysisTaskBuilder setInclusionStrategy(InclusionStrategy inclusionStrategy) {
        this.inclusionStrategy = inclusionStrategy;
        return this;
    }

    @Override
    public AnalysisTaskBuilder setStateLabelingStrategy(StateLabelingStrategy stateLabelingStrategy) {
        this.stateLabelingStrategy = stateLabelingStrategy;
        return this;
    }

    /**
     * Determines the initial program state.
     * @return The initial program state.
     */
    protected abstract ProgramState setupInitialState();

    /**
     * @return A state space generator builder with a common default configuration.
     */
    protected SSGBuilder setupStateSpaceGeneratorBuilder() {

        return StateSpaceGenerator.builder()
                .setAbortStrategy(
                        getAppliedAbortStrategy()
                )
                .setCanonizationStrategy(
                        getAppliedCanonizationStrategy()
                )
                .setInclusionStrategy(
                        getAppliedInclusionStrategy()
                )
                .setMaterializationStrategy(
                        getAppliedMaterializationStrategy()
                )
                .setStateLabelingStrategy(
                        getAppliedStateLabelingStrategy()
                )
                .setProgram(
                        program
                )
                .setInitialState(
                        setupInitialState()
                );
    }

    /**
     * @return The abort strategy that is applied when executing the analysis task.
     */
    private AbortStrategy getAppliedAbortStrategy() {
        int stateSpaceBound = Settings.getInstance().options().getMaxStateSpaceSize();
        int stateBound = Settings.getInstance().options().getMaxStateSize();

        if(abortStrategy == null) {
            return new StateSpaceBoundedAbortStrategy(stateSpaceBound, stateBound);
        }
        return abortStrategy;
    }

    /**
     * @return The canonicalization strategy that is applied when executing the analysis task.
     */
    private CanonicalizationStrategy getAppliedCanonizationStrategy() {
       if(canonicalizationStrategy == null) {
           return new DefaultCanonicalizationStrategy(getGrammar(), true);
       }
       return canonicalizationStrategy;
    }

    /*
     * @return The grammar used when executing the analysis task.
     */
    protected Grammar getGrammar() {
        return Settings.getInstance().grammar().getGrammar();
    }

    /**
     * @return The inclusion strategy that is applied when executing the analysis task.
     */
    private InclusionStrategy getAppliedInclusionStrategy() {
        if(inclusionStrategy == null) {
            return new GeneralInclusionStrategy();
        }
        return inclusionStrategy;
    }

    /**
     * @return The materialization strategy that is applied when executing the analysis task.
     */
    private MaterializationStrategy getAppliedMaterializationStrategy() {
        if(materializationStrategy == null) {
        	ViolationPointResolver vioResolver = new ViolationPointResolver( getGrammar() );
        	MaterializationRuleManager grammarManager = 
        			new DefaultMaterializationRuleManager(vioResolver);
        	GrammarResponseApplier ruleApplier = 
        			new DefaultGrammarResponseApplier( new GraphMaterializer() );
            return new GeneralMaterializationStrategy( grammarManager, ruleApplier );
        }
        return materializationStrategy;
    }

    /**
     * @return The state labeling strategy that is applied when executing the analysis task.
     */
    private StateLabelingStrategy getAppliedStateLabelingStrategy() {
        if(stateLabelingStrategy == null) {
            return new NoStateLabelingStrategy();
        }
        return stateLabelingStrategy;
    }

    /**
     * Checks whether all mandatory elements to create an analysis task are properly configured.
     */
    protected void checkElements() {
        if(input == null) {
            throw new IllegalStateException("No input has been defined.");
        }

        if(program == null) {
            throw new IllegalStateException("No program has been defined.");
        }
    }

}
