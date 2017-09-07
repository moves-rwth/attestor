package de.rwth.i2.attestor.refinement;

import de.rwth.i2.attestor.refinement.visitedNodes.VisitedStateRefinementStrategy;
import de.rwth.i2.attestor.refinement.visitedNodes.VisitedVariableStateRefinementStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import de.rwth.i2.attestor.refinement.balanced.BalancednessStateRefinementStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class JsonToStateRefinementStrategies {

    private static final Logger logger = LogManager.getLogger("JsonToStateRefinementStrategies");

    private StateRefinementStrategy stateRefinementStrategy;

    public JsonToStateRefinementStrategies(JSONArray jsonArray) {

        if(jsonArray.length() == 1) {
            String name = jsonArray.getString(0);
            stateRefinementStrategy = parseStrategy(name);
            if(stateRefinementStrategy == null) {
                stateRefinementStrategy = state -> state;
            }
            return;
        }

        if(jsonArray.length() == 0) {
            stateRefinementStrategy = state -> state;
            return;
        }

        List<StateRefinementStrategy> strategies = new ArrayList<>(jsonArray.length());
        for(int i=0; i < jsonArray.length(); i++) {

            String name = jsonArray.getString(i);
            StateRefinementStrategy s = parseStrategy(name);
            if(s != null) {
                strategies.add(s);
            }
        }

        stateRefinementStrategy = new BundledStateRefinementStrategy(strategies);
    }

    private StateRefinementStrategy parseStrategy(String name) {

        switch(name) {
            case "balance":
                return new BalancednessStateRefinementStrategy();
            case "visited":
                return new VisitedStateRefinementStrategy();
            default:
                if(name.startsWith("visitedBy(") && name.endsWith(")")) {
                    String varName = name.split("[\\(\\)]")[1];
                    return new VisitedVariableStateRefinementStrategy(varName);
                } else {
                    logger.warn("Skipping unknown state refinement strategy '" + name + "'");
                    return null;

                }
        }
    }

    public StateRefinementStrategy getStateRefinementStrategy() {

        return stateRefinementStrategy;
    }

}
