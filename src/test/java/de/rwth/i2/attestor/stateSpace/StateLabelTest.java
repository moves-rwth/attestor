package de.rwth.i2.attestor.stateSpace;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.rwth.i2.attestor.stateSpaceGeneration.StateLabel;

public class StateLabelTest {
	
	@Test
	public void testAPHandling(){
		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
		label1.addAP("ap2");
		
		assertTrue(label1.contains("ap2"));
	}

}
