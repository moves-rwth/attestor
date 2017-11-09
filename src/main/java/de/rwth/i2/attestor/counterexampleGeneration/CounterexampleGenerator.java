package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.counterexampleGeneration.heapConfWithPartner.HeapConfigurationWithPartner;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeCleanup;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.*;

public final class CounterexampleGenerator {

    private Program program;
    private List<ProgramState> trace;
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
        HeapConfigurationWithPartner inputWithPartner = new HeapConfigurationWithPartner(input, input.clone());
        initialState = initialState.shallowCopyWithUpdateHeap(inputWithPartner);


        try {
            StateSpace stateSpace = StateSpaceGenerator
                .builder()
                    .setStateLabelingStrategy(s -> {})
                    .setMaterializationStrategy(materializationStrategy)
                    .setCanonizationStrategy((semantics,state) -> state)
                    .setStateRefinementStrategy(stateRefinementStrategy)
                    .setBreadthFirstSearchEnabled(true)
                    .setSemanticsOptionsSupplier(s ->
                            new CounterexampleSemanticsOptions(s, trace)
                    )
                    .setExplorationStrategy((s,sp) -> {
                        ProgramState canon = canonicalizationStrategy.canonicalize(new Skip(1), s);
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

            return ((HeapConfigurationWithPartner) finalHeap).getPartner();
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
        finalStates.add(trace.get(trace.size()-1));
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

        public CounterexampleGeneratorBuilder setTrace(List<ProgramState> trace) {
            generator.trace = trace;
            return this;
        }

        public CounterexampleGeneratorBuilder addTraceState(ProgramState state) {
            if(generator.trace == null) {
                generator.trace = new ArrayList<>();
            }
            generator.trace.add(state);
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
