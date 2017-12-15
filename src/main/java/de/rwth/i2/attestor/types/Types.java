package de.rwth.i2.attestor.types;

public final class Types {

    public final static Type NULL = new GeneralType(TypeNames.NULL, true);
    public final static Type UNDEFINED = new GeneralType(TypeNames.UNDEFINED, true);

    public final static Type INT = new GeneralType(TypeNames.INT, true);
    public static final Type BOOL = new GeneralType(TypeNames.BOOL, true);
    public static final Type CHAR = new GeneralType(TypeNames.CHAR, true);
    public static final Type BYTE = new GeneralType(TypeNames.BYTE, true);
    public static final Type LONG = new GeneralType(TypeNames.LONG, true);
    public static final Type DOUBLE = new GeneralType(TypeNames.DOUBLE, true);
    public static final Type SHORT = new GeneralType(TypeNames.SHORT, true);
    public static final Type STRING = new GeneralType(TypeNames.STRING, true);

    public final static Type INT_0 = new GeneralType(TypeNames.INT_0, true);
    public final static Type INT_PLUS_1 = new GeneralType(TypeNames.INT_MINUS_1, true);
    public final static Type INT_MINUS_1 = new GeneralType(TypeNames.INT_PLUS_1, true);

    public static boolean isConstantType(Type type) {
        return type == BOOL
                || type == NULL
                || type == INT_0
                || type == INT_PLUS_1
                || type == INT_MINUS_1;
    }
}
