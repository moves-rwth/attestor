package de.rwth.i2.attestor.graph.heap;

import de.rwth.i2.attestor.graph.digraph.NodeLabel;


/**
 * Wrapper for a String such that Strings can be used as {@link NodeLabel} in
 * {@link de.rwth.i2.attestor.graph.digraph.LabeledDigraph}.
 *
 * @author Christoph
 */
public class Variable implements NodeLabel {

    /**
     * The String that is wrapped by this Variable.
     */
    private final String name;

    /**
     * @param name The string to be wrapped.
     */
    public Variable(String name) {

        this.name = name;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Variable other = (Variable) obj;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }

    /**
     * @return The String wrapped by this Variable.
     */
    public String getName() {

        return this.name;
    }

    /**
     * @see Variable#getName()
     */
    public String toString() {

        return this.name;
    }


}
