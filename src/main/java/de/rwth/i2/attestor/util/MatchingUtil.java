package de.rwth.i2.attestor.util;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Generic method to find all elements of a collection that satisfies a given predicate.
 *
 * @author Christoph, Hannah Arndt.
 */
public class MatchingUtil {

    public static <T> boolean containsMatch(Collection<T> selectorCollection, Predicate<T> matchingFunction) {

        for (T element : selectorCollection) {
            if (matchingFunction.test(element)) {
                return true;
            }
        }
        return false;
    }

}
