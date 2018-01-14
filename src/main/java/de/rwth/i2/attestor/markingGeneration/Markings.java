package de.rwth.i2.attestor.markingGeneration;

public class Markings {

    public static final String MARKING_SEPARATOR = "-";
    public static final String MARKING_PREFIX = "%";


    public static boolean isMarking(String variableName) {

        return variableName.startsWith(MARKING_PREFIX);
    }

    public static boolean isComposedMarking(String variableName) {

        return isMarking(variableName)
                && variableName.contains(MARKING_SEPARATOR);
    }
}
