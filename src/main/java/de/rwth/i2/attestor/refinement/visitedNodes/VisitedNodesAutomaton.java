package de.rwth.i2.attestor.refinement.visitedNodes;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.HeapAutomatonState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;

public final class VisitedNodesAutomaton implements HeapAutomaton {

    @Override
    public VisitedNodesAutomatonState transition(HeapConfiguration heapConfiguration,
                                                 List<HeapAutomatonState> statesOfNonterminals) {

        TIntSet nodesVisitedThroughNts = computeNodesVisitedThroughNts(heapConfiguration, statesOfNonterminals);

        boolean allInternalsVisited = true;
        BitSet externalNodesVisited = new BitSet(heapConfiguration.countExternalNodes());

        TIntIterator iter = heapConfiguration.nodes().iterator();
        while(iter.hasNext()) {
            int node = iter.next();
            int extIndex = heapConfiguration.externalIndexOf(node);
            boolean isVisited = VisitedTypeHelper.isVisited(heapConfiguration.nodeTypeOf(node))
                    && nodesVisitedThroughNts.contains(node);
            if(extIndex != HeapConfiguration.INVALID_ELEMENT) {
                externalNodesVisited.set(extIndex, isVisited);
            } else {
                allInternalsVisited &= isVisited;
            }
        }

        // TODO we should move to an error state if there is a mix of internal nodes (possibly hidden inside of
        // TODO hyperedges) that are visited and not visited.

        return new VisitedNodesAutomatonState(externalNodesVisited, allInternalsVisited);
    }

    private TIntSet computeNodesVisitedThroughNts(HeapConfiguration heapConfiguration,
                                                  List<HeapAutomatonState> statesOfNonterminals) {

        TIntSet result = new TIntHashSet(statesOfNonterminals.size());
        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        for(int i=0; i < ntEdges.size(); i++) {
            int edge = ntEdges.get(i);
            VisitedNodesAutomatonState state = (VisitedNodesAutomatonState) statesOfNonterminals.get(i);
            TIntArrayList att = heapConfiguration.attachedNodesOf(edge);
            for(int j=0; j < att.size(); j++) {
                if(state.hasVisitedExternal(j)) {
                    result.add(att.get(j));
                }
            }
        }
        return result;
    }

    @Override
    public boolean isInitialState(HeapAutomatonState heapAutomatonState) {

        if(heapAutomatonState == null || heapAutomatonState.getClass() != VisitedNodesAutomatonState.class) {
            return false;
        }

        VisitedNodesAutomatonState state = (VisitedNodesAutomatonState) heapAutomatonState;

        if(state.isVisitedInternals()) {
            return false;
        }

        for(int i=0; i < state.rank(); i++) {
            if(state.hasVisitedExternal(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<HeapConfiguration> getPossibleHeapRewritings(HeapConfiguration heapConfiguration) {

        List<HeapConfiguration> result = new ArrayList<>();
        BitSet set = new BitSet(heapConfiguration.countExternalNodes()+1);
        while(set.nextClearBit(0) != set.length()) {

            int bit = set.nextClearBit(0);
            set.set(bit, true);
            set.clear(0, bit-1);
            result.add( rewriteHeap(heapConfiguration, set) );
        }
        return result;
    }

    private HeapConfiguration rewriteHeap(HeapConfiguration heapConfiguration, BitSet visitedSet) {

       boolean visitedInternals = visitedSet.get(visitedSet.length()-1);
       HeapConfigurationBuilder builder = heapConfiguration.builder();
       if(visitedInternals) {
           TIntIterator nodeIter = heapConfiguration.nodes().iterator();
           while(nodeIter.hasNext()) {
               int node = nodeIter.next();
               int extIndex = heapConfiguration.externalIndexOf(node);
               if(extIndex == HeapConfiguration.INVALID_ELEMENT
                       || visitedSet.get(extIndex)) {
                   Type type = VisitedTypeHelper.getVisitedType(heapConfiguration.nodeTypeOf(node));
                   builder.replaceNodeType(node, type);
               }
           }
       } else {
           TIntArrayList extNodes = heapConfiguration.externalNodes();
           for(int i=0; i < extNodes.size(); i++) {
               if(visitedSet.get(i)) {
                   int node = extNodes.get(i);
                   Type type = VisitedTypeHelper.getVisitedType(heapConfiguration.nodeTypeOf(node));
                   builder.replaceNodeType(node, type);
               }
           }
       }

       return builder.build();
    }
}

final class VisitedNodesAutomatonState extends HeapAutomatonState {

    private final BitSet visitedExternals;
    private boolean visitedInternals;

    VisitedNodesAutomatonState(BitSet visitedExternals, boolean visitedInternals) {

        this.visitedExternals = visitedExternals;
        this.visitedInternals = visitedInternals;
    }

    public int rank() {

        return visitedExternals.length();
    }

    boolean hasVisitedExternal(int i) {

        return visitedExternals.get(i);
    }

    boolean isVisitedInternals() {

        return visitedInternals;
    }

    @Override
    public Set<String> toAtomicPropositions() {

        if(visitedInternals && allExternalsVisited()) {
            return Collections.singleton("visited");
        }
        return Collections.singleton("not visited");
    }

    private boolean allExternalsVisited() {

        for(int i=0; i < rank(); i++) {
            if(!visitedExternals.get(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object otherObject) {

        if(otherObject == this) {
            return true;
        }

        if(otherObject == null) {
            return false;
        }

        if(otherObject.getClass() != VisitedNodesAutomatonState.class) {
            return false;
        }

        VisitedNodesAutomatonState other = (VisitedNodesAutomatonState) otherObject;
        return visitedInternals == other.visitedInternals
                && visitedExternals.equals(other.visitedExternals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitedInternals, visitedExternals);
    }
}
