package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.MaterializationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.pair.HeapConfigurationPair;
import de.rwth.i2.attestor.ipa.methodExecution.AbstractMethodExecutor;
import de.rwth.i2.attestor.ipa.methodExecution.ScopeExtractor;
import de.rwth.i2.attestor.ipa.methods.Method;
import de.rwth.i2.attestor.ipa.methods.MethodExecutor;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoCanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoPostProcessingStrategy;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class CounterexampleGenerator {

    private CounterexampleTrace trace;
    private Collection<Method> availableMethods;
    private Program topLevelProgram;
    private StateSubsumptionStrategy stateSubsumptionStrategy;
    private MaterializationStrategy materializationStrategy;
    private CanonicalizationStrategy canonicalizationStrategy;
    private StateRefinementStrategy stateRefinementStrategy;
    private Function<Method, ScopeExtractor> scopeExtractorFactory;
    private TraceBasedExplorationStrategy topLevelExplorationStrategy;

    private final Stack<Predicate<ProgramState>> requiredFinalStatesStack = new Stack<>();
    private final Map<Method, MethodExecutor> originalExecutors = new LinkedHashMap<>();

    public static Builder builder() {
        return new Builder();
    }

    public ProgramState generate() {

        topLevelExplorationStrategy = new TraceBasedExplorationStrategy(trace, stateSubsumptionStrategy);

        requiredFinalStatesStack.push(
                state -> stateSubsumptionStrategy.subsumes(state, trace.getFinalState())
        );

        decorateMethodExecutioners();

        Collection<ProgramState> finalStates = determineFinalStates();

        if(finalStates.size() != 1) {
            throw new IllegalStateException("Failed to determine a unique counterexample input " +
                    "(determined " +
                    finalStates.size() +
                    " final states)");
        }

        ProgramState result = extractCounterexampleInput(finalStates.iterator().next());

        restoreOriginalMethodExecutioners();

        return result;
    }

    private void decorateMethodExecutioners() {

        for(Method method : availableMethods) {
            decorateMethodExecutioner(method);
        }
    }

    private void decorateMethodExecutioner(Method method) {

        MethodExecutor executor = method.getMethodExecutor();
        originalExecutors.put(method, executor);

        assert executor instanceof AbstractMethodExecutor;
        AbstractMethodExecutor abstractExecutor = (AbstractMethodExecutor) executor;

        CounterexampleMethodExecutor newExecutor = new CounterexampleMethodExecutor(
                scopeExtractorFactory.apply(method),
                new CounterexampleContractCollection(abstractExecutor.getContractCollection()),
                new CounterexampleContractGenerator(getFinalStatesComputer(method)),
                canonicalizationStrategy
        );
        method.setMethodExecution(newExecutor);
    }

    private void restoreOriginalMethodExecutioners() {

        originalExecutors.forEach(Method::setMethodExecution);
    }

    private Collection<ProgramState> determineFinalStates() {

        StateSpaceGenerator stateSpaceGenerator = setupStateSpaceGenerator(
               topLevelProgram,
               getInitialState(),
               topLevelExplorationStrategy
        );
        try {
            return stateSpaceGenerator
                    .generate()
                    .getFinalStates();
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Counterexample generation has been aborted.");
        }
    }

    private StateSpaceGenerator setupStateSpaceGenerator(Program program, ProgramState initialState,
                                                         ExplorationStrategy explorationStrategy) {

        return StateSpaceGenerator.builder()
                .setProgram(program)
                .addInitialState(initialState)
                .setMaterializationStrategy(materializationStrategy)
                .setExplorationStrategy(explorationStrategy)
                .setStateSpaceSupplier(getStateSpaceSupplier())
                .setBreadthFirstSearchEnabled(true)
                .setStateRefinementStrategy(stateRefinementStrategy)
                .setAbortStrategy(stateSpace -> {})
                .setCanonizationStrategy(new NoCanonicalizationStrategy())
                .setStateLabelingStrategy(state -> {})
                .setStateCounter(states -> {})
                .setPostProcessingStrategy(new NoPostProcessingStrategy())
                .build();
    }

    private ProgramState getInitialState() {

        HeapConfiguration initialHeap = trace.getInitialState().getHeap();
        HeapConfigurationPair initialPair = new HeapConfigurationPair(
                initialHeap,
                initialHeap
        );

        return trace
                .getInitialState()
                .shallowCopyWithUpdateHeap(initialPair);
    }

    private StateSpaceSupplier getStateSpaceSupplier() {

        return () -> {
            if(requiredFinalStatesStack.isEmpty()) {
                throw new IllegalStateException("Now required final states.");
            }
            return new CounterexampleStateSpace(requiredFinalStatesStack.pop());
        };
    }


    private FinalStatesComputer getFinalStatesComputer(Method method) {

        return (targetHeaps, programState) -> {

            Collection<ProgramState> targetStates = new LinkedHashSet<>(targetHeaps.size());
            for(HeapConfiguration hc : targetHeaps) {
                ProgramState targetState = programState.shallowCopyWithUpdateHeap(hc);
                targetState.setProgramCounter(-1);
                targetStates.add(targetState);
            }
            addPredicate(targetStates);

            try {
                return setupStateSpaceGenerator(
                        method.getBody(),
                        programState,
                        new TargetBasedExplorationStrategy(targetStates, stateSubsumptionStrategy)
                ).generate().getFinalStates();
            } catch (StateSpaceGenerationAbortedException e) {
                throw new IllegalStateException("Failed to execute method: " + method);
            }
        };
    }

    private void addPredicate(Collection<ProgramState> targetStates) {
        requiredFinalStatesStack.push(
                programState -> {
                    for(ProgramState requiredState: targetStates) {
                        if(stateSubsumptionStrategy.subsumes(programState, requiredState)) {
                            return true;
                        }
                    }
                    return false;
                }
        );
    }

    private ProgramState extractCounterexampleInput(ProgramState counterexampleState) {

        HeapConfigurationPair hcPair = (HeapConfigurationPair) counterexampleState.getHeap();
        return counterexampleState.shallowCopyWithUpdateHeap(
                hcPair.getPairedHeapConfiguration()
        );
    }

    public static class Builder {

        private CounterexampleGenerator generator = new CounterexampleGenerator();

        public CounterexampleGenerator build() {

            if(generator.trace == null) {
                throw new IllegalArgumentException("No trace has been provided.");
            }

            if(generator.availableMethods == null) {
                throw new IllegalArgumentException("No collection of available methods has been provided.");
            }

            if(generator.topLevelProgram == null) {
                throw new IllegalArgumentException("No program has been provided.");
            }

            if(generator.stateSubsumptionStrategy == null) {
                throw new IllegalArgumentException("No StateSubsumptionStrategy has been provided.");
            }

            if(generator.materializationStrategy == null) {
                throw new IllegalArgumentException("No MaterializationStrategy has been provided.");
            }

            if(generator.canonicalizationStrategy == null) {
                throw new IllegalArgumentException("No CanonicalizationStrategy has been provided.");
            }

            if(generator.stateRefinementStrategy == null) {
                throw new IllegalArgumentException("No StateRefinementStrategy has been provided.");
            }

            if(generator.scopeExtractorFactory == null) {
                throw new IllegalArgumentException("No ScopeExtractorFactory has been provided.");
            }

            CounterexampleGenerator result = generator;
            generator = null;
            return result;
        }

        public Builder setTrace(CounterexampleTrace trace) {

            generator.trace = trace;
            return this;
        }

        public Builder setAvailableMethods(Collection<Method> availableMethods) {

            generator.availableMethods = availableMethods;
            return this;
        }

        public Builder setProgram(Program program) {

            generator.topLevelProgram = program;
            return this;
        }

        public Builder setStateSubsumptionStrategy(StateSubsumptionStrategy stateSubsumptionStrategy) {

            generator.stateSubsumptionStrategy = stateSubsumptionStrategy;
            return this;
        }

        public Builder setMaterializationStrategy(MaterializationStrategy materializationStrategy) {

            generator.materializationStrategy = materializationStrategy;
            return this;
        }

        public Builder setCanonicalizationStrategy(CanonicalizationStrategy canonicalizationStrategy) {

            generator.canonicalizationStrategy = canonicalizationStrategy;
            return this;
        }

        public Builder setStateRefinementStrategy(StateRefinementStrategy strategy) {

            generator.stateRefinementStrategy = strategy;
            return this;
        }

        public Builder setScopeExtractorFactory(Function<Method, ScopeExtractor> scopeExtractorFactory) {

            generator.scopeExtractorFactory = scopeExtractorFactory;
            return this;
        }
    }

}
