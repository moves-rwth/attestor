package de.rwth.i2.attestor.refinement;

import de.rwth.i2.attestor.refinement.pointsTo.PointsToHeapAutomaton;
import de.rwth.i2.attestor.refinement.product.ProductHeapAutomaton;
import de.rwth.i2.attestor.refinement.reachability.ReachabilityHeapAutomaton;
import de.rwth.i2.attestor.refinement.visitedNodes.VisitedNodesAutomaton;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class JsonToStateLabelingStrategies {

    private static final Logger logger = LogManager.getLogger("JsonToStateLabelingStrategies");

    private HeapAutomaton heapAutomaton;
    private StateLabelingStrategy stateLabelingStrategy;

    public JsonToStateLabelingStrategies(JSONArray jsonArray) {

        List<HeapAutomaton> automata = new ArrayList<>();
        for(int i=0; i < jsonArray.length(); i++) {
            String automatonName = jsonArray.getString(i);
            HeapAutomaton automaton = parseAutomaton(automatonName);
            if(automaton != null) {
                automata.add(automaton);
            }
        }

        if(automata.isEmpty()) {
            heapAutomaton = null;
            stateLabelingStrategy = s -> {};
        } else if(automata.size() == 1) {
            heapAutomaton = automata.get(0);
            stateLabelingStrategy = new AutomatonStateLabelingStrategy(heapAutomaton);
        } else {
            HeapAutomaton[] automataArray = new HeapAutomaton[automata.size()];
            heapAutomaton = new ProductHeapAutomaton(automata.toArray(automataArray));
            stateLabelingStrategy = new AutomatonStateLabelingStrategy(heapAutomaton);
        }
    }

    private HeapAutomaton parseAutomaton(String automatonName) {

        switch(automatonName) {
            case "reach":
                return new ReachabilityHeapAutomaton();
            case "points-to":
                return new PointsToHeapAutomaton();
            case "visited":
                return new VisitedNodesAutomaton();
            default:
                logger.warn("Skipping unknown heap automaton '" + automatonName + "'.");
                return null;

        }
    }

    public StateLabelingStrategy getStateLabelingStrategy() {

        return stateLabelingStrategy;
    }

    public HeapAutomaton getHeapAutomaton() {

        return heapAutomaton;
    }
}
