package de.rwth.i2.attestor.seplog;

import java.util.List;
import java.util.Set;

/**
 * An interface to deal with variable names in symbolic heaps, where
 * multiple variables may refer to the same location.
 *
 * @Christoph
 */
public interface VariableUnification {

    /**
     * @param variableName The name of a variable
     * @return The unique name used to identify all variable equal to variableName
     */
    String getUniqueName(String variableName);

    /**
     * @param variableName The name of a variable
     * @return The type underlying the variable.
     */
    String getType(String variableName);

    /**
     * @return A list of all unique variable names.
     */
    List<String> getUniqueVariableNames();

    /**
     * @return A list of all program variable names.
     */
    Set<String> getProgramVariableNames();
}
