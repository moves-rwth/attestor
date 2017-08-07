package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.automata.BalancedTreeAutomaton;
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

		BalancedTreeAutomaton automaton = new BalancedTreeAutomaton();
		automaton.move(input.getHeap());

		assertEquals(expected, input);
	}

}
