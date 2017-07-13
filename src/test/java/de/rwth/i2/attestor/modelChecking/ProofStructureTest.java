package de.rwth.i2.attestor.modelChecking;

import static org.junit.Assert.fail;

import org.junit.*;
import static org.junit.Assert.*;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabel;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultLabelledState;

@Ignore
public class ProofStructureTest extends StateSpace {
	
	public ProofStructureTest(){
		super();
	}
	
	@Test
	public void buildProofStructureTestAndStateform(){
		
		LTLFormula formula = null;
		try{
			formula = new LTLFormula("(ap1 & ap2)");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}
		
		// Create dummy state space
		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
		StateLabel label2 = new StateLabel();
		label2.addAP("ap2");

		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState initialState = new DefaultLabelledState(heapconf);
		initialState.addLabel(label1);
		DefaultLabelledState state1 = new DefaultLabelledState(heapconf);
		state1.addLabel(label2);
		
		this.addState(initialState);
		this.addInitialState(initialState);
		this.addState(state1);
		this.addControlFlowSuccessor(initialState, "stmt1", state1);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
		// Expected output
		assertEquals(proofStruct.getLeaves().size(), 2);
		boolean successful = true;
		for(Assertion assertion : proofStruct.getLeaves()){
			// Make sure one leaf is not successful
			if(!assertion.isTrue()){
				successful = false;
			}
		}
		assertFalse(successful);
		
		assertTrue(proofStruct.size() == 4);
		assertFalse(proofStruct.isSuccessful());
	}
	
	@Test
	public void buildProofStructureTestOrStateform(){
		
		LTLFormula formula = null;
		try{
			formula = new LTLFormula("(ap1 | ap2)");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}
		
		// Create dummy state space
		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
		StateLabel label2 = new StateLabel();
		label2.addAP("ap2");

		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState initialState = new DefaultLabelledState(heapconf);
		initialState.addLabel(label1);
		DefaultLabelledState state1 = new DefaultLabelledState(heapconf);
		state1.addLabel(label2);
		
		this.addState(initialState);
		this.addInitialState(initialState);
		this.addState(state1);
		this.addControlFlowSuccessor(initialState, "stmt1", state1);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
		// Expected output
		assertEquals(proofStruct.getLeaves().size(), 1);
		for(Assertion assertion : proofStruct.getLeaves()){
			// Make sure the one leaf is successful
			assertTrue(assertion.isTrue());
		}
		
		assertTrue(proofStruct.size() <= 3 && proofStruct.size() >= 2);
		assertTrue(proofStruct.isSuccessful());
	}
	
	@Test 
	public void buildProofStructureTestNextNegLtlform(){
		
		LTLFormula formula = null;
		try{
			formula = new LTLFormula("X ! ap1");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}
		
		// Create dummy state space
		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
		StateLabel label2 = new StateLabel();
		label2.addAP("ap2");

		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState initialState = new DefaultLabelledState(heapconf);
		initialState.addLabel(label1);
		DefaultLabelledState state1 = new DefaultLabelledState(heapconf);
		state1.addLabel(label2);
		
		this.addState(initialState);
		this.addInitialState(initialState);
		this.addState(state1);
		this.addControlFlowSuccessor(initialState, "stmt1", state1);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
		// Expected output
		assertEquals(proofStruct.getLeaves().size(), 1);
		for(Assertion assertion : proofStruct.getLeaves()){
			// Make sure all leaves are successful
			assertTrue(assertion.isTrue());
			
		}
		assertTrue(proofStruct.size() == 2);
		assertTrue(proofStruct.isSuccessful());
		
	}
	
	@Test
	public void buildProofStructureTestUntilLtlform(){
		
		LTLFormula formula = null;
		try{
			formula = new LTLFormula("(ap1 U ap2)");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}
		
		// Create dummy state space
		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
		StateLabel label2 = new StateLabel();
		label2.addAP("ap2");

		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState initialState = new DefaultLabelledState(heapconf);
		initialState.addLabel(label1);
		DefaultLabelledState state1 = new DefaultLabelledState(heapconf);
		state1.addLabel(label2);
		
		this.addState(initialState);
		this.addInitialState(initialState);
		this.addState(state1);
		this.addControlFlowSuccessor(initialState, "stmt1", state1);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
		// Expected output
		assertEquals(proofStruct.getLeaves().size(), 3);
		for(Assertion assertion : proofStruct.getLeaves()){
			// Make sure all leaves are successful
			assertTrue(assertion.isTrue());
			
		}
		assertTrue(proofStruct.size() <= 9 && proofStruct.size() >= 7);
		assertTrue(proofStruct.isSuccessful());
	}
	
