package de.rwth.i2.attestor.strategies.stateRefinement;

import de.rwth.i2.attestor.refinement.BundledStateRefinementStrategy;
import de.rwth.i2.attestor.refinement.balanced.BalancednessStateRefinementStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class JsonStateRefinementStrategyParser {

    private static final Logger logger = LogManager.getLogger("JsonStateRefinementStrategyParser");

    private List<StateRefinementStrategy> strategies = new ArrayList<>();

    /**
     * The logger of this class.
     */
    public JsonStateRefinementStrategyParser(JSONArray jsonArray) {

        for(int i=0; i < jsonArray.length(); i++) {
            String name = jsonArray.getString(i);
            StateRefinementStrategy next = parseStrategy(name);
            if (next != null) {
                strategies.add(next);
            }
        }
    }

    public StateRefinementStrategy getStrategy() {

        if(strategies.isEmpty()) {
            return null;
        }

        return new BundledStateRefinementStrategy(strategies);
    }

    private StateRefinementStrategy parseStrategy(String name) {

        switch (name) {
            case "balance":
                return new BalancednessStateRefinementStrategy();
            default:
                logger.error("Unknown state refinement strategy '" + name + "' is ignored.");
                return null;
        }

    }
}
