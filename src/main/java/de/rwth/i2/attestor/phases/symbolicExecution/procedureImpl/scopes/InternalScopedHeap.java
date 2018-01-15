package de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.ContractMatch;
import de.rwth.i2.attestor.procedures.ScopedHeap;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Collection;

public class InternalScopedHeap implements ScopedHeap {

    private final HeapConfiguration heapInScope;
    private final HeapConfiguration heapOutsideScope;
    private final int edgeToMergeScopes;

    public InternalScopedHeap(HeapConfiguration heapInScope, HeapConfiguration heapOutsideScope,
                              int edgeToMergeScopes) {

        this.heapInScope = heapInScope;
        this.heapOutsideScope = heapOutsideScope;
        this.edgeToMergeScopes = edgeToMergeScopes;
    }

    @Override
    public HeapConfiguration getHeapInScope() {
        return heapInScope;
    }

    @Override
    public HeapConfiguration getHeapOutsideScope() {
        return heapOutsideScope;
    }

    @Override
    public Collection<HeapConfiguration> merge(ContractMatch contractMatch) {

        assert contractMatch.hasMatch();
        HeapConfiguration reorderedHeapInScope = reorder(contractMatch.getExternalReordering());
        Collection<HeapConfiguration> result = new ArrayList<>(contractMatch.getPostconditions().size());
        for(HeapConfiguration post : contractMatch.getPostconditions()) {
           HeapConfiguration mergedHeap = reorderedHeapInScope.clone()
                   .builder()
                   .replaceNonterminalEdge(edgeToMergeScopes, post)
                   .build();
           result.add(mergedHeap);
        }
        return result;
    }

    public HeapConfiguration reorder(int[] externalReordering) {

        HeapConfiguration heap = heapOutsideScope.clone();
        TIntArrayList oldTentacles = heap.attachedNodesOf(edgeToMergeScopes);
        TIntArrayList newTentacles = new TIntArrayList();
        for (int aReordering : externalReordering) {
            newTentacles.add(oldTentacles.get(aReordering));
        }

        Nonterminal label = heap.labelOf(edgeToMergeScopes);
        return heap.builder().removeNonterminalEdge(edgeToMergeScopes)
                .addNonterminalEdge(label, newTentacles).build();
    }
}
