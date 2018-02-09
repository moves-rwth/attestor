package de.rwth.i2.attestor.phases.modelChecking.modelChecker;

public enum ModelCheckingResult {

    SATISFIED,
    UNSATISFIED,
    UNKNOWN;

    public static String getString(ModelCheckingResult result) {

        switch (result) {
            case SATISFIED:
                return "LTL-SAT";
            case UNSATISFIED:
                return "LTL-UNSAT";
            case UNKNOWN:
                return "LTL-UNKNOWN";
            default:
                    return "";
        }
    }
}
