package de.rwth.i2.attestor.domain;

import java.util.Set;

public interface RelativeIndexSupplier<T, I extends RelativeIndex<T>> {
    I get(T value, Set<Integer> variables);
}
