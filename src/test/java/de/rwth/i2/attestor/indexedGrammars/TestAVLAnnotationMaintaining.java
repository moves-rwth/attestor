package de.rwth.i2.attestor.indexedGrammars;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestAVLAnnotationMaintaining {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		IndexedState input = new IndexedState( ExampleIndexedGraphFactory.getInput_AnnotationMaintaining() );
		IndexedState expected = new IndexedState(ExampleIndexedGraphFactory.getExpected_AnnotationMaintaining());
		
		AVLAnnotationMaintainingStrategy strategy = new AVLAnnotationMaintainingStrategy();
		strategy.maintainAnnotations(input);
		
		assertEquals(expected, input);
	}

}
