package de.rwth.i2.attestor.semantics.util;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;

public class ConstantsTest {

    @Test
    public void testIsConstant() {

        assert (Constants.isConstant(Constants.NULL));
        assert (Constants.isConstant("null"));
        assertFalse(Constants.isConstant("x"));
        assert (Constants.isConstant(Constants.FALSE));
        assert (Constants.isConstant(Constants.TRUE));
        assert (Constants.isConstant(Constants.ONE));
        assert (Constants.isConstant(Constants.ZERO));
        assert (Constants.isConstant(Constants.MINUS_ONE));
    }
}
