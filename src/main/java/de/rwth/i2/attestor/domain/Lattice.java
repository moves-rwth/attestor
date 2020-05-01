package de.rwth.i2.attestor.domain;

import java.util.Set;

public interface Lattice<T> {
    T getLeastElement();
    T getLeastUpperBound(Set<T> elements);
    boolean isLessOrEqual(T e1, T e2);
}