	@Test
	public void buildProofStructureTestUntilWithCycle(){
		LTLFormula formula = null;
		try{
			formula = new LTLFormula("(ap1 U ap2)");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}
		
		// Create dummy state space
		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
		StateLabel label2 = new StateLabel();
		label2.addAP("ap2");

		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState initialState = new DefaultLabelledState(heapconf);
		initialState.addLabel(label1);
		//DefaultLabelledState state1 = new DefaultLabelledState(heapconf);
		//state1.addLabel(label2);
		
		this.addState(initialState);
		this.addInitialState(initialState);
		//this.addState(state1);
		this.addControlFlowSuccessor(initialState, "stmt1", initialState);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
		// Expected output
		assertEquals(proofStruct.getLeaves().size(), 1);
		for(Assertion assertion : proofStruct.getLeaves()){
			// Make sure all leaves are successful
			assertTrue(assertion.isTrue());
			
		}
		assertTrue(proofStruct.size() >= 4 && proofStruct.size() <= 5);
		assertFalse(proofStruct.isSuccessful());
	}
	
	@Test 
	public void buildProofStructureTestUntilOrRelease(){
		LTLFormula formula = null;
		try{
			formula = new LTLFormula("((ap1 U ap2) | (ap2 R ap1))");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}
		
		// Create dummy state space
		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
		StateLabel label2 = new StateLabel();
		label2.addAP("ap2");

		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState initialState = new DefaultLabelledState(heapconf);
		initialState.addLabel(label1);
		//DefaultLabelledState state1 = new DefaultLabelledState(heapconf);
		//state1.addLabel(label2);
		
		this.addState(initialState);
		this.addInitialState(initialState);
		//this.addState(state1);
		this.addControlFlowSuccessor(initialState, "stmt1", initialState);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
		// Expected output
		assertEquals(proofStruct.getLeaves().size(), 2);
		for(Assertion assertion : proofStruct.getLeaves()){
			// Make sure all leaves are successful
			assertTrue(assertion.isTrue());
			
		}
		assertTrue(proofStruct.isSuccessful());
		
		
	}
	
	@Test 
	public void buildProofStructureTestUntilAndRelease(){
		LTLFormula formula = null;
		try{
			formula = new LTLFormula("((ap1 U ap2) & (ap2 R ap1))");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}
		
		// Create dummy state space
		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
		StateLabel label2 = new StateLabel();
		label2.addAP("ap2");

		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState initialState = new DefaultLabelledState(heapconf);
		initialState.addLabel(label1);
		//DefaultLabelledState state1 = new DefaultLabelledState(heapconf);
		//state1.addLabel(label2);
		
		this.addState(initialState);
		this.addInitialState(initialState);
		//this.addState(state1);
		this.addControlFlowSuccessor(initialState, "stmt1", initialState);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
		// Expected output
		assertEquals(proofStruct.getLeaves().size(), 2);
		for(Assertion assertion : proofStruct.getLeaves()){
			// Make sure all leaves are successful
			assertTrue(assertion.isTrue());
			
		}
		assertFalse(proofStruct.isSuccessful());
	}
	
	@Test
	public void buildProofStructureComplexTest(){
		
		LTLFormula formula = null;
		try{
			formula = new LTLFormula("X(ap1 U (ap3 R X ap2))");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}
		
		// Create dummy state space
		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
		label1.addAP("ap3");
		StateLabel label2 = new StateLabel();
		label2.addAP("ap2");

		HeapConfiguration heapconf1 = Settings.getInstance().factory().createEmptyHeapConfiguration();
		HeapConfiguration heapconf2 = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState initialState = new DefaultLabelledState(heapconf1);
		initialState.addLabel(label2);
		DefaultLabelledState state1 = new DefaultLabelledState(heapconf2);
		state1.addLabel(label1);
		DefaultLabelledState state2 = new DefaultLabelledState(heapconf2);
		state2.addLabel(label2);
		
		
		this.addState(initialState);
		initialState.setProgramCounter(0);
		this.addInitialState(initialState);
		this.addState(state1);
		state1.setProgramCounter(1);
		this.addControlFlowSuccessor(initialState, "stmt1", state1);
		this.addControlFlowSuccessor(state1, "stmt2", state1);
		this.addState(state2);
		state2.setProgramCounter(2);
		this.addControlFlowSuccessor(state1, "stmt3", state2);
		this.addControlFlowSuccessor(state2, "stmt4", initialState);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
		// Expected output
		assertFalse(proofStruct.isSuccessful());
	}
}
