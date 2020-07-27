package de.rwth.i2.attestor.domain;

import java.util.Set;
import java.util.function.Supplier;

public interface RelativeIndexSupplier<T, I extends RelativeIndex<T>> extends Supplier<I> {
    I get();

    I get(T value);

    I get(T value, Set<Integer> variables);
}
