package de.rwth.i2.attestor.seplog;

import de.rwth.i2.attestor.types.TypeNames;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class VariableEquivalenceClass {

    String representative;
    private String type;
    private boolean hasConstantType;
    private Set<String> variableNames = new HashSet<>();

    public VariableEquivalenceClass(String constantName) {

        representative = constantName;
        variableNames.add(representative);
        hasConstantType = true;

        switch (constantName) {
            case "null":
                type = TypeNames.NULL;
                break;
            case "-1":
                type = TypeNames.INT_MINUS_1;
                break;
            case "0":
                type = TypeNames.INT_0;
                break;
            case "1":
                type = TypeNames.INT_PLUS_1;
                break;
            default:
                throw new IllegalArgumentException("Unsupported constant '" + constantName + "'");
        }
    }

    public VariableEquivalenceClass(String variableName, String type) {

        representative = variableName;
        variableNames.add(representative);
        this.type = type;
        hasConstantType = false;
    }


    String getType() {

        return type;
    }

    String getRepresentative() {

        return representative;
    }

    boolean contains(@Nonnull String name) {

        return variableNames.contains(name);
    }

    @Override
    public boolean equals(Object o) {

        if(o == this) {
            return true;
        }
        if(o == null) {
            return false;
        }
        if(o.getClass() != VariableEquivalenceClass.class) {
            return false;
        }
        VariableEquivalenceClass other = (VariableEquivalenceClass) o;
        return variableNames.equals(other.variableNames);
    }

    @Override
    public int hashCode() {

        return variableNames.hashCode();
    }

    void merge(VariableEquivalenceClass rhs) {

        if(rhs.hasConstantType) {
            if (hasConstantType) {
                assert type.equals(rhs.type);
            } else {
                type = rhs.type;
                hasConstantType = true;
            }
        } else if(!hasConstantType) {
            assert type.equals(rhs.type);
        }

        variableNames.addAll(rhs.variableNames);
    }
}
