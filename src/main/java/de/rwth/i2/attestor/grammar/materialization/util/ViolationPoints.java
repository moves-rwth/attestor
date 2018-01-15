package de.rwth.i2.attestor.grammar.materialization.util;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A set of violation points.
 * Violation points are pairs of variables and selectors that indicate a missing selector
 * that has to be obtained through materialization.
 *
 * @author Christoph
 */
public class ViolationPoints {

    /**
     * A dedicated object that denotes an empty set of violation points to avoid
     * useless object creation.
     */
    private final static ViolationPoints EMPTY_VIOLATION_POINTS = new ViolationPoints();
    /**
     * The internal representation of violation points as a mapping from
     * variable names to a set of selectors. Hence, every (key,value) pair
     * is a violation point.
     */
    private final Map<String, Set<String>> variablesWithFields;

    /**
     * Creates a new empty set of violation points.
     */
    public ViolationPoints() {

        variablesWithFields = new LinkedHashMap<>();

    }

    /**
     * Creates a set of violation points that containsSubsumingState a single violation point.
     *
     * @param variable The name of the variable that makes up a violation point.
     * @param selector The name of the selector that makes up a violation point.
     */
    public ViolationPoints(String variable, String selector) {

        variablesWithFields = new LinkedHashMap<>();
        add(variable, selector);
    }

    /**
     * @return An empty set of violation points.
     */
    public static ViolationPoints getEmptyViolationPoints() {

        return EMPTY_VIOLATION_POINTS;
    }

    /**
     * Adds another violation point.
     *
     * @param variable The name of the variable that makes up a violation point.
     * @param selector The name of the selector that makes up a violation point.
     */
    public void add(String variable, String selector) {

        if (!variablesWithFields.containsKey(variable)) {

            variablesWithFields.put(variable, new LinkedHashSet<>());
        }

        variablesWithFields.get(variable).add(selector);
    }

    /**
     * Add all violation points to this set of violation points.
     *
     * @param vio Another set of violation points.
     */
    public void addAll(ViolationPoints vio) {

        if (vio.variablesWithFields.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Set<String>> entry : vio.variablesWithFields.entrySet()) {

            if (!variablesWithFields.containsKey(entry.getKey())) {

                variablesWithFields.put(entry.getKey(), entry.getValue());
            } else {

                variablesWithFields.get(entry.getKey()).addAll(entry.getValue());
            }
        }
    }

    /**
     * @param variable The name of a variable.
     * @return The selectors of all violation points that contain the given variable name.
     */
    public Set<String> getSelectorsOf(String variable) {

        return variablesWithFields.get(variable);
    }

    /**
     * @return The names of all variables that belong to some violation point.
     */
    public Set<String> getVariables() {

        return variablesWithFields.keySet();
    }

    /**
     * @return A String representation of this set of violation points.
     */
    public String toString() {

        return variablesWithFields.toString();
    }

}
