package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.ContractMatch;
import de.rwth.i2.attestor.procedures.ScopeExtractor;
import de.rwth.i2.attestor.procedures.ScopedHeap;

import java.util.ArrayList;
import java.util.Collection;

public class MockupScopeExtractor implements ScopeExtractor {

    private HeapConfiguration fullHeap;
    private ScopedHeap scopedHeap;

    public MockupScopeExtractor(MockupHeaps mockupHeaps) {

        fullHeap = mockupHeaps.getHeap();

        scopedHeap = new ScopedHeap() {
            @Override
            public HeapConfiguration getHeapInScope() {
                return mockupHeaps.getHeapInScope();
            }

            @Override
            public HeapConfiguration getHeapOutsideScope() {
                return mockupHeaps.getHeapOutsideScope();
            }

            @Override
            public Collection<HeapConfiguration> merge(ContractMatch contractMatch) {

                Collection<HeapConfiguration> result = new ArrayList<>();
                for (HeapConfiguration post : contractMatch.getPostconditions()) {
                    result.add(
                            mockupHeaps.getHeapOutsideScope()
                                    .builder()
                                    .replaceNonterminalEdge(mockupHeaps.getPlaceholderEdge(), post)
                                    .build()
                    );
                }
                return result;
            }
        };
    }

    @Override
    public ScopedHeap extractScope(HeapConfiguration heapConfiguration) {

        assert fullHeap.equals(heapConfiguration);
        return scopedHeap;
    }
}
