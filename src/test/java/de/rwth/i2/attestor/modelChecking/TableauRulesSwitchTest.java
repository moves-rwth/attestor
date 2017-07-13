package de.rwth.i2.attestor.modelChecking;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.generated.node.AAndStateform;
import de.rwth.i2.attestor.generated.node.AAtomicpropTerm;
import de.rwth.i2.attestor.generated.node.AFalseTerm;
import de.rwth.i2.attestor.generated.node.ANextLtlform;
import de.rwth.i2.attestor.generated.node.AReleaseLtlform;
import de.rwth.i2.attestor.generated.node.ANegStateform;
import de.rwth.i2.attestor.generated.node.AStateformLtlform;
import de.rwth.i2.attestor.generated.node.ATermLtlform;
import de.rwth.i2.attestor.generated.node.ATrueTerm;
import de.rwth.i2.attestor.generated.node.AUntilLtlform;
import de.rwth.i2.attestor.generated.node.Node;
import de.rwth.i2.attestor.generated.node.TAnd;
import de.rwth.i2.attestor.generated.node.TAtomicprop;
import de.rwth.i2.attestor.generated.node.TFalse;
import de.rwth.i2.attestor.generated.node.TLparen;
import de.rwth.i2.attestor.generated.node.TNeg;
import de.rwth.i2.attestor.generated.node.TRelease;
import de.rwth.i2.attestor.generated.node.TRparen;
import de.rwth.i2.attestor.generated.node.TTrue;
import de.rwth.i2.attestor.generated.node.TUntil;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabel;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultLabelledState;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

public class TableauRulesSwitchTest {
	
	@Test
	public void caseAAtomicpropTerm(){
		
		// Generate assertion

		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
		
		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState state = new DefaultLabelledState(heapconf);
		state.addLabel(label1);

		Assertion currentVertex = new Assertion(state);
		
		TAtomicprop ap = new TAtomicprop("ap1");
		AAtomicpropTerm term = new AAtomicpropTerm(ap);
		
		currentVertex.addFormula(term);
		
		// Check whether the generated assertion satisfies AP term
		TableauRulesSwitch rulesSwitch = new TableauRulesSwitch();
		rulesSwitch.setIn(term, currentVertex);
		
		term.apply(rulesSwitch);
		
		assertTrue(currentVertex.isTrue());
		
		// Reset state and rule switch with new AP
		state = new DefaultLabelledState(heapconf);
		state.addLabel(label1);

		currentVertex = new Assertion(state);
		
		TAtomicprop ap2 = new TAtomicprop("ap2");
		AAtomicpropTerm term2 = new AAtomicpropTerm(ap2);
		
		currentVertex.addFormula(term2);
		
		// Check whether the generated assertion satisfies AP term2
		rulesSwitch = new TableauRulesSwitch();
		rulesSwitch.setIn(term2, currentVertex);
		
		term2.apply(rulesSwitch);
		
		assertFalse(currentVertex.isTrue());
		HashSet<Assertion> output = (HashSet<Assertion>) rulesSwitch.getOut(term2);
		
		assertEquals(output.size(), 1);
		for(Assertion newAssertion : output){
			assertTrue(newAssertion.getFormulae().isEmpty());
		}

		
	}
	
	@Test
	public void caseALtlTerm(){
		// Generate assertion

		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
				
		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState state = new DefaultLabelledState(heapconf);
		state.addLabel(label1);

		Assertion currentVertex = new Assertion(state);
				
		TAtomicprop ap = new TAtomicprop("ap1");
		AAtomicpropTerm term = new AAtomicpropTerm(ap);
		ATermLtlform ltlTerm = new ATermLtlform(term);
				
		currentVertex.addFormula(ltlTerm);
				
		// Check whether the generated assertion satisfies AP term
		TableauRulesSwitch rulesSwitch = new TableauRulesSwitch();
		rulesSwitch.setIn(ltlTerm, currentVertex);
		
		ltlTerm.apply(rulesSwitch);
				
		assertTrue(currentVertex.isTrue());
		assertEquals(rulesSwitch.getOut(ltlTerm), null);
	}
	
