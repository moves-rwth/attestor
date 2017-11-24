package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.graph.heap.pair.HeapConfigurationPair;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoCanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoPostProcessingStrategy;

import java.util.*;

/**
 * This class generates an abstract heap configuration from a previously computed counterexample trace obtained
 * during LTL model checking.
 * The resulting abstract heap configuration describes a set of concrete states that can be fed into the program
 * as an input to reproduce the detected violation of an LTL formula.
 *
 * The main idea to generate suitable input states is to perform another symbolic execution of the program
 * with respect to the trace's initial state. In contrast to the usual state space generation, however,
 * no abstraction is performed. Moreover, each state now containsSubsumingState two heap configurations:
 * <ol>
 *     <li>One heap configuration is used by the state space generation to perform the symbolic execution.</li>
 *     <li>A second heap configuration is materialized together with the first heap configuration. However, no
 *         execution steps are performed. Thus, when reaching a final state, the second heap configuration
 *         corresponds to an initial state in which exactly those materialized states that are required to reach
 *         the final state in question have already been performed.</li>
 * </ol>
 * Furthermore, the counterexample generator performs its symbolic computation in a breadth-first manner to ensure
 * that all states required to cover a trace are eventually encountered.
 *
 * @author Christoph
 */
public final class CounterexampleGenerator {

    private Program program;
    private Trace trace;
    private CanonicalizationStrategy canonicalizationStrategy;
    private MaterializationStrategy materializationStrategy;
    private StateRefinementStrategy stateRefinementStrategy;
    private boolean deadVariableEliminationEnabled;

    /**
     * @return A builder object to construct a new CounterexampleGenerator.
     */
    public static CounterexampleGeneratorBuilder builder() {
        return new CounterexampleGeneratorBuilder();
    }

    /**
     * Prevent construction of CounterexampleGenerator without using CounterexampleGeneratorBuilder.builder().
     */
    private CounterexampleGenerator() {}

    /**
     * Start generation of an abstract heap configuration that represents input states to trigger the violation
     * of a previously checked LTL property that is described by the trace passed to the
     * CounterexampleGeneratorBuilder.
     *
     * @return The found abstract heap configuration.
     */
    public HeapConfiguration generate() {

        try {
            StateSpaceGenerator generator = setupStateSpaceGenerator();
            StateSpace stateSpace = generator.generate();

            Iterator<ProgramState>  iterator = stateSpace.getFinalStates().iterator();
            assert iterator.hasNext();
            HeapConfiguration finalHeap = iterator.next().getHeap();
            return ((HeapConfigurationPair) finalHeap).getPairedHeapConfiguration();

        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Counterexample generation failed.");
        }
    }

    private StateSpaceGenerator setupStateSpaceGenerator() {

        ProgramState initialState = getInitialState();
        return StateSpaceGenerator
                .builder()
                .setStateLabelingStrategy(s -> {})
                .setMaterializationStrategy(materializationStrategy)
                .setCanonizationStrategy(new NoCanonicalizationStrategy())
                .setStateRefinementStrategy(stateRefinementStrategy)
                .setDeadVariableElimination(deadVariableEliminationEnabled)
                .setBreadthFirstSearchEnabled(true)
                .setSemanticsOptionsSupplier(s -> new CounterexampleSymbolicExecutionObserver(s, trace))
                .setExplorationStrategy((s,sp) -> {
                    Semantics semantics = program.getStatement(s.getProgramCounter());
                    ProgramState canon = s;
                    if(semantics.permitsCanonicalization()) {
                        canon = canonicalizationStrategy.canonicalize(s);
                    }
                    return trace.containsSubsumingState(canon);
                })
                .setStateSpaceSupplier(getStateSpaceSupplier())
                .setAbortStrategy(s -> {})
                .setProgram(program)
                .addInitialState(initialState)
                .setStateCounter(states -> {})
                .setPostProcessingStrategy(new NoPostProcessingStrategy())
                .build();
    }

    private StateSpaceSupplier getStateSpaceSupplier() {

        CounterexampleStateSpaceSupplier stateSpaceSupplier
                = new CounterexampleStateSpaceSupplier(program, canonicalizationStrategy);
        Set<ProgramState> finalStates = new HashSet<>(1);
        finalStates.add(trace.getFinalState());
        stateSpaceSupplier.setFinalStatesOfPreviousProcedure(finalStates);
        return stateSpaceSupplier;
    }

    private ProgramState getInitialState() {

        ProgramState initialState = trace.getInitialState();
        HeapConfiguration input = initialState.getHeap();
        HeapConfigurationPair inputWithPartner = new HeapConfigurationPair(input, input.clone());
        return initialState.shallowCopyWithUpdateHeap(inputWithPartner);
    }

    public static class CounterexampleGeneratorBuilder {

        private CounterexampleGenerator generator;

        CounterexampleGeneratorBuilder() {
            generator = new CounterexampleGenerator();
        }

        /**
         * Finish construction of a CounterexampleGenerator.
         * @return CounterexampleGenerator.
         */
        public CounterexampleGenerator build() {
            assert generator.trace != null && !generator.trace.isEmpty();
            assert generator.program != null;
            assert generator.materializationStrategy != null;
            assert generator.canonicalizationStrategy != null;
            assert generator.stateRefinementStrategy != null;
            CounterexampleGenerator result = generator;
            generator = null;
            return result;
        }

        public CounterexampleGeneratorBuilder setTrace(Trace trace) {
            generator.trace = trace;
            return this;
        }

        public CounterexampleGeneratorBuilder setProgram(Program program) {
            generator.program = program;
            return this;
        }

        public CounterexampleGeneratorBuilder setMaterializationStrategy(MaterializationStrategy strategy) {
            generator.materializationStrategy = strategy;
            return this;
        }

        public CounterexampleGeneratorBuilder setCanonicalizationStrategy(CanonicalizationStrategy strategy) {
            generator.canonicalizationStrategy = strategy;
            return this;
        }

        public CounterexampleGeneratorBuilder setStateRefinementStrategy(StateRefinementStrategy strategy) {
            generator.stateRefinementStrategy = strategy;
            return this;
        }

        public CounterexampleGeneratorBuilder setDeadVariableEliminationEnabled(boolean enabled) {
            generator.deadVariableEliminationEnabled = enabled;
            return this;
        }
    }
}
