package de.rwth.i2.attestor.types;

import de.rwth.i2.attestor.semantics.util.Constants;

public final class TypeNames {

    public static final String NULL = "NULL";
    public static final String UNDEFINED = "undefined";
    public static final String INT = "int";
    public static final String BOOL = "bool";
    public static final String CHAR = "char";
    public static final String BYTE = "byte";
    public static final String LONG = "long";
    public static final String DOUBLE = "double";
    public static final String SHORT = "short";
    public static final String STRING = "String";

    public static final String INT_0 = "int_0";
    public static final String INT_PLUS_1 = "int_1";
    public static final String INT_MINUS_1 = "int_-1";

    public static String getDefaultValue(String typeName) {

        switch (typeName) {
            case INT:
            case CHAR:
            case BYTE:
            case LONG:
            case SHORT:
            case DOUBLE:
                return Constants.ZERO;
            case BOOL:
                return Constants.FALSE;
            default:
                return Constants.NULL;
        }

    }
}
