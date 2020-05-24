package de.rwth.i2.attestor.domain;

public interface Monoid<T> {
    T identity();

    T operate(T e1, T e2);
}
