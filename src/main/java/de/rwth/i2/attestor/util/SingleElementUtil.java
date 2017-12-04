package de.rwth.i2.attestor.util;

import java.util.*;


/**
 * Auxiliary methods to create collections that contain a single element.
 *
 * @author Christoph, Hannah Arndt.
 */
public class SingleElementUtil {

    public static <E> Set<E> createSet(E initialElement) {

        Set<E> res = new HashSet<>();
        res.add(initialElement);
        return res;
    }

    public static <K, V> Map<K, V> createMap(K initialKey, V initialValue) {

        Map<K, V> res = new HashMap<>();
        res.put(initialKey, initialValue);
        return res;
    }

    public static <E> List<E> createList(E initialElement) {

        List<E> res = new ArrayList<>();
        res.add(initialElement);
        return res;
    }

}
