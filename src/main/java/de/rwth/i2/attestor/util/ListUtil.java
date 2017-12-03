package de.rwth.i2.attestor.util;

import java.util.List;

/**
 * Compares lists that are interpreted as multi-sets.
 *
 * @author Christoph
 */
public class ListUtil {

    /**
     * Checks whether two given lists that are interpreted as multi-sets contain the same elements.
     * The provided lists may be altered during this check.
     *
     * @param left  A list.
     * @param right Another list.
     * @return True if and only if both lists contain equal elements including repetitions.
     */
    public static boolean isEqualAsMultiset(List<?> left, List<?> right) {

        for (Object e : left) {

            if (right.contains(e)) {
                right.remove(e);
            } else {
                return false;
            }
        }

        return right.isEmpty();
    }

    /**
     * Checks whether all elements of a list are contained in another list if both lists are interpreted as multi-sets.
     * The provided lists may be altered during this check.
     * That is, all repetitions of the left list have also to be repetitions in the right list.
     *
     * @param left  A list.
     * @param right Another list.
     * @return True if and only if all elements of left, including repetitions, are equal to an element of right.
     */
    public static boolean isSubsetAsMultiset(List<?> left, List<?> right) {

        for (Object e : left) {

            if (right.contains(e)) {
                right.remove(e);
            } else {
                return false;
            }
        }

        return true;
    }
}
