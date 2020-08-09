package de.rwth.i2.attestor.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RelativeIndex<T> {
    private final T concrete;
    private final Set<Integer> variables = new HashSet<>();

    protected RelativeIndex(T concrete, Set<Integer> variables) {
        this.concrete = concrete;
        this.variables.addAll(variables);
    }

    public boolean isRelative() {
        return !variables.isEmpty();
    }

    public T getConcrete() {
        return concrete;
    }

    public Set<Integer> getVariables() {
        return Collections.unmodifiableSet(variables);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RelativeIndex)) {
            return false;
        }

        RelativeIndex<?> other = (RelativeIndex<?>) obj;

        if (this.concrete == null || other.concrete == null) {
            if (this.concrete != other.concrete) {
                return false;
            }
        }

        return this.concrete.equals(other.concrete) && this.variables.equals(other.variables);
    }

    @Override
    public String toString() {
        return "[" + "concrete value = " + concrete +
                ", variables = " + variables + ']';
    }
}