	@Test
	public void caseAFalseTerm(){
		
		// Generate assertion

		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
				
		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState state = new DefaultLabelledState(heapconf);
		state.addLabel(label1);

		Assertion currentVertex = new Assertion(state);
		
		TFalse f = new TFalse();
		AFalseTerm term = new AFalseTerm(f);
				
		currentVertex.addFormula(term);
		// The current assertion holds one formulae
		assertEquals(currentVertex.getFormulae().size(), 1);
				
		// Check whether the generated assertion satisfies AP term "false"
		TableauRulesSwitch rulesSwitch = new TableauRulesSwitch();
		rulesSwitch.setIn(term, currentVertex);
				
		term.apply(rulesSwitch);
		
		// The current assertion still holds one formulae
		assertEquals(currentVertex.getFormulae().size(), 1);
		
		assertFalse(currentVertex.isTrue());
		HashSet<Assertion> output = (HashSet<Assertion>) rulesSwitch.getOut(term);
		
		assertEquals(output.size(), 1);
		for(Assertion newAssertion : output){
			assertTrue(newAssertion.getFormulae().isEmpty());
		}
		
		
		
	}
	
	@Test 
	public void caseATrueTerm(){
		// Generate assertion

		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
						
		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState state = new DefaultLabelledState(heapconf);
		state.addLabel(label1);

		Assertion currentVertex = new Assertion(state);
			
		TTrue t = new TTrue();
		ATrueTerm term = new ATrueTerm(t);
						
		currentVertex.addFormula(term);

						
		// Check whether the generated assertion satisfies AP term "true"
		TableauRulesSwitch rulesSwitch = new TableauRulesSwitch();
		rulesSwitch.setIn(term, currentVertex);
						
		term.apply(rulesSwitch);
		
		assertTrue(currentVertex.isTrue());
		assertEquals(rulesSwitch.getOut(term), null);
		
	}
	
	@Test
	public void ANegStateform(){
		// Generate assertion

		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
				
		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState state = new DefaultLabelledState(heapconf);
		state.addLabel(label1);

		Assertion currentVertex = new Assertion(state);
		
		TAtomicprop ap = new TAtomicprop("ap1");
		ANegStateform negStateForm = new ANegStateform(new TNeg(), ap);
		
		currentVertex.addFormula(negStateForm);
		
		// Check whether the generated assertion satisfies negated AP term "ap1"
		TableauRulesSwitch rulesSwitch = new TableauRulesSwitch();
		rulesSwitch.setIn(negStateForm, currentVertex);
						
		negStateForm.apply(rulesSwitch);
		
		assertFalse(currentVertex.isTrue());
		
		HashSet<Assertion> output = (HashSet<Assertion>) rulesSwitch.getOut(negStateForm);
		
		assertEquals(output.size(), 1);
		for(Assertion outputAssertion : output){
			assertTrue(outputAssertion.getFormulae().isEmpty());
		}
	}
	
	@Test
	public void AAndStateform(){
		// Generate assertion

		StateLabel label1 = new StateLabel();
		label1.addAP("ap1");
								
		HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
		DefaultLabelledState state = new DefaultLabelledState(heapconf);
		state.addLabel(label1);

		Assertion currentVertex = new Assertion(state);
		
		TAtomicprop ap1 = new TAtomicprop("ap1");
		AAtomicpropTerm term1 = new AAtomicpropTerm(ap1);
		ATermLtlform termLtl1 = new ATermLtlform(term1);
		
		TAtomicprop ap2 = new TAtomicprop("ap2");
		AAtomicpropTerm term2 = new AAtomicpropTerm(ap2);
		ATermLtlform termLtl2 = new ATermLtlform(term2);
		currentVertex.addFormula(termLtl2);
					
		AAndStateform andStateForm = new AAndStateform(new TLparen(), termLtl1, new TAnd(), termLtl2,new TRparen());	
		currentVertex.addFormula(andStateForm);


								
		// Check whether the tableau rule application returns two new assertions with the same state but term1 (term2) 
		// included instead of term1 and term2
		TableauRulesSwitch rulesSwitch = new TableauRulesSwitch();
		rulesSwitch.setIn(andStateForm, currentVertex);
								
		andStateForm.apply(rulesSwitch);
		
		assertFalse(currentVertex.isTrue());
		HashSet<Assertion> output = (HashSet<Assertion>) rulesSwitch.getOut(andStateForm);
		
		assertEquals(output.size(), 2);
		for(Assertion outputAssertion : output){
			assertTrue(outputAssertion.getFormulae().contains(termLtl1) | outputAssertion.getFormulae().contains(termLtl2));
		}

	}
	
