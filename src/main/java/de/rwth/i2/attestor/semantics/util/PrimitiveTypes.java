package de.rwth.i2.attestor.semantics.util;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

public final class PrimitiveTypes {

    public static final String INT_TYPE = "int" ;
    public static final String BOOL_TYPE = "bool" ;
    public static final String CHAR_TYPE = "char" ;
    public static final String BYTE_TYPE = "byte" ;
    public static final String LONG_TYPE = "long" ;
    public static final String DOUBLE_TYPE = "double" ;
    public static final String SHORT_TYPE = "short" ;
    public static final String STRING_TYPE = "String" ;


    public static boolean isPrimitiveType(String typeName) {

        switch(typeName) {
            case INT_TYPE:
            case CHAR_TYPE:
            case BYTE_TYPE:
            case LONG_TYPE:
            case SHORT_TYPE:
            case DOUBLE_TYPE:
            case BOOL_TYPE:
            case STRING_TYPE:
                return true;
        }
        return false;
    }

    public static String getDefaultValue(String typeName) {

        switch(typeName) {
            case INT_TYPE:
            case CHAR_TYPE:
            case BYTE_TYPE:
            case LONG_TYPE:
            case SHORT_TYPE:
            case DOUBLE_TYPE:
                return Constants.ZERO;
            case BOOL_TYPE:
                return Constants.FALSE;
            default:
                return Constants.NULL;
        }

    }
}
