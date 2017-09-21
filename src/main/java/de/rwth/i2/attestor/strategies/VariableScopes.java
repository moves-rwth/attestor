package de.rwth.i2.attestor.strategies;

import de.rwth.i2.attestor.markings.Markings;
import de.rwth.i2.attestor.semantics.util.Constants;

import java.util.regex.Pattern;

public class VariableScopes {

    private static final String SCOPE_DELIMITER = "-";

    private static final Pattern scopedPattern = Pattern.compile("^[\\p{Digit}]+\\-[\\p{Alnum}\\$]+");

    public static String getScopedName(String name, int scopeDepth) {

        if(isScoped(name)) {
            return scopeDepth + SCOPE_DELIMITER + getName(name);
        }

        return scopeDepth + SCOPE_DELIMITER + name;
    }

    public static boolean isScoped(String name) {

        return scopedPattern.matcher(name).matches();
    }

    public static boolean isScopeable(String name) {

        return !(Constants.isConstant(name) || Markings.isMarking(name) || name.startsWith("@"));
    }

    public static int getScope(String scopedName) {

        try {
            String[] scope = scopedName.split(SCOPE_DELIMITER);
            if (scope.length > 1) {
                return Integer.valueOf(scope[0]);
            }
        } catch(NumberFormatException e) {
            // intended
        }

        return -1;
    }

    public static boolean hasScope(String scopedName, int scope) {

        String[] split = scopedName.split(SCOPE_DELIMITER);
        if (split .length > 1) {
            return split[0].equals(String.valueOf(scope));
        }
        return false;
    }

    public static String getName(String scopedName) {

        if(!isScoped(scopedName)) {
            return scopedName;
        }

        String[] scope = scopedName.split(SCOPE_DELIMITER);
        if (scope.length > 1) {
            return scope[1];
        } else {
            return "";
        }
    }
}
