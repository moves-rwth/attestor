package de.rwth.i2.attestor.modelChecking;

import java.util.LinkedList;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.generated.node.ANextLtlform;
import de.rwth.i2.attestor.generated.node.Node;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

/**
 * This class implements the states of the tableau method proof structure. Each state consists 
 * of a program state and a list of (sub)formulae, which together form an assertion, that has
 * to be discharged.
 * 
 * @author christina
 *
 */

public class Assertion {
	
	int progState;
	LinkedList<Node> formulae;
	// specifies, if the assertions is known to hold
	boolean isTrue;
	
	public Assertion(int progState){
		this.progState = progState;
		this.formulae = new LinkedList<Node>();
		isTrue = false;
	}
	
	public Assertion(int progState, LTLFormula formula){
		this.progState = progState;
		
		this.formulae = new LinkedList<Node>();
		// Note: To make sure that only "rule-nodes" are added, walk a step into the AST!
		this.formulae.add(formula.getASTRoot().getPLtlform());
		
		isTrue = false;
	}
	
	/**
	 * This constructor returns a new assertion as a copy of the provided one.
	 * Note that the new assertion receives a shallow copy of the formulae list.
	 * 
	 * @param assertion, the assertion to be copied
	 */
	public Assertion(Assertion assertion){
		this.progState = assertion.getProgramState();
		this.formulae = (LinkedList<Node>) assertion.getFormulae().clone();
		this.isTrue = assertion.isTrue();
	}

	LinkedList<Node> getFormulae() {
		return this.formulae;
	}

	public int getProgramState(){
		return this.progState;
	}
	
	public boolean isTrue() {
		return this.isTrue;
	}
	
	public Node getFirstFormula(){
		return this.formulae.getFirst();
	}
	
	public void removeFormula(Node formula){
		this.formulae.remove(formula);
	}
	
	/**
	 * This method adds a formula to the list of formulae in case it is not already contained 
	 * (otherwise no action is performed).
	 * Note that add respects the insertion order forced by the tableau method, i.e. next formulae
	 * are inserted at the back of the list, while all remaining formulae are inserted at its  
	 * beginning.
	 * 
	 * @param formula, the formula to be added
	 */
	public void addFormula(Node formula){
		if(!this.formulae.contains(formula)){
			if(formula instanceof ANextLtlform){
				this.formulae.addLast(formula);
			} else {
				this.formulae.addFirst(formula);
			}
		}
	}

	public void setTrue() {
		this.isTrue = true;
	}

	public void removeFirstFormula(Node node) {
		this.formulae.removeFirst();
		
	}
	
	public String stateIDAndFormulaeToString(){
		
		return this.progState + this.formulae.toString();
		
		
	}

}
