package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.graph.heap.pair.HeapConfigurationPair;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.strategies.NoCanonicalizationStrategy;

import java.util.*;

public final class CounterexampleGenerator {

    private Program program;
    private Trace trace;
    private CanonicalizationStrategy canonicalizationStrategy;
    private MaterializationStrategy materializationStrategy;
    private StateRefinementStrategy stateRefinementStrategy;

    public static CounterexampleGeneratorBuilder builder() {
        return new CounterexampleGeneratorBuilder();
    }

    private CounterexampleGenerator() {
    }

    public HeapConfiguration generate() {

        ProgramState initialState = trace.iterator().next();
        HeapConfiguration input = initialState.getHeap();
        HeapConfigurationPair inputWithPartner = new HeapConfigurationPair(input, input.clone());
        initialState = initialState.shallowCopyWithUpdateHeap(inputWithPartner);


        try {
            StateSpace stateSpace = StateSpaceGenerator
                .builder()
                    .setStateLabelingStrategy(s -> {})
                    .setMaterializationStrategy(materializationStrategy)
                    .setCanonizationStrategy(new NoCanonicalizationStrategy())
                    .setStateRefinementStrategy(stateRefinementStrategy)
                    .setBreadthFirstSearchEnabled(true)
                    .setSemanticsOptionsSupplier(s ->
                            new CounterexampleSemanticsOptions(s, trace)
                    )
                    .setExplorationStrategy((s,sp) -> {
                        ProgramState canon = canonicalizationStrategy.canonicalize(s);
                        return trace.contains(canon);
                    })
                    .setStateSpaceSupplier(getStateSpaceSupplier())
                    .setAbortStrategy(s -> {})
                    .setProgram(program)
                    .addInitialState(initialState)
                    .setStateCounter(states -> {})
                .build()
                .generate();

            assert stateSpace.getFinalStates().size() == 1;
            HeapConfiguration finalHeap = stateSpace
                    .getFinalStates()
                    .iterator()
                    .next()
                    .getHeap();

            return ((HeapConfigurationPair) finalHeap).getPairedHeapConfiguration();
        } catch (StateSpaceGenerationAbortedException e) {
            e.printStackTrace();
            return null;
        }

    }

    private StateSpaceSupplier getStateSpaceSupplier() {

        CounterexampleStateSpaceSupplier stateSpaceSupplier = new CounterexampleStateSpaceSupplier(
                canonicalizationStrategy
        );
        Set<ProgramState> finalStates = new HashSet<>(1);
        finalStates.add(trace.getFinalState());
        stateSpaceSupplier.setFinalStatesOfPreviousProcedure(finalStates);
        return stateSpaceSupplier;
    }

    public static class CounterexampleGeneratorBuilder {

        private CounterexampleGenerator generator;

        CounterexampleGeneratorBuilder() {
            generator = new CounterexampleGenerator();
        }

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
    }
}
