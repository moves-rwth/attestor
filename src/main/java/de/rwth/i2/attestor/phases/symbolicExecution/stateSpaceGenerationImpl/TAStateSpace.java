package de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl;

import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.HeapTransformation;
import de.rwth.i2.attestor.graph.heap.internal.TAHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.IsomorphismChecker;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Deque;

public class TAStateSpace extends InternalStateSpace {
    private final Map<Pair<Integer, Integer>, Deque<HeapTransformation>> transformations = new HashMap<>();
    private final Map<Pair<Integer, Integer>, Matching> mergers = new HashMap<>();

    public TAStateSpace(int capacity) {
        super(capacity);
    }

    @Override
    public void addMaterializationTransition(ProgramState from, ProgramState to) {
        super.addMaterializationTransition(from, to);
        saveTransformationQueue(from, to);
        saveMerger(from, to);
    }

    @Override
    public void addControlFlowTransition(ProgramState from, ProgramState to) {
        super.addControlFlowTransition(from, to);
        saveTransformationQueue(from, to);
        saveMerger(from, to);
    }

    public Deque<HeapTransformation> getTransformationQueue(int from, int to) {
        return new LinkedList<>(transformations.get(new Pair<>(from, to)));
    }

    public Matching getMerger(int from, int to) {
        return mergers.get(new Pair<>(from, to));
    }

    private void saveTransformationQueue(ProgramState from, ProgramState to) {

        if (!(to.getHeap() instanceof TAHeapConfiguration)) {
            throw new IllegalArgumentException("TAStateSpace only supports TAHeapConfigurations");
        }

        TAHeapConfiguration heap = (TAHeapConfiguration) to.getHeap();
        Deque<HeapTransformation> copy = new LinkedList<>(heap.transformationQueue);
        heap.transformationQueue.clear();
        transformations.put(new Pair<>(from.getStateSpaceId(), to.getStateSpaceId()), copy);
    }

    private void saveMerger(ProgramState from, ProgramState to) {
        if (getState(to.getStateSpaceId()) != to) {
            ProgramState old = getState(to.getStateSpaceId());
            Matching matching = new IsomorphismChecker(to.getHeap(), old.getHeap()).getMatching();
            mergers.put(new Pair<>(from.getStateSpaceId(), to.getStateSpaceId()), matching);
        }
    }
}
