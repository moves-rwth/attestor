package de.rwth.i2.attestor.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RelativeIndex<T> {
    private static final Set<Integer> reservedVariables = new HashSet<>();

    final T concrete;
    final Set<Integer> variables = new HashSet<>();

    public static <T> RelativeIndex<T> getVariable() {
        return new RelativeIndex<>();
    }

    public RelativeIndex(T concrete) {
        this.concrete = concrete;
    }

    private RelativeIndex() {
        this.concrete = null;
        int id = reservedVariables.isEmpty() ? 0 : Collections.max(reservedVariables) + 1;
        reservedVariables.add(id);
        variables.add(id);
    }

    RelativeIndex(T concrete, Set<Integer> variables) {
        this.concrete = concrete;
        if (reservedVariables.containsAll(variables)) {
            this.variables.addAll(variables);
        } else {
            throw new IllegalArgumentException("Constructing a relative index using unreserved variables is not allowed");
        }
    }

    public boolean isConcrete() {
        return variables.isEmpty();
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
        return "{" + "concrete=" + concrete + ", variables=" + variables + '}';
    }
}
