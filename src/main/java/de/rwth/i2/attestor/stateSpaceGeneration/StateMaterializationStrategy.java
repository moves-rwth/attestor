package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class StateMaterializationStrategy {

    private MaterializationStrategy heapStrategy;

    public StateMaterializationStrategy(MaterializationStrategy heapStrategy) {

        this.heapStrategy = heapStrategy;
    }

    public MaterializationStrategy getHeapStrategy() {
        return heapStrategy;
    }

    public Collection<ProgramState> materialize(ProgramState state, ViolationPoints violationPoints) {

        Collection<HeapConfiguration> materializedHeaps = heapStrategy.materialize(state.getHeap(), violationPoints);

        return new Collection<ProgramState>() {
            @Override
            public int size() {
                return materializedHeaps.size();
            }

            @Override
            public boolean isEmpty() {
                return materializedHeaps.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return materializedHeaps.contains(o);
            }

            @Override
            public Iterator<ProgramState> iterator() {

                Iterator<HeapConfiguration> iterator = materializedHeaps.iterator();

                return new Iterator<ProgramState>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public ProgramState next() {
                        return state.shallowCopyWithUpdateHeap(iterator.next());
                    }
                };
            }

            @Override
            public ProgramState[] toArray() {

                ProgramState states[] = new ProgramState[materializedHeaps.size()];
                int i=0;
                for(HeapConfiguration hc : materializedHeaps) {
                    states[i] = state.shallowCopyWithUpdateHeap(hc);
                    ++i;
                }
                return states;
            }

            @Override
            public <T> T[] toArray(T[] ts) {
                assert false;
                return null;
            }

            @Override
            public boolean add(ProgramState state) {
                assert false;
                return false;
            }

            @Override
            public boolean remove(Object o) {
                assert false;
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {

                List<ProgramState> states = new ArrayList<>();
                for(HeapConfiguration hc : materializedHeaps) {
                    states.add(
                            state.shallowCopyWithUpdateHeap(hc)
                    );
                }
                return states.containsAll(collection);
            }

            @Override
            public boolean addAll(Collection<? extends ProgramState> collection) {
                assert false;
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                assert false;
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                assert false;
                return false;
            }

            @Override
            public void clear() {
                assert false;
            }
        };
    }
}
