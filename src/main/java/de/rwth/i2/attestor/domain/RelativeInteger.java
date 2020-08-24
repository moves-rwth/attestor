package de.rwth.i2.attestor.domain;

import java.util.Set;

public final class RelativeInteger extends RelativeIndex<AugmentedInteger> {
    public static final RelativeIntegerOp opSet = new RelativeIntegerOp();

    protected RelativeInteger(AugmentedInteger concrete, Set<Integer> variables) {
        super(concrete, variables);
    }

    public static RelativeInteger get(int value) {
        return opSet.getFromConcrete(new AugmentedInteger(value));
    }

    public static RelativeInteger getWithAllVariables(int value) {
        return opSet.geFromConcreteWithAllVariables(new AugmentedInteger(value));
    }
}
