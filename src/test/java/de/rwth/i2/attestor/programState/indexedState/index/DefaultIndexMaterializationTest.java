package de.rwth.i2.attestor.programState.indexedState.index;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultIndexMaterializationTest {

    private DefaultIndexMaterialization indexGrammar = new DefaultIndexMaterialization();

    @Test
    public void test_X_to_sX() {

        final IndexSymbol symbol_X = DefaultIndexMaterialization.SYMBOL_X;
        final IndexSymbol symbol_s = DefaultIndexMaterialization.SYMBOL_s;
        assertTrue(indexGrammar.canCreateSymbolFor(symbol_X, symbol_s));
        assertThat(indexGrammar.getRuleCreatingSymbolFor(symbol_X, symbol_s),
                contains(symbol_s, symbol_X));
    }

    @Test
    public void test_X_to_Z() {

        final IndexSymbol symbol_X = DefaultIndexMaterialization.SYMBOL_X;
        final IndexSymbol symbol_Z = DefaultIndexMaterialization.SYMBOL_Z;
        assertTrue(indexGrammar.canCreateSymbolFor(symbol_X, symbol_Z));
        assertThat(indexGrammar.getRuleCreatingSymbolFor(symbol_X, symbol_Z),
                contains(symbol_Z));
    }

    @Test
    public void test_Y_to_sY() {

        final IndexSymbol symbol_Y = DefaultIndexMaterialization.SYMBOL_Y;
        final IndexSymbol symbol_s = DefaultIndexMaterialization.SYMBOL_s;
        assertTrue(indexGrammar.canCreateSymbolFor(symbol_Y, symbol_s));
        assertThat(indexGrammar.getRuleCreatingSymbolFor(symbol_Y, symbol_s),
                contains(symbol_s, symbol_Y));
    }


    @Test
    public void test_Y_to_C() {

        final IndexSymbol symbol_Y = DefaultIndexMaterialization.SYMBOL_Y;
        final IndexSymbol symbol_C = DefaultIndexMaterialization.SYMBOL_C;
        assertTrue(indexGrammar.canCreateSymbolFor(symbol_Y, symbol_C));
        assertThat(indexGrammar.getRuleCreatingSymbolFor(symbol_Y, symbol_C),
                contains(symbol_C));
    }

    @Test
    public void test_Y_to_Z() {

        final IndexSymbol symbol_Y = DefaultIndexMaterialization.SYMBOL_Y;
        final IndexSymbol symbol_Z = DefaultIndexMaterialization.SYMBOL_Z;
        assertFalse(indexGrammar.canCreateSymbolFor(symbol_Y, symbol_Z));
    }

    @Test
    public void test_X_to_C() {

        final IndexSymbol symbol_X = DefaultIndexMaterialization.SYMBOL_X;
        final IndexSymbol symbol_C = DefaultIndexMaterialization.SYMBOL_C;
        assertFalse(indexGrammar.canCreateSymbolFor(symbol_X, symbol_C));
    }
}
