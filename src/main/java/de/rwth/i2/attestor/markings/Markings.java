package de.rwth.i2.attestor.markings;

public class Markings {

    public static final String MARKING_PREFIX = "%";

    public static boolean isMarking(String name) {

        return name.startsWith(MARKING_PREFIX);
    }
}