	@Test
	public void caseAUntilLtlform(){
		
		// Generate assertion

				StateLabel label1 = new StateLabel();
				label1.addAP("ap1");
										
				HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
				DefaultLabelledState state = new DefaultLabelledState(heapconf);
				state.addLabel(label1);

				Assertion currentVertex = new Assertion(state);
				
				TAtomicprop ap1 = new TAtomicprop("ap1");
				AAtomicpropTerm term1 = new AAtomicpropTerm(ap1);
				ATermLtlform termLtl1 = new ATermLtlform(term1);
				
				TAtomicprop ap2 = new TAtomicprop("ap2");
				AAtomicpropTerm term2 = new AAtomicpropTerm(ap2);
				ATermLtlform termLtl2 = new ATermLtlform(term2);
				
				TAtomicprop ap3 = new TAtomicprop("ap3");
				AAtomicpropTerm term3 = new AAtomicpropTerm(ap3);
				ATermLtlform termLtl3 = new ATermLtlform(term3);
							
				AAndStateform andStateForm = new AAndStateform(new TLparen(), termLtl1, new TAnd(), termLtl2,new TRparen());
				AStateformLtlform andLTLForm = new AStateformLtlform(andStateForm);
				currentVertex.addFormula(andStateForm);

				AUntilLtlform untilForm = new AUntilLtlform(new TLparen(), termLtl3, new TUntil(), andLTLForm,new TRparen());
				currentVertex.addFormula(untilForm);
										
				// Check whether the tableau rule application returns two new assertions with the same state but term1 (term2) 
				// included instead of term1 and term2
				TableauRulesSwitch rulesSwitch = new TableauRulesSwitch();
				rulesSwitch.setIn(untilForm, currentVertex);
										
				untilForm.apply(rulesSwitch);
				assertFalse(currentVertex.isTrue());
				HashSet<Assertion> output = (HashSet<Assertion>) rulesSwitch.getOut(untilForm);

				assertEquals(output.size(), 2);
				
				boolean containsNextForm = false;
				for(Assertion generatedAssertion : output){
					
					assertFalse(generatedAssertion.getFormulae().contains(untilForm));
					assertEquals(generatedAssertion.getFormulae().size(), 3);
					
					for(Node node : generatedAssertion.getFormulae()){
						if(node instanceof ANextLtlform){
							containsNextForm = true;
							assertEquals(node, generatedAssertion.getFormulae().getLast());
						}
					}
				}
				assertTrue(containsNextForm);
	}
	
	@Test
	public void caseAReleaseLtlform(){
		
		// Generate assertion

				StateLabel label1 = new StateLabel();
				label1.addAP("ap1");
										
				HeapConfiguration heapconf = Settings.getInstance().factory().createEmptyHeapConfiguration();
				DefaultLabelledState state = new DefaultLabelledState(heapconf);
				state.addLabel(label1);

				Assertion currentVertex = new Assertion(state);
				
				TAtomicprop ap1 = new TAtomicprop("ap1");
				AAtomicpropTerm term1 = new AAtomicpropTerm(ap1);
				ATermLtlform termLtl1 = new ATermLtlform(term1);
				
				TAtomicprop ap2 = new TAtomicprop("ap2");
				AAtomicpropTerm term2 = new AAtomicpropTerm(ap2);
				ATermLtlform termLtl2 = new ATermLtlform(term2);
				currentVertex.addFormula(termLtl2);
				
				TAtomicprop ap3 = new TAtomicprop("ap3");
				AAtomicpropTerm term3 = new AAtomicpropTerm(ap3);
				ATermLtlform termLtl3 = new ATermLtlform(term3);
							
				AAndStateform andStateForm = new AAndStateform(new TLparen(), termLtl1, new TAnd(), termLtl2,new TRparen());
				AStateformLtlform andLTLForm = new AStateformLtlform(andStateForm);
				

				AReleaseLtlform releaseForm = new AReleaseLtlform(new TLparen(), termLtl3, new TRelease(), andLTLForm,new TRparen());
				currentVertex.addFormula(releaseForm);
										
				// Check whether the tableau rule application returns two new assertions with the same state but term1 (term2) 
				// included instead of term1 and term2
				TableauRulesSwitch rulesSwitch = new TableauRulesSwitch();
				rulesSwitch.setIn(releaseForm, currentVertex);
										
				releaseForm.apply(rulesSwitch);
				assertFalse(currentVertex.isTrue());
				HashSet<Assertion> output = (HashSet<Assertion>) rulesSwitch.getOut(releaseForm);

				assertEquals(output.size(), 2);
				
				boolean containsNextForm = false;
				for(Assertion generatedAssertion : output){
					
					assertFalse(generatedAssertion.getFormulae().contains(releaseForm));
					
					for(Node node : generatedAssertion.getFormulae()){
						if(node instanceof ANextLtlform){
							containsNextForm = true;
							assertEquals(node, generatedAssertion.getFormulae().getLast());
							assertEquals(generatedAssertion.getFormulae().size(), 3);

						}
						
						if(node.equals(andLTLForm)){
							assertEquals(generatedAssertion.getFormulae().size(), 2);
						}
					}
				}
				assertTrue(containsNextForm);
	}


}
