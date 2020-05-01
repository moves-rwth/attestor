package de.rwth.i2.attestor.domain;

public interface PartialOrder<T> {
    boolean isLessOrEqual(T e1, T e2);
}
