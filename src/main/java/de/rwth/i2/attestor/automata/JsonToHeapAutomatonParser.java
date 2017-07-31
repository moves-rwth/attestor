package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.automata.composition.IntersectionAutomaton;
import de.rwth.i2.attestor.automata.implementations.balancedness.BalancedTreeAutomaton;
import de.rwth.i2.attestor.automata.implementations.reachability.ReachabilityHeapAutomaton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;

public class JsonToHeapAutomatonParser {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LogManager.getLogger( "JsonToHeapAutomatonParser" );

    private HeapAutomaton heapAutomaton;

    public JsonToHeapAutomatonParser(JSONArray automataArray) throws FileNotFoundException {

        heapAutomaton = null;

        for(int i=0; i < automataArray.length(); i++) {
            JSONObject jsonObject = automataArray.getJSONObject(i);
            HeapAutomaton nextAutomaton = parseAutomaton(jsonObject);
            if(nextAutomaton != null) {
                if (heapAutomaton == null) {
                    heapAutomaton = nextAutomaton;
                } else {
                    heapAutomaton = new IntersectionAutomaton(heapAutomaton, nextAutomaton);
                }
            }
        }
    }

    public HeapAutomaton getHeapAutomaton() {

        return heapAutomaton;
    }

    private HeapAutomaton parseAutomaton(JSONObject jsonObject) {

        String automatonType = jsonObject.getString("automaton");
        if (automatonType.equals("reach")) {
            return parseReachabilityAutomaton(jsonObject);
        } else if(automatonType.equals("balance")) {
            return parseBalanceAutomaton(jsonObject);
        } else {
            logger.error("Unknown automaton '" + automatonType + "' is ignored.");
            return null;
        }

    }

    private HeapAutomaton parseReachabilityAutomaton(JSONObject jsonObject) {

        if (!jsonObject.has("source")) {
            logger.error("Reachability automaton requires the name of a source variable.");
        }
        if (!jsonObject.has("target")) {
            logger.error("Reachability automaton requires the name of a target variable.");
        }
        String source = jsonObject.getString("source");
        String target = jsonObject.getString("target");
        return new ReachabilityHeapAutomaton(source, target);
    }

    private HeapAutomaton parseBalanceAutomaton(JSONObject jsonObject) {

        return new BalancedTreeAutomaton();
    }

}
