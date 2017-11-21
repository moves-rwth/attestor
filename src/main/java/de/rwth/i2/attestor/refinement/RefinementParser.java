package de.rwth.i2.attestor.refinement;

import de.rwth.i2.attestor.refinement.product.ProductHeapAutomaton;
import de.rwth.i2.attestor.refinement.reachability.ReachabilityHeapAutomaton;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoStateRefinementStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class RefinementParser {

    private static final Logger logger = LogManager.getLogger("RefinementParser");

    private static final Pattern visitedByPattern = Pattern.compile("^visited\\(\\p{Alnum}+\\)$");
    private static final Pattern reachablePattern = Pattern.compile("^isReachable\\(\\p{Alnum}+,\\p{Alnum}+\\)$");
    private static final Pattern equalityPattern = Pattern.compile("^\\p{Alnum}+ \\=\\= \\p{Alnum}+$");
    private static final Pattern inequalityPattern = Pattern.compile("^\\p{Alnum}+ \\!\\= \\p{Alnum}+$");

    private final List<HeapAutomaton> heapAutomata = new ArrayList<>();
    private final List<StateRefinementStrategy> stateRefinements = new ArrayList<>();
    private boolean hasReachableAutomaton = false;

    public RefinementParser(Set<String> atomicPropositions) {

        for(String ap : atomicPropositions) {
            parseAtomicPropositions(ap);
        }
    }

    private void parseAtomicPropositions(String ap) {

        switch(ap) {
            case "tree":
            case "btree":
            case "sll":
            case "dll":
            case "identicNeighbours":
            case "visited":
                logger.warn("Atomic proposition '" + ap + "' is not supported yet.");
                break;
            default:
                parseParametrizedAp(ap);
                break;
        }
    }

    private void parseParametrizedAp(String ap) {

        if(visitedByPattern.matcher(ap).matches()) {
            //heapAutomata.add(new VisitedNodesAutomaton(ap));
            String varName = ap.split("[\\(\\)]")[1];
            logger.warn("Atomic proposition '" + ap + "' is not supported yet.");
            //stateRefinements.add(new VisitedVariableStateRefinementStrategy(varName));
            //logger.info("Enable heap automaton to track all nodes visited by variable '" + varName + "'.");
        } else if(!hasReachableAutomaton && reachablePattern.matcher(ap).matches()) {
            heapAutomata.add(new ReachabilityHeapAutomaton());
            hasReachableAutomaton = true;
            logger.debug("Enable heap automaton to track reachable variables");
        } else if(equalityPattern.matcher(ap).matches()) {
            logger.warn("Atomic proposition '" + ap + "' is not supported yet.");
        } else if(inequalityPattern.matcher(ap).matches()) {
            logger.warn("Atomic proposition '" + ap + "' is not supported yet.");
        } else {
            logger.warn("Unknown atomic proposition '" + ap + "'.");
        }
    }


    public StateRefinementStrategy getStateRefinementStrategy() {

        if(stateRefinements.isEmpty()) {
            return new NoStateRefinementStrategy();
        }
        return new BundledStateRefinementStrategy(stateRefinements);
    }

    public StateLabelingStrategy getStateLabelingStrategy() {

        HeapAutomaton automaton = getRefinementAutomaton();
        if(automaton == null) {
            return programState -> {};
        }
        return new AutomatonStateLabelingStrategy(automaton);
    }

    public HeapAutomaton getRefinementAutomaton() {

        int size = heapAutomata.size();
        if(size == 0) {
            return null;
        } else if(size == 1) {
            return heapAutomata.get(0);
        } else {
            HeapAutomaton[] ha = new HeapAutomaton[size];
            return new ProductHeapAutomaton(heapAutomata.toArray(ha));
        }
    }
}
