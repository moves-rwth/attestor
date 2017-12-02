package de.rwth.i2.attestor.markings;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.main.settings.Settings;

import java.util.ArrayList;
import java.util.List;

import static de.rwth.i2.attestor.markings.Markings.MARKING_PREFIX;

/**
 * A marking specifies a single variable that should traverse all unfolded HeapConfigurations according to a grammar
 * together with a (sub)set of selectors that should additionally be marked.
 *
 * @author Christoph
 */
public class Marking {

    private final String markingName;
    private List<SelectorLabel> requiredSelectorLabels;
    private boolean markAllSuccessors = false;

    /**
     * Specifies a new Marking
     * @param markingName The name of the universally quantified variable that should traverse every node.
     * @param requiredSelectors The (sub)set of selectors that should additionally be marked.
     */
    public Marking(String markingName, SelectorLabel... requiredSelectors) {

        this.markingName = markingName;

        requiredSelectorLabels = new ArrayList<>(requiredSelectors.length);

        for(SelectorLabel label : requiredSelectors) {
            requiredSelectorLabels.add(label);
        }

    }

    /**
     * Specifies a new Marking
     * @param markingName The name of the universally quantified variable that should traverse every node.
     * @param markAllSuccessors True iff all successors should be marked without checking for the existence of
     *                          specific ones.
     */
    public Marking(String markingName, boolean markAllSuccessors) {

        this.markingName = markingName;
        this.markAllSuccessors = markAllSuccessors;
    }

    /**
     * @return The full variable name used to mark the currently visited node.
     */
    public String getUniversalVariableName() {

        return MARKING_PREFIX + markingName;
    }

    /**
     * @return A list of all selector labels that have to be marked.
     */
    public List<SelectorLabel> getRequiredSelectors() {

        return requiredSelectorLabels;
    }

    /**
     * @return True iff all selectors should be marked without checking for the existence of specific ones.
     */
    public boolean isMarkAllSuccessors() {
        return markAllSuccessors;
    }

    /**
     * @param selector The label of a selector label.
     * @return The variable name used to mark a selector with the given label.
     */
    public String getSelectorVariableName(String selector) {

        return MARKING_PREFIX + markingName + "." + selector;
    }

    public String extractSelectorName(String selectorVariableName) {

        String[] split = selectorVariableName.split(".");
        if(split.length == 2) {
            return split[1];
        }
        return null;
    }
}
