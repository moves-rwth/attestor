package de.rwth.i2.attestor.automata;

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
        switch (automatonType) {
            case "reach":
                return new ReachabilityAutomaton();
            case "balance":
                return new BalancedTreeAutomaton();
            default:
                logger.error("Unknown automaton '" + automatonType + "' is ignored.");
                return null;
        }

    }
}
