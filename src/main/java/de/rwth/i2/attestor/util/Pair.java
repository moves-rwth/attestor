package de.rwth.i2.attestor.util;

/**
 * A pair of two objects.
 *
 * @param <L> The type of the left element of the pair.
 * @param <R> The type of the right element of the pair.
 * @author Christoph
 */
public class Pair<L, R> {

    private final L first;
    private final R second;

    public Pair(L first, R second) {

        this.first = first;
        this.second = second;
    }

    public L first() {

        return first;
    }

    public R second() {

        return second;
    }

    @Override
    public boolean equals(Object other) {

        if (other instanceof Pair<?, ?>) {

            Pair<?, ?> pair = (Pair<?, ?>) other;
            return pair.first.equals(first)
                    && pair.second.equals(second);
        }
        return false;
    }

    @Override
    public int hashCode() {

        return first.hashCode() * second.hashCode() + second.hashCode() * first.hashCode();
    }

    public String toString() {

        return first.toString() + ", " + second.toString();
    }

}
