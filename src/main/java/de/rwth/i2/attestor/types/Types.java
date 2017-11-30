package de.rwth.i2.attestor.types;

public final class Types {

    public final static Type NULL = new GeneralType(TypeNames.NULL);
    public final static Type UNDEFINED = new GeneralType(TypeNames.UNDEFINED);

    public static boolean isConstantType(Type type) {
        return NULL.equals(type)
                || UNDEFINED.equals(type);
    }
}
