package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.counterexamples.heapConfWithPartner.HeapConfigurationWithPartner;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import gnu.trove.iterator.TIntIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CounterexampleGenerator {

    private Program program;
    private Trace trace;
    private MaterializationStrategy materializationStrategy;
    private CanonicalizationStrategy canonicalizationStrategy;
    private StateRefinementStrategy stateRefinementStrategy;

    private ProgramState inputState;
    private List<ProgramState> traceStates;

    public static class CounterexampleGeneratorBuilder {

        private CounterexampleGenerator product = new CounterexampleGenerator();

        protected CounterexampleGeneratorBuilder() {
        }

        public CounterexampleGeneratorBuilder setProgram(Program program) {
            product.program = program;
            return this;
        }

        public CounterexampleGeneratorBuilder setTrace(Trace trace) {
            product.trace = trace;
            return this;
        }

        public CounterexampleGeneratorBuilder setMaterializationStrategy(MaterializationStrategy materializationStrategy) {
            product.materializationStrategy = materializationStrategy;
            return this;
        }

        public CounterexampleGeneratorBuilder setCanonicalizationStrategy(CanonicalizationStrategy canonicalizationStrategy) {
            product.canonicalizationStrategy = canonicalizationStrategy;
            return this;
        }

        public CounterexampleGeneratorBuilder setStateRefinementStrategy(StateRefinementStrategy stateRefinementStrategy) {
            product.stateRefinementStrategy = stateRefinementStrategy;
            return this;
        }

        public CounterexampleGenerator build() {

            if(product.program == null
                || product.trace == null
                || product.materializationStrategy == null
                || product.canonicalizationStrategy == null
                || product.stateRefinementStrategy == null) {
                throw new IllegalStateException("CounterexampleGenerator is incomplete.");
            }

            return product;
        }
    }

    public static CounterexampleGeneratorBuilder builder() {
        return new CounterexampleGeneratorBuilder();
    }

    public HeapConfiguration generate() throws SpuriousCounterexampleException {

        traceStates = trace.getTrace();
        assert(!traceStates.isEmpty());

        Iterator<ProgramState> traceIterator = traceStates.iterator();
        ProgramState initialState = traceIterator.next();

        HeapConfiguration initialHeap = initialState.getHeap();
        HeapConfigurationWithPartner combinedHeap = new HeapConfigurationWithPartner(initialHeap, initialHeap);
        ProgramState currentState = initialState.shallowCopyWithUpdateHeap(combinedHeap);

        while(traceIterator.hasNext()) {
            ProgramState expectedTraceElement = traceIterator.next();
            currentState = performStep(currentState, expectedTraceElement);
        }

        HeapConfiguration failureInput = ((HeapConfigurationWithPartner) currentState.getHeap()).getPartner();

        return failureInput;
    }

    private ProgramState performStep(ProgramState currentState, ProgramState traceElement)
            throws SpuriousCounterexampleException {

        Semantics semantics = program.getStatement(currentState.getProgramCounter());
        List<ProgramState> materializedStates = materializationStrategy
                .materialize(currentState, semantics.getPotentialViolationPoints());

        if(materializedStates.isEmpty()) {
            materializedStates.add(currentState);
        }

        assert(materializedStates.size() > 0);

        try {
            for(ProgramState mState : materializedStates) {
                Set<ProgramState> successorStates = semantics.computeSuccessors(mState, null); // TODO
                if(successorStates.size() != 1) {
                    throw new SpuriousCounterexampleException("Nondeterminism could not be resolved.");
                }
                ProgramState succState = successorStates.iterator().next();
                if(isSubsumedByTraceState(succState, traceElement, semantics)) {
                    return succState;
                }
            }
        } catch (NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e) {
        }
        throw new SpuriousCounterexampleException("Could not find covered materialized state.");
    }

    private boolean isSubsumedByTraceState(ProgramState succState, ProgramState traceElement, Semantics semantics) {

        ProgramState copy = succState.clone();
        HeapConfiguration succHeap = copy.getHeap();
        HeapConfiguration traceHeap = traceElement.getHeap();

        TIntIterator iter = succState.getHeap().variableEdges().iterator();
        while(iter.hasNext())  {
            int varEdge = iter.next();
            String name = succHeap.nameOf(varEdge);
            if(traceHeap.variableWith(name) == HeapConfiguration.INVALID_ELEMENT) {
                succHeap.builder().removeVariableEdge(varEdge).build();
            }
        }

        // avoid useless abstraction steps if both graphs are already isomorphic
        if(copy.equals(traceElement)) {
            return true;
        }

        ProgramState abstractState = canonicalizationStrategy.canonicalize(semantics, copy);
        return abstractState.equals(traceElement);
    }
}

