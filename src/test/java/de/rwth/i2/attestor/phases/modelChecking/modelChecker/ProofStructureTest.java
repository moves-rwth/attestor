package de.rwth.i2.attestor.phases.modelChecking.modelChecker;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class ProofStructureTest extends InternalStateSpace {

    private SceneObject sceneObject;
    private HeapConfiguration hc;

    public ProofStructureTest() {

        super(0);
    }

    @Before
    public void setup() {

        sceneObject = new MockupSceneObject();
        hc = sceneObject.scene().createHeapConfiguration();
    }

    @Test
    public void buildProofStructureTestAndStateform() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("({dll} & {tree})");
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.addAP("{ dll }");
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.addAP("{ tree }");
        state1.setProgramCounter(1);

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, state1);
        this.addArtificialInfPathsTransition(state1);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.build(this, formula);

        // Expected output
        assertEquals(proofStruct.getLeaves().size(), 2);
        boolean successful = true;
        for (Assertion assertion : proofStruct.getLeaves()) {
            // Make sure one leaf is not successful
            if (!assertion.isTrue()) {
                successful = false;
            }
        }
        assertFalse(successful);

        assertTrue(proofStruct.size() == 4);
        assertFalse(proofStruct.isSuccessful());
    }

    @Test
    public void buildProofStructureTestOrStateform() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("({dll} | {tree})");
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.addAP("{ dll }");
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.addAP("{ tree }");
        state1.setProgramCounter(1);

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, state1);
        this.addArtificialInfPathsTransition(state1);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        // Expected output
        assertEquals(proofStruct.getLeaves().size(), 1);
        for (Assertion assertion : proofStruct.getLeaves()) {
            // Make sure the one leaf is successful
            assertTrue(assertion.isTrue());
        }

        assertTrue(proofStruct.size() <= 3 && proofStruct.size() >= 2);
        assertTrue(proofStruct.isSuccessful());
    }

    @Test
    public void buildProofStructureTestNextLtlform() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("X {dll}");
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.addAP("{ dll }");
        state1.setProgramCounter(1);

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, state1);
        this.addArtificialInfPathsTransition(state1);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        assertTrue(proofStruct.isSuccessful());

    }


    @Test
    public void buildProofStructureTestNextNegLtlform() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("X ! {dll}");
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.addAP("{ dll }");
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.addAP("{ tree }");
        state1.setProgramCounter(1);

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, state1);
        this.addArtificialInfPathsTransition(state1);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        // Expected output
        assertEquals(proofStruct.getLeaves().size(), 1);
        for (Assertion assertion : proofStruct.getLeaves()) {
            // Make sure all leaves are successful
            assertTrue(assertion.isTrue());

        }
        assertTrue(proofStruct.size() == 2);
        assertTrue(proofStruct.isSuccessful());

    }

    @Test
    public void buildProofStructureTestUntilLtlform() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("({dll} U {tree})");
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.addAP("{ dll }");
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.addAP("{ tree }");
        state1.setProgramCounter(1);

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, state1);
        this.addArtificialInfPathsTransition(state1);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        // Expected output
        assertEquals(proofStruct.getLeaves().size(), 3);
        for (Assertion assertion : proofStruct.getLeaves()) {
            // Make sure all leaves are successful
            assertTrue(assertion.isTrue());

        }
        assertTrue(proofStruct.size() <= 9 && proofStruct.size() >= 7);
        assertTrue(proofStruct.isSuccessful());
    }

    @Test
    public void buildProofStructureTestTrueUntil() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("(true U {tree})");
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.setProgramCounter(1);

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, state1);
        this.addArtificialInfPathsTransition(state1);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        // Make sure that verification fails
        assertFalse(proofStruct.isSuccessful());
    }

    @Test
    public void buildProofStructureTestNegFinally() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("! F { tree }");
            formula.toPNF();
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.setProgramCounter(1);
        initialState.addAP("{ dll }");
        state1.addAP("{ tree }");

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, state1);
        this.addArtificialInfPathsTransition(state1);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        // Make sure that verification fails
        assertFalse(proofStruct.isSuccessful());
    }

    @Test
    public void buildProofStructureTestGloballyFinally() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("G F { tree }");
            formula.toPNF();
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.setProgramCounter(1);
        initialState.addAP("{ dll }");
        state1.addAP("{ tree }");

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, state1);
        this.addArtificialInfPathsTransition(state1);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        // Make sure that verification succeeds
        assertTrue(proofStruct.isSuccessful());
    }


    @Test
    public void buildProofStructureTestImpliesFalse() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("(F {tree} -> false)");
            formula.toPNF();
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.setProgramCounter(1);
        initialState.addAP("{ dll }");
        state1.addAP("{ tree }");

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, state1);
        this.addArtificialInfPathsTransition(state1);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        // Make sure that verification fails
        assertFalse(proofStruct.isSuccessful());
    }

    @Test
    public void buildProofStructureTestImpliesFalseLoop() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("(F {tree} -> false)");
            formula.toPNF();
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.setProgramCounter(1);
        initialState.addAP("{ dll }");
        state1.addAP("{ tree }");

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, initialState);
        this.addControlFlowTransition(initialState, state1);
        this.addArtificialInfPathsTransition(state1);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        // Make sure that verification fails
        assertFalse(proofStruct.isSuccessful());
    }

    @Test
    public void buildProofStructureTestUntilWithCycle() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("({dll} U {tree})");
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.addAP("{ dll }");

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        //this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, initialState);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        // Expected output
        assertEquals(proofStruct.getLeaves().size(), 1);
        for (Assertion assertion : proofStruct.getLeaves()) {
            // Make sure all leaves are successful
            assertTrue(assertion.isTrue());

        }
        assertTrue(proofStruct.size() >= 4 && proofStruct.size() <= 5);
        assertFalse(proofStruct.isSuccessful());
    }

    @Test
    public void buildProofStructureTestUntilOrRelease() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("(({sll} U {dll}) | ({dll} R {sll}))");
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.addAP("{ sll }");

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        this.addControlFlowTransition(initialState, initialState);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        assertEquals(proofStruct.getLeaves().size(), 2);
        for (Assertion assertion : proofStruct.getLeaves()) {
            // Make sure all leaves are successful
            assertTrue(assertion.isTrue());

        }
        assertTrue(proofStruct.isSuccessful());


    }

    @Test
    public void buildProofStructureTestUntilAndRelease() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("(({sll} U {dll}) & ({dll} R {sll}))");
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.addAP("{ sll }");

        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        //this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, initialState);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        HashSet<Assertion> leaves = proofStruct.getLeaves();
        assertEquals(2, leaves.size());
        for (Assertion assertion : leaves) {
            // Make sure all leaves are successful
            assertTrue(assertion.isTrue());

        }
        assertFalse(proofStruct.isSuccessful());
    }

    @Test
    public void buildProofStructureComplexTest() {

        LTLFormula formula = null;
        try {
            formula = new LTLFormula("X({sll} U ({tree} R X {dll}))");
        } catch (Exception e) {
            fail("Formula should parse correctly. No Parser and Lexer exception expected!");
        }

        ProgramState initialState = sceneObject.scene().createProgramState(hc);
        initialState.addAP("{ dll }");
        initialState.setProgramCounter(0);
        ProgramState state1 = sceneObject.scene().createProgramState(hc);
        state1.addAP("{ sll }");
        state1.setProgramCounter(1);
        ProgramState state2 = sceneObject.scene().createProgramState(hc);
        state2.addAP("{ tree }");
        state2.setProgramCounter(2);


        this.addStateIfAbsent(initialState);

        initialState.setProgramCounter(0);
        this.addStateIfAbsent(initialState);
        this.addInitialState(initialState);
        state1.setProgramCounter(1);
        this.addStateIfAbsent(state1);
        this.addControlFlowTransition(initialState, state1);
        this.addControlFlowTransition(state1, state1);
        state2.setProgramCounter(2);
        this.addStateIfAbsent(state2);
        this.addControlFlowTransition(state1, state2);
        this.addControlFlowTransition(state2, initialState);

        ProofStructure proofStruct = new ProofStructure();
        proofStruct.setBuildFullStructure();
        proofStruct.build(this, formula);

        // Expected output
        assertFalse(proofStruct.isSuccessful());
    }
}
