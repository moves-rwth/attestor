package de.rwth.i2.attestor.domain;

import java.util.Set;

public interface Lattice<T> extends PartialOrder<T> {
    T getLeastElement();
    T getLeastUpperBound(Set<T> elements);
}
