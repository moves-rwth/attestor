package de.rwth.i2.attestor.graph.heap;

import de.rwth.i2.attestor.graph.digraph.NodeLabel;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for a String such that Strings can be used as {@link NodeLabel} in
 * {@link de.rwth.i2.attestor.graph.digraph.LabeledDigraph}.
 *
 * @author Christoph
 */
public class Variable implements NodeLabel {

    /**
     * Caches all already created Variables together with their wrapped strings.
     */
    private static final Map<String, Variable> existingLabels = new HashMap<>();
    /**
     * The String that is wrapped by this Variable.
     */
    private final String name;

    /**
     * @param name The string to be wrapped.
     */
    private Variable(String name) {

        this.name = name;
    }

    /**
     * Provides a Variable wrapping the provided name.
     *
     * @param name The String to be wrapped.
     * @return The Variable object wrapping name.
     */
    public static synchronized Variable get(String name) {

        if (!existingLabels.containsKey(name)) {
            existingLabels.put(name, new Variable(name));
        }
        return existingLabels.get(name);
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
