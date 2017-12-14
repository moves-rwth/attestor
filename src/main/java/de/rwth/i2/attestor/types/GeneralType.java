package de.rwth.i2.attestor.types;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.semantics.util.Constants;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple implementation of types.
 * For every name of types exactly one object is stored.
 *
 * @author Christoph
 */
public final class GeneralType implements Type {

    private final Map<SelectorLabel, String> selectorLabelNames = new LinkedHashMap<>();
    /**
     * The name of the type.
     */
    private String name;

    /**
     * @param name The name of the type to be created.
     */
    protected GeneralType(String name) {

        this.name = name;
    }

    /*
     * Shows only the last component of the type name for readability.
     * e.g. if the type is "de.rwth.i2.attestor.package.subpackage.List"
     * then the result will be "List"
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        String[] components = this.name.split("\\.");

        if (components.length > 0) {
            return components[components.length - 1];
        } else {
            return this.name;
            //return "";
        }

    }

    /**
     * Checks whether two types are equal.
     *
     * @param other Another type.
     * @return True if and only if two objects refer to the same type.
     */
    public boolean typeEquals(Object other) {

        return this == other;
    }

    @Override
    public boolean hasSelectorLabel(SelectorLabel selectorLabel) {

        return selectorLabelNames.containsKey(selectorLabel);
    }

    @Override
    public void addSelectorLabel(SelectorLabel selectorLabel, String defaultValue) {

        if (Types.isConstantType(this)) {
            throw new IllegalStateException("Cannot assign selector labels to node of constant type.");
        }
        selectorLabelNames.put(selectorLabel, defaultValue);
    }

    @Override
    public Map<SelectorLabel, String> getSelectorLabels() {

        return selectorLabelNames;
    }

    @Override
    public boolean isPrimitiveType(SelectorLabel selectorLabel) {

        String defaultValue = selectorLabelNames.get(selectorLabel);
        return defaultValue != null && defaultValue.equals(Constants.ZERO);
    }

    public static final class Factory {

        private final Map<String, Type> knownTypes = new LinkedHashMap<>();

        public Factory() {

            knownTypes.put(TypeNames.NULL, Types.NULL);
            knownTypes.put(TypeNames.UNDEFINED, Types.UNDEFINED);
            knownTypes.put(TypeNames.INT, Types.INT);
            knownTypes.put(TypeNames.BOOL, Types.BOOL);
            knownTypes.put(TypeNames.CHAR, Types.CHAR);
            knownTypes.put(TypeNames.BYTE, Types.BYTE);
            knownTypes.put(TypeNames.LONG, Types.LONG);
            knownTypes.put(TypeNames.DOUBLE, Types.DOUBLE);
            knownTypes.put(TypeNames.SHORT, Types.SHORT);
            knownTypes.put(TypeNames.STRING, Types.STRING);
        }

        public Type get(String name) {

            Type result = knownTypes.get(name);
            if (result == null) {
                result = new GeneralType(name);
                knownTypes.put(name, result);
            }
            return result;
        }
    }
}
