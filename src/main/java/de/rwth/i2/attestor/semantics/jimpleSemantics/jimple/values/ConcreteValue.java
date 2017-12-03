package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.types.Type;

/**
 * Concrete Values represent elements of the heap,
 * e.g. nodes in a hypergraph. They can be passed to
 * a JimpleExecutable for Operations such as assignVariable.
 * <p>
 * The Implementations of ConcreteValue and JimplesExecutable
 * have to match otherwise classCastExceptions will occur.
 *
 * @author Hannah Arndt
 */
public interface ConcreteValue {

    /**
     * @return The type (class) of this value.
     */
    Type type();

    /**
     * @param other Another value.
     * @return True if and only if both values are equal.
     */
    boolean equals(ConcreteValue other);

    /**
     * @return True if and only if this value is not defined.
     */
    boolean isUndefined();
}
