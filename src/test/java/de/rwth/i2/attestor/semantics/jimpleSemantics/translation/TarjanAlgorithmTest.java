package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import de.rwth.i2.attestor.ipa.IpaAbstractMethod;

public class TarjanAlgorithmTest {

	//only single, non-recursive method
	@Test
	public void testMarkRecursiveMethodsTrivial() {
		IpaAbstractMethod m1 = new IpaAbstractMethod("non-recursive 1");
		TarjanAlgorithm algorithm = new TarjanAlgorithm();
		algorithm.addMethodAsVertex(m1);
		
		algorithm.markRecursiveMethods();
		
		assertFalse( m1.isRecursive() );
	}

}
