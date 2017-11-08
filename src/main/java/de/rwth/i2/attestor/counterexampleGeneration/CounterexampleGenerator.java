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

    private ProgramState lastProcedureInitialState;
    private Set<ProgramState> lastProcedureFinalStates;
    private InvokeCleanup lastProcedureInvokeCleanup;

    public static CounterexampleGeneratorBuilder builder() {
        return new CounterexampleGeneratorBuilder();
    }

    private CounterexampleGenerator() {
    }

    protected void setLastProcedureInvokeCleanup(InvokeCleanup invokeCleanup) {
        this.lastProcedureInvokeCleanup = invokeCleanup;
    }

    protected InvokeCleanup getLastProcedureInvokeCleanup() {
        return lastProcedureInvokeCleanup;
    }

    protected void setLastProcedureInitialState(ProgramState initialState) {
        lastProcedureInitialState = initialState;
    }

    protected void setLastProcedureFinalStates(Set<ProgramState> finalStates) {
        lastProcedureFinalStates = finalStates;
    }

    protected ProgramState getTraceSuccessor(ProgramState state) {

        Iterator<ProgramState> iter = trace.iterator();
        while(iter.hasNext()) {
            ProgramState current = iter.next();
            if(current.getStateSpaceId() == state.getStateSpaceId()) {
                if(iter.hasNext()) {
                    return iter.next();
                }
                return null;
            }
        }
        return null;
    }

    protected CanonicalizationStrategy getCanonicalizationStrategy() {
        return canonicalizationStrategy;
    }

    protected ProgramState getLastProcedureInitialState() {

        if(lastProcedureInitialState == null) {
            return trace.get(0);
        }
        return lastProcedureInitialState;
    }

    protected Set<ProgramState> getLastProcedureFinalStates() {

        if(lastProcedureFinalStates == null) {
            Set<ProgramState> result = new HashSet<>(1);
            result.add(trace.get(trace.size()-1));
            return result;
        }

        return lastProcedureFinalStates;
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
                    .setCanonizationStrategy((sem,state) -> state)
                    .setStateRefinementStrategy(stateRefinementStrategy)
                    .setBreadthFirstSearchEnabled(true)
                    .setSemanticsOptionsSupplier(s ->
                            new CounterexampleSemanticsOptions(s, this)
                    )
                    .setExplorationStrategy((s,sp) -> {
                        ProgramState canon = canonicalizationStrategy.canonicalize(new Skip(1), s);
                        return trace.contains(canon);
                    })
                    .setStateSpaceSupplier(() ->
                            new CounterexampleStateSpace(this)
                    )
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
