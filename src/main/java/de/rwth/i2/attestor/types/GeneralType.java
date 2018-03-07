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
     * True if and only if this type corresponds to a node of constant value.
     */
    private final boolean primitiveType;

    /**
     * @param name The name of the type to be created.
     */
    protected GeneralType(String name) {

        this(name, false);
    }

    protected GeneralType(String name, boolean primitiveType) {

        this.name = name;
        this.primitiveType = primitiveType;
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

        if (isPrimitiveType()) {
            throw new IllegalStateException("Cannot assign selector labels to node of constant type.");
        }
        selectorLabelNames.put(selectorLabel, defaultValue);
    }

    @Override
    public Map<SelectorLabel, String> getSelectorLabels() {

        return selectorLabelNames;
    }

    @Override
    public boolean isOptional(SelectorLabel selectorLabel) {

        String defaultValue = selectorLabelNames.get(selectorLabel);
        return defaultValue != null && defaultValue.equals(Constants.ZERO);
    }

    @Override
    public boolean isPrimitiveType() {

        return primitiveType;
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

            knownTypes.put(TypeNames.INT_0, Types.INT_0);
            knownTypes.put(TypeNames.INT_MINUS_1, Types.INT_MINUS_1);
            knownTypes.put(TypeNames.INT_PLUS_1, Types.INT_PLUS_1);
        }

        public Type get(String name) {

            Type result = knownTypes.computeIfAbsent(name, GeneralType::new);
            return result;
        }
    }
}
