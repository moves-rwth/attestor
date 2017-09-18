package de.rwth.i2.attestor.markings;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.main.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class Marking {

    private static final String MARKING_PREFIX = "%";

    private String markingName;
    private List<SelectorLabel> requiredSelectorLabels;

    public Marking(String markingName, String... requiredSelectors) {

        this.markingName = markingName;

        requiredSelectorLabels = new ArrayList<>(requiredSelectors.length);

        for(String s : requiredSelectors) {
            SelectorLabel label = Settings.getInstance().factory().getSelectorLabel(s);
            requiredSelectorLabels.add(label);
        }

    }

    public String getUniversalVariableName() {

        return MARKING_PREFIX + markingName;
    }

    public List<SelectorLabel> getRequiredSelectors() {

        return requiredSelectorLabels;
    }

    public String getSelectorVariableName(String selector) {

        return MARKING_PREFIX + markingName + "." + selector;
    }
}
