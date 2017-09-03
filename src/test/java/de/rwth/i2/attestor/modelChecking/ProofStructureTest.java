package de.rwth.i2.attestor.modelChecking;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.stateSpaceGeneration.stateSpace.InternalStateSpace;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProofStructureTest extends InternalStateSpace {

	private HeapConfiguration hc;

	public ProofStructureTest(){
		super(0);
	}

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setup() {

		hc = Settings.getInstance().factory().createEmptyHeapConfiguration();
	}

	@Test
	public void buildProofStructureTestAndStateform(){
		
		LTLFormula formula = null;
		try{
			formula = new LTLFormula("({dll} & {tree})");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}

		DefaultProgramState initialState = new DefaultProgramState(hc);
		initialState.addAP("{ dll }");
		DefaultProgramState state1 = new DefaultProgramState(hc);
		state1.addAP("{ tree }");

		this.addState(initialState);
		this.addInitialState(initialState);
		this.addState(state1);
		this.addControlFlowTransition(initialState, state1);
		
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
			formula = new LTLFormula("({dll} | {tree})");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}

		DefaultProgramState initialState = new DefaultProgramState(hc);
		initialState.addAP("{ dll }");
		DefaultProgramState state1 = new DefaultProgramState(hc);
		state1.addAP("{ tree }");

		this.addState(initialState);
		this.addInitialState(initialState);
		this.addState(state1);
		this.addControlFlowTransition(initialState, state1);
		
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
			formula = new LTLFormula("X ! {dll}");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}

		DefaultProgramState initialState = new DefaultProgramState(hc);
		initialState.addAP("{ dll }");
		DefaultProgramState state1 = new DefaultProgramState(hc);
		state1.addAP("{ tree }");
		
		this.addState(initialState);
		this.addInitialState(initialState);
		this.addState(state1);
		this.addControlFlowTransition(initialState, state1);
		
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
			formula = new LTLFormula("({dll} U {tree})");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}

		DefaultProgramState initialState = new DefaultProgramState(hc);
		initialState.addAP("{ dll }");
		DefaultProgramState state1 = new DefaultProgramState(hc);
		state1.addAP("{ tree }");

		this.addState(initialState);
		this.addInitialState(initialState);
		this.addState(state1);
		this.addControlFlowTransition(initialState, state1);
		
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
			formula = new LTLFormula("({dll} U {tree})");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}
		
		DefaultProgramState initialState = new DefaultProgramState(hc);
		initialState.addAP("{ dll }");

		this.addState(initialState);
		this.addInitialState(initialState);
		//this.addState(state1);
		this.addControlFlowTransition(initialState, initialState);
		
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
			formula = new LTLFormula("(({sll} U {dll}) | ({dll} R {sll}))");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}

		DefaultProgramState initialState = new DefaultProgramState(hc);
		initialState.addAP("{ sll }");

		this.addState(initialState);
		this.addInitialState(initialState);
		this.addControlFlowTransition(initialState, initialState);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
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
			formula = new LTLFormula("(({sll} U {dll}) & ({dll} R {sll}))");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}
		
		DefaultProgramState initialState = new DefaultProgramState(hc);
		initialState.addAP("{ sll }");

		this.addState(initialState);
		this.addInitialState(initialState);
		//this.addState(state1);
		this.addControlFlowTransition(initialState, initialState);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
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
			formula = new LTLFormula("X({sll} U ({tree} R X {dll}))");
		} catch(Exception e) {
			fail("Formula should parse correctly. No Parser and Lexer exception expected!");
		}

		DefaultProgramState initialState = new DefaultProgramState(hc);
		initialState.addAP("{ dll }");
		DefaultProgramState state1 = new DefaultProgramState(hc);
		state1.addAP("{ sll }");
		DefaultProgramState state2 = new DefaultProgramState(hc);
		state2.addAP("{ tree }");
		
		
		this.addState(initialState);

		initialState.setProgramCounter(0);
		this.addState(initialState);
		this.addInitialState(initialState);
		state1.setProgramCounter(1);
		this.addState(state1);
		this.addControlFlowTransition(initialState, state1);
		this.addControlFlowTransition(state1, state1);
		state2.setProgramCounter(2);
		this.addState(state2);
		this.addControlFlowTransition(state1, state2);
		this.addControlFlowTransition(state2, initialState);
		
		ProofStructure proofStruct = new ProofStructure();
		proofStruct.build(this, formula);
		
		// Expected output
		assertFalse(proofStruct.isSuccessful());
	}
}
