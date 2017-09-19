package de.rwth.i2.attestor.refinement.visitedNodes;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.HeapAutomatonState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.BitSequence;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;

import static de.rwth.i2.attestor.refinement.visitedNodes.VisitedStatus.*;


enum VisitedStatus {
    EMPTY,
    ALL_VISITED,
    ALL_NOT_VISITED,
    ERROR,
}

public final class VisitedNodesAutomaton implements HeapAutomaton {

    final String atomicPropositionName;

    public VisitedNodesAutomaton() {

        atomicPropositionName = "visited";
    }

    public VisitedNodesAutomaton(String atomicPropositionsName) {

        this.atomicPropositionName = atomicPropositionsName;
    }

    @Override
    public VisitedNodesAutomatonState transition(HeapConfiguration heapConfiguration,
                                                 List<HeapAutomatonState> statesOfNonterminals) {

        BitSet externalNodesVisited = new BitSet(heapConfiguration.countExternalNodes());
        TIntSet nodesVisitedThroughNts = new TIntHashSet();
        VisitedStatus internalNodesStatus = checkNonterminals(heapConfiguration,
                statesOfNonterminals, nodesVisitedThroughNts);

        TIntIterator iter = heapConfiguration.nodes().iterator();
        while(iter.hasNext()) {
            int node = iter.next();
            int extIndex = heapConfiguration.externalIndexOf(node);
            boolean isVisited = VisitedTypeHelper.isVisited(heapConfiguration.nodeTypeOf(node))
                    || nodesVisitedThroughNts.contains(node);
            if(extIndex != HeapConfiguration.INVALID_ELEMENT) {
                externalNodesVisited.set(extIndex, isVisited);
            } else {
                if(isVisited) {
                    internalNodesStatus = updateVisited(internalNodesStatus, ALL_VISITED);
                } else {
                    internalNodesStatus = updateVisited(internalNodesStatus, ALL_NOT_VISITED);
                }
                if(internalNodesStatus == ERROR) {
                    return VisitedNodesAutomatonState.ERROR_STATE;
                }
            }
        }

        return new VisitedNodesAutomatonState(heapConfiguration.countExternalNodes(),
                externalNodesVisited, internalNodesStatus, atomicPropositionName);
    }

    private VisitedStatus checkNonterminals(HeapConfiguration heapConfiguration,
                                      List<HeapAutomatonState> statesOfNonterminals,
                                      TIntSet nodesVisitedThroughNts) {

        VisitedStatus result = EMPTY;

        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        for(int i=0; i < ntEdges.size(); i++) {
            int edge = ntEdges.get(i);
            VisitedNodesAutomatonState state = (VisitedNodesAutomatonState) statesOfNonterminals.get(i);
            result = updateVisited(result, state.getInternalNodesStatus());
            TIntArrayList att = heapConfiguration.attachedNodesOf(edge);
            for(int j=0; j < att.size(); j++) {
                if(state.hasVisitedExternal(j)) {
                    nodesVisitedThroughNts.add(att.get(j));
                }
            }
        }

        return result;
    }

    private VisitedStatus updateVisited(VisitedStatus allVisited, VisitedStatus nodeVisited) {

        switch (allVisited) {
            case ALL_VISITED:
                switch(nodeVisited) {
                    case EMPTY:
                    case ALL_VISITED:
                        return ALL_VISITED;
                    default:
                        return ERROR;
                }
            case ALL_NOT_VISITED:
                switch(nodeVisited) {
                    case EMPTY:
                    case ALL_NOT_VISITED:
                        return ALL_NOT_VISITED;
                    default:
                        return ERROR;
                }
            case EMPTY:
                return nodeVisited;
            default:
                return ERROR;
        }
    }

    @Override
    public boolean isInitialState(HeapAutomatonState heapAutomatonState) {

        if(heapAutomatonState == null || heapAutomatonState.getClass() != VisitedNodesAutomatonState.class) {
            return false;
        }

        VisitedNodesAutomatonState state = (VisitedNodesAutomatonState) heapAutomatonState;

        if(state.getInternalNodesStatus() != ALL_NOT_VISITED && state.getInternalNodesStatus() != EMPTY) {
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

        for(BitSequence possibleVisits : new BitSequence(heapConfiguration.countExternalNodes()+1)) {
            result.add(rewriteHeap(heapConfiguration, possibleVisits));
        }
        return result;
    }

    private HeapConfiguration rewriteHeap(HeapConfiguration heapConfiguration, BitSequence possibleVisits) {

        HeapConfigurationBuilder builder = heapConfiguration.clone().builder();
        boolean visitedInternals = possibleVisits.isSet(heapConfiguration.countExternalNodes());
        TIntIterator nodeIter = heapConfiguration.nodes().iterator();
        while(nodeIter.hasNext()) {
            int node = nodeIter.next();
            int extIndex = heapConfiguration.externalIndexOf(node);
            if( (visitedInternals && extIndex == HeapConfiguration.INVALID_ELEMENT)
                || possibleVisits.isSet(extIndex) ) {
                Type type = VisitedTypeHelper.getVisitedType(heapConfiguration.nodeTypeOf(node));
                builder.replaceNodeType(node, type);
            }
        }
       return builder.build();
    }
}


final class VisitedNodesAutomatonState extends HeapAutomatonState {

    private static final Set<String> AP_DEFAULT = Collections.emptySet();

    static final VisitedNodesAutomatonState ERROR_STATE = new VisitedNodesAutomatonState(0,null, ERROR, "");

    private final String AP_VISITED;
    private final int rank;
    private final BitSet visitedExternals;
    private VisitedStatus visitedInternals;

    VisitedNodesAutomatonState(int rank, BitSet visitedExternals, VisitedStatus visitedInternals, String AP_VISITED) {

        this.rank = rank;
        this.visitedExternals = visitedExternals;
        this.visitedInternals = visitedInternals;
        this.AP_VISITED = AP_VISITED;
    }

    @Override
    public boolean isError() {
        return visitedInternals == ERROR;
    }

    boolean hasVisitedExternal(int i) {

        if(i >= rank) {
            return false;
        }

        return visitedExternals.get(i);
    }

    int rank() {
        return  rank;
    }

    VisitedStatus getInternalNodesStatus() {

        return visitedInternals;
    }

    @Override
    public Set<String> toAtomicPropositions() {

        if((visitedInternals == ALL_VISITED || visitedInternals == EMPTY) && allExternalsVisited()) {
            return Collections.singleton(AP_VISITED);
        }
        return AP_DEFAULT;
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
                && rank == other.rank
                && visitedExternals.equals(other.visitedExternals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitedInternals, visitedExternals);
    }

    @Override
    public String toString() {
        return visitedInternals + ":" + visitedExternals;
    }
}
