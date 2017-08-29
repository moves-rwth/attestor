package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.strategies.stateRefinement.BalancednessStateRefinementStrategy;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestAVLAnnotationMaintaining {


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}


	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		IndexedState input = new IndexedState( ExampleIndexedGraphFactory.getInput_AnnotationMaintaining() );
		IndexedState expected = new IndexedState(ExampleIndexedGraphFactory.getExpected_AnnotationMaintaining());

		BalancednessStateRefinementStrategy strategy = new BalancednessStateRefinementStrategy();
		strategy.refine(input);

		assertEquals(expected, input);
	}

}
