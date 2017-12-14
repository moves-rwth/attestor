package de.rwth.i2.attestor.semantics.util;

public final class Constants {

    public static final String NULL = "null";
    public static final String ONE = "1";
    public static final String ZERO = "0";
    public static final String MINUS_ONE = "-1";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static boolean isConstant(String name) {

        switch (name) {
            case NULL:
            case ONE:
            case ZERO:
            case MINUS_ONE:
            case TRUE:
            case FALSE:
                return true;
            default:
                return false;
        }
    }
}
