package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.counterexamples.heapConfWithPartner.HeapConfigurationWithPartner;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

public final class CounterexampleGenerator {

    private Program program;
    private StateSpace traceStateSpace; // assumption: connected, exactly one initial and one final state
    private CanonicalizationStrategy canonicalizationStrategy;
    private MaterializationStrategy materializationStrategy;
    private StateRefinementStrategy stateRefinementStrategy;

    public static CounterexampleGeneratorBuilder builder() {
        return new CounterexampleGeneratorBuilder();
    }

    private CounterexampleGenerator() {
    }

    public HeapConfiguration generate() {

        ProgramState initialState = traceStateSpace.getInitialStates().iterator().next().clone();
        HeapConfiguration input = initialState.getHeap();
        HeapConfigurationWithPartner inputWithPartner = new HeapConfigurationWithPartner(input, input.clone());
        initialState = initialState.shallowCopyWithUpdateHeap(inputWithPartner);

        try {
            StateSpace stateSpace = StateSpaceGenerator
                .builder()
                    .setStateLabelingStrategy(s -> {})
                    .setMaterializationStrategy(materializationStrategy)
                    .setCanonizationStrategy((sem,state) -> state)
                    .setStateRefinementStrategy(stateRefinementStrategy)
                    .setBreadthFirstSearchEnabled(true)
                    .setSemanticsOptionsSupplier(s -> new StateSpaceGeneratorSemanticsOptions(s))
                    .setExplorationStrategy(s -> {
                        ProgramState canon = canonicalizationStrategy.canonicalize(new Skip(1), s);
                        return traceStateSpace.getStates().contains(canon);
                    })
                    .setStateSpaceSupplier(() -> new InternalStateSpace(100))
                    .setAbortStrategy(s -> {})
                    .setProgram(program)
                    .addInitialState(initialState)
                    .setStateCounter(states -> {})
                .build()
                .generate();

            assert stateSpace.getFinalStateIds().size() == 1;
            HeapConfiguration finalHeap = stateSpace
                    .getFinalStates()
                    .iterator()
                    .next()
                    .getHeap();

            return ((HeapConfigurationWithPartner) finalHeap).getPartner();
        } catch (StateSpaceGenerationAbortedException e) {
            e.printStackTrace();
            return null;
        }

    }

    static class CounterexampleGeneratorBuilder {

        private CounterexampleGenerator generator;

        CounterexampleGeneratorBuilder() {
            generator = new CounterexampleGenerator();
        }

        public CounterexampleGenerator build() {
            assert generator.traceStateSpace != null;
            assert generator.program != null;
            assert generator.materializationStrategy != null;
            assert generator.canonicalizationStrategy != null;
            assert generator.stateRefinementStrategy != null;
            CounterexampleGenerator result = generator;
            generator = null;
            return result;
        }

        public CounterexampleGeneratorBuilder setTraceStateSpace(StateSpace traceStateSpace) {
            assert traceStateSpace.getInitialStateIds().size() == 1;
            assert traceStateSpace.getFinalStateIds().size() == 1;
            generator.traceStateSpace = traceStateSpace;
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
