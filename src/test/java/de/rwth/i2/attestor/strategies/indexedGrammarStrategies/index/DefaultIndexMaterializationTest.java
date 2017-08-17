package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.DefaultStackMaterialization;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.IndexSymbol;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultIndexMaterializationTest {
	
	private DefaultStackMaterialization stackGrammar = new DefaultStackMaterialization();

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}


	@Test
	public void test_X_to_sX() {
		final IndexSymbol symbol_X = DefaultStackMaterialization.SYMBOL_X;
		final IndexSymbol symbol_s = DefaultStackMaterialization.SYMBOL_s;
		assertTrue( stackGrammar.canCreateSymbolFor(symbol_X, symbol_s) );
		assertThat( stackGrammar.getRuleCreatingSymbolFor(symbol_X, symbol_s),
				contains( symbol_s, symbol_X ) );
	}
	
	@Test
	public void test_X_to_Z() {
		final IndexSymbol symbol_X = DefaultStackMaterialization.SYMBOL_X;
		final IndexSymbol symbol_Z = DefaultStackMaterialization.SYMBOL_Z;
		assertTrue( stackGrammar.canCreateSymbolFor(symbol_X, symbol_Z) );
		assertThat( stackGrammar.getRuleCreatingSymbolFor(symbol_X, symbol_Z),
				contains( symbol_Z ) );
	}
	
	@Test
	public void test_Y_to_sY() {
		final IndexSymbol symbol_Y = DefaultStackMaterialization.SYMBOL_Y;
		final IndexSymbol symbol_s = DefaultStackMaterialization.SYMBOL_s;
		assertTrue( stackGrammar.canCreateSymbolFor(symbol_Y, symbol_s) );
		assertThat( stackGrammar.getRuleCreatingSymbolFor(symbol_Y, symbol_s),
				contains( symbol_s, symbol_Y ) );
	}
	
	
	@Test
	public void test_Y_to_C() {
		final IndexSymbol symbol_Y = DefaultStackMaterialization.SYMBOL_Y;
		final IndexSymbol symbol_C = DefaultStackMaterialization.SYMBOL_C;
		assertTrue( stackGrammar.canCreateSymbolFor(symbol_Y, symbol_C) );
		assertThat( stackGrammar.getRuleCreatingSymbolFor(symbol_Y, symbol_C),
				contains( symbol_C ) );
	}
	
	@Test
	public void test_Y_to_Z() {
		final IndexSymbol symbol_Y = DefaultStackMaterialization.SYMBOL_Y;
		final IndexSymbol symbol_Z = DefaultStackMaterialization.SYMBOL_Z;
		assertFalse( stackGrammar.canCreateSymbolFor(symbol_Y, symbol_Z) );
	}
	
	@Test
	public void test_X_to_C() {
		final IndexSymbol symbol_X = DefaultStackMaterialization.SYMBOL_X;
		final IndexSymbol symbol_C = DefaultStackMaterialization.SYMBOL_C;
		assertFalse( stackGrammar.canCreateSymbolFor(symbol_X, symbol_C) );
	}
}
