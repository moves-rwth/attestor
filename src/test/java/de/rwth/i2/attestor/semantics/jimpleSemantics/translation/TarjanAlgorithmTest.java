package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.rwth.i2.attestor.ipa.IpaAbstractMethod;

public class TarjanAlgorithmTest {

	//only single, non-recursive method
	@Test
	public void testTrivial() {
		TarjanAlgorithm algorithm = new TarjanAlgorithm();
		
		IpaAbstractMethod m1 = new IpaAbstractMethod("non-recursive 1");
		algorithm.addMethodAsVertex(m1);
	
		
		algorithm.markRecursiveMethods();
		
		assertFalse( m1.isRecursive() );
	}
	
	@Test
	public void testCallChain() {
		TarjanAlgorithm algorithm = new TarjanAlgorithm();
		
		IpaAbstractMethod m1 = new IpaAbstractMethod("non-recursive 1");
		algorithm.addMethodAsVertex(m1);
		
		IpaAbstractMethod m2 = new IpaAbstractMethod("non-recursive 2");
		algorithm.addMethodAsVertex(m2);
		algorithm.addCallEdge(m1, m2);
		
		IpaAbstractMethod m3 = new IpaAbstractMethod("non-recursive 3");
		algorithm.addMethodAsVertex(m3);
		algorithm.addCallEdge(m2, m3);
		
		algorithm.markRecursiveMethods();
		
		assertFalse( m1.isRecursive() );
		assertFalse( m2.isRecursive() );
		assertFalse( m3.isRecursive() );
	}
	
	@Test
	public void testDirectRecursion() {
		TarjanAlgorithm algorithm = new TarjanAlgorithm();
		
		IpaAbstractMethod m1 = new IpaAbstractMethod("recursive 1");
		algorithm.addMethodAsVertex(m1);
		algorithm.addCallEdge(m1, m1);
	
		
		algorithm.markRecursiveMethods();
		
		assertTrue( m1.isRecursive() );
	}

	
	@Test
	public void testIndirectRecursion() {
		TarjanAlgorithm algorithm = new TarjanAlgorithm();
		
		IpaAbstractMethod m1 = new IpaAbstractMethod("recursive 1");
		algorithm.addMethodAsVertex(m1);
		
		IpaAbstractMethod m2 = new IpaAbstractMethod("recursive 2");
		algorithm.addMethodAsVertex(m2);
		algorithm.addCallEdge(m1, m2);
		
		IpaAbstractMethod m3 = new IpaAbstractMethod("recursive 3");
		algorithm.addMethodAsVertex(m3);
		algorithm.addCallEdge(m2, m3);
		algorithm.addCallEdge(m3, m1);
		
		algorithm.markRecursiveMethods();
		
		assertTrue( m1.isRecursive() );
		assertTrue( m2.isRecursive() );
		assertTrue( m3.isRecursive() );
	}
	
	@Test
	public void testMixedSetting() {
		TarjanAlgorithm algorithm = new TarjanAlgorithm();
		
		IpaAbstractMethod m1 = new IpaAbstractMethod("recursive 1");
		algorithm.addMethodAsVertex(m1);
		
		IpaAbstractMethod m2 = new IpaAbstractMethod("recursive 2");
		algorithm.addMethodAsVertex(m2);
		algorithm.addCallEdge(m1, m2);
		
		IpaAbstractMethod m3 = new IpaAbstractMethod("recursive 3");
		algorithm.addMethodAsVertex(m3);
		algorithm.addCallEdge(m2, m3);
		algorithm.addCallEdge(m3, m3);
		
		IpaAbstractMethod m4 = new IpaAbstractMethod("recursive 4");
		algorithm.addMethodAsVertex(m4);
		algorithm.addCallEdge(m3, m4);
		
		algorithm.markRecursiveMethods();
		
		assertFalse("m1", m1.isRecursive() );
		assertFalse("m2", m2.isRecursive() );
		assertTrue("m3", m3.isRecursive() );
		assertFalse("m4", m4.isRecursive() );
		
	}

}
