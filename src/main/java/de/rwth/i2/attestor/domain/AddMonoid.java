package de.rwth.i2.attestor.domain;

public interface AddMonoid<T> extends Monoid<T> {
    default T add(T e1, T e2) {
        return operate(e1, e2);
    }
}
