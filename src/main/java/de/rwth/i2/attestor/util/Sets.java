package de.rwth.i2.attestor.util;

import java.util.*;

// https://stackoverflow.com/questions/1670862/obtaining-a-powerset-of-a-set-in-java
public class Sets {
    public static <T> Set<Set<T>> powerSet(Set<T> set) {
        Set<Set<T>> sets = new HashSet<>();

        if (set.isEmpty()) {
            sets.add(Collections.emptySet());
            return sets;
        }

        List<T> list = new ArrayList<>(set);
        T head = list.get(0);
        Set<T> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<T> s : powerSet(rest)) {
            Set<T> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(s);
            sets.add(newSet);
            sets.add(s);
        }
        return sets;
    }
}
