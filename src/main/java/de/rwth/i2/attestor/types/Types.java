package de.rwth.i2.attestor.types;

public final class Types {

    public final static Type NULL = new GeneralType(TypeNames.NULL);
    public final static Type UNDEFINED = new GeneralType(TypeNames.UNDEFINED);

    public final static Type INT = new GeneralType(TypeNames.INT);
    public static final Type BOOL = new GeneralType(TypeNames.BOOL);
    public static final Type CHAR = new GeneralType(TypeNames.CHAR);
    public static final Type BYTE = new GeneralType(TypeNames.BYTE);
    public static final Type LONG = new GeneralType(TypeNames.LONG);
    public static final Type DOUBLE = new GeneralType(TypeNames.DOUBLE);
    public static final Type SHORT = new GeneralType(TypeNames.SHORT);
    public static final Type STRING = new GeneralType(TypeNames.STRING);


    public static boolean isConstantType(Type type) {

        return NULL.equals(type)
                || UNDEFINED.equals(type);
    }
}
