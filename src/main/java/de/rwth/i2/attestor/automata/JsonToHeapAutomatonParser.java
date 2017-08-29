package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.main.settings.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

public class JsonToHeapAutomatonParser {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LogManager.getLogger( "JsonToHeapAutomatonParser" );

    private HeapAutomaton heapAutomaton;

    public JsonToHeapAutomatonParser(JSONArray automataArray) {

        heapAutomaton = null;

        for(int i=0; i < automataArray.length(); i++) {
            String automatonName = automataArray.getString(i);
            HeapAutomaton nextAutomaton = parseAutomaton(automatonName);
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

    private HeapAutomaton parseAutomaton(String automatonName) {

        switch (automatonName) {
            case "reach":
                return new ReachabilityAutomaton();
            case "points-to":
                return new PointsToAutomaton( () -> Settings.getInstance().factory().createEmptyHeapConfiguration() );
            default:
                logger.error("Unknown automaton '" + automatonName + "' is ignored.");
                return null;
        }

    }
}
