package de.rwth.i2.attestor.semantics.util;

public final class PrimitiveTypes {

    public static boolean isPrimitiveType(String typeName) {

        switch(typeName) {
            case "int":
            case "boolean":
            case "char":
            case "byte":
            case "long":
            case "short":
            case "double":
            case "String":
                return true;
        }
        return false;
    }
}
