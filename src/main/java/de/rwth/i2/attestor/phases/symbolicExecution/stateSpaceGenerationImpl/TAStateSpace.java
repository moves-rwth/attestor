package de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl;

import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.HeapTransformation;
import de.rwth.i2.attestor.graph.heap.internal.TAHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.IsomorphismChecker;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.util.Pair;

import java.util.*;

public class TAStateSpace extends InternalStateSpace {
    private final Map<Pair<ProgramState, ProgramState>, Queue<HeapTransformation>> transformations = new HashMap<>();

    public TAStateSpace(int capacity) {
        super(capacity);
    }

    @Override
    public boolean addStateIfAbsent(ProgramState state) {
        if (!super.addStateIfAbsent(state)) {
            if (!(state.getHeap() instanceof TAHeapConfiguration)) {
                throw new IllegalArgumentException("TAStateSpace only supports TAHeapConfigurations");
            }

            TAHeapConfiguration heap = (TAHeapConfiguration) state.getHeap();
            ProgramState old = getState(state.getStateSpaceId());
            Matching matching = new IsomorphismChecker(heap, old.getHeap()).getMatching();

            heap.transformationBuffer.get(heap.transformationBuffer.size() - 1).merge(matching);
            return false;
        }

        return true;
    }

    @Override
    public void addMaterializationTransition(ProgramState from, ProgramState to) {
        super.addMaterializationTransition(from, to);

        saveTransformationBuffer(from, to);
    }

    @Override
    public void addControlFlowTransition(ProgramState from, ProgramState to) {
        super.addControlFlowTransition(from, to);

        saveTransformationBuffer(from, to);
    }

    public Queue<HeapTransformation> getTransformationBuffer(ProgramState from, ProgramState to) {
        return new LinkedList<>(transformations.get(new Pair<>(from, to)));
    }

    private void saveTransformationBuffer(ProgramState from, ProgramState to) {

        if (!(to.getHeap() instanceof TAHeapConfiguration)) {
            throw new IllegalArgumentException("TAStateSpace only supports TAHeapConfigurations");
        }

        TAHeapConfiguration heap = (TAHeapConfiguration) to.getHeap();
        Queue<HeapTransformation> copy = new LinkedList<>(heap.transformationBuffer);
        heap.transformationBuffer.clear();
        transformations.put(new Pair<>(from, to), copy);
    }
}
