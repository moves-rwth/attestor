package de.rwth.i2.attestor.automata.visited;

import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;

/**
 *
 * Utility class to check whether a type corresponds to a visited node.
 *
 * @author Christoph
 */
public class VisitedTypes {

    private static final String VISITED_PREFIX = "!";

    /**
     * Checks whether a type is marked as visited.
     * @param type The type to check.
     * @return True if and only if the type is marked as visited.
     */
    public static boolean isVisited(Type type) {

        return type != null && type.toString().startsWith(VISITED_PREFIX);
    }

    /**
     * Constructs a type that is equal to the given one except that it is marked as visited.
     * @param type A type.
     * @return The corresponding visited type.
     */
    public static Type getVisited(Type type) {

        if(isVisited(type)) {
            return type;
        }

        return Settings.getInstance().factory().getType(VISITED_PREFIX + type.toString());
    }
}
