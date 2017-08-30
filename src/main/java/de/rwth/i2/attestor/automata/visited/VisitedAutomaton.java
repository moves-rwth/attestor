package de.rwth.i2.attestor.automata.visited;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.HeapAutomaton;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class VisitedAutomaton extends HeapAutomaton {

    @Override
    protected VisitedState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        int rank = heapConfiguration.countExternalNodes();
        BitSet visited = new BitSet(rank + 1);
        visited.set(rank, true);

        TIntSet visitedByNt = computeNodesVisitedByNonterminals(visited, ntAssignment, heapConfiguration);

        updateVisited(visited, visitedByNt, heapConfiguration);

        return new VisitedState(visited);
    }

    private TIntSet computeNodesVisitedByNonterminals(BitSet visited, List<AutomatonState> ntAssignment,
                                                      HeapConfiguration heapConfiguration) {

        int rank = heapConfiguration.countExternalNodes();
        TIntSet visitedByNt = new TIntHashSet();
        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        for (int i = 0; i < ntEdges.size(); i++) {
            int edge = ntEdges.get(i);
            VisitedState vState = (VisitedState) ntAssignment.get(i);

            if (!vState.isInternalVisited()) {
                visited.set(rank, false);
            }

            TIntArrayList attached = heapConfiguration.attachedNodesOf(edge);
            for (int j = 0; j < attached.size(); j++) {
                int node = attached.get(j);
                if (vState.isExternalIndex(j) && vState.isExternalVisited(j)) {
                    visitedByNt.add(node);
                }
            }
        }

        return visitedByNt;
    }

    private void updateVisited(BitSet visited, TIntSet visitedByNt, HeapConfiguration heapConfiguration) {

        int rank = heapConfiguration.countExternalNodes();
        TIntIterator iter = heapConfiguration.nodes().iterator();
        while (iter.hasNext()) {
            int node = iter.next();
            int extIndex = heapConfiguration.externalIndexOf(node);
            boolean isVisited = VisitedTypes.isVisited(heapConfiguration.nodeTypeOf(node)) || visitedByNt.contains(node);
            if (extIndex == HeapConfiguration.INVALID_ELEMENT) {
                visited.set(rank, isVisited && visited.get(rank));
            } else {
                visited.set(extIndex, isVisited);
            }
        }
    }
}

class VisitedState implements AutomatonState {

        private BitSet visited;

        VisitedState(BitSet visited) {
            this.visited = visited;
        }

        boolean isExternalVisited(int index) {

            if(isExternalIndex(index)) {
                return visited.get(index);
            }
            throw new IndexOutOfBoundsException();
        }

        boolean isExternalIndex(int index) {
            return index < rank();
        }

        int rank() {
            return visited.size()-1;
        }

        boolean isInternalVisited() {

            return visited.get(rank());
        }

        @Override
        public boolean equals(Object otherObject) {

            if(otherObject == this) {
                return true;
            }

            if(otherObject == null) {
                return false;
            }

            if(otherObject.getClass() != VisitedState.class) {
                return false;
            }

            VisitedState other = (VisitedState) otherObject;
            return visited.equals( other.visited );
        }

        @Override
        public int hashCode() {

            return visited.hashCode();
        }


        @Override
        public boolean isFinal() {

            return visited.length() == visited.nextClearBit(0);
        }

        @Override
        public Set<String> getAtomicPropositions() {

            if(isFinal()) {
                return Collections.singleton("fully visited");
            } else {
                return Collections.singleton("not fully visited");
            }
        }
}
