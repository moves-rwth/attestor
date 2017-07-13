package de.rwth.i2.attestor.modelChecking;

import java.util.HashSet;

import com.google.common.collect.HashBiMap;

import de.rwth.i2.attestor.generated.analysis.AnalysisAdapter;
import de.rwth.i2.attestor.generated.node.AAndStateform;
import de.rwth.i2.attestor.generated.node.AAtomicpropTerm;
import de.rwth.i2.attestor.generated.node.AFalseTerm;
import de.rwth.i2.attestor.generated.node.ANegStateform;
import de.rwth.i2.attestor.generated.node.ANextLtlform;
import de.rwth.i2.attestor.generated.node.AOrStateform;
import de.rwth.i2.attestor.generated.node.AReleaseLtlform;
import de.rwth.i2.attestor.generated.node.AStateformLtlform;
import de.rwth.i2.attestor.generated.node.ATermLtlform;
import de.rwth.i2.attestor.generated.node.ATrueTerm;
import de.rwth.i2.attestor.generated.node.AUntilLtlform;
import de.rwth.i2.attestor.generated.node.Node;
import de.rwth.i2.attestor.generated.node.PLtlform;
import de.rwth.i2.attestor.generated.node.PStateform;
import de.rwth.i2.attestor.generated.node.PTerm;
import de.rwth.i2.attestor.generated.node.Start;
import de.rwth.i2.attestor.generated.node.TNext;

/**
 * This class implements the tableau rules for the model checking.
 * To this aim a tableau rules switch holds an "in" map from subformulae to the assertion 
 * of the proof structure a subformula is checked for.
 * The assertions that result from applying the tableau rule are stored in the "out" map.
 * @author christina
 *
 */

public class TableauRulesSwitch extends AnalysisAdapter{
	
	// Holds the additional next formulae obtained while unrolling release and until
	HashBiMap<Node, ANextLtlform> additionalNextFormulae;

	public TableauRulesSwitch(){
		additionalNextFormulae = HashBiMap.create();
	}
	
	/**
	 * Care! This case should never happen, because we initialise the proof structure with the 
	 * successor node of the AST start node!
	 */
	public void caseStart(Start node) throws RuntimeException
    {
		//PLtlform current = node.getPLtlform();
		
		//this.setIn(current, this.getIn(node));
        //node.getPLtlform().apply(this);
        //this.setOut(node, this.getOut(current));
		throw (new RuntimeException());
    }
	
	/** In case we encounter a term as Ltlform, we proceed with the underlying PTerm and
	 * simply pass the "in" and "out" information through.
	 */
	public void caseATermLtlform(ATermLtlform node){
		PTerm current = node.getTerm();
		
		this.setIn(current, this.getIn(node));
		current.apply(this);
		this.setOut(node, this.getOut(current));
	}
	
	/**
	 * In case we encounter a state formula as ltl formula, we proceed with the state 
	 * formula and pass the "in" and "out" information through.
	 */
	public void caseAStateformLtlform(AStateformLtlform node){
		PStateform current = node.getStateform();
		
		this.setIn(current, this.getIn(node));
		current.apply(this);
		this.setOut(node, this.getOut(current));
	}
	
	public void caseAAtomicpropTerm(AAtomicpropTerm node)
    {
		Assertion current = (Assertion) this.getIn(node);
		
		String expectedAP = node.toString().trim();
		if(current.getProgramState().satisfiesAP(expectedAP)){
			current.setTrue();
			this.setOut(node, null);
		} else {
			removeFormulaAndSetOut(node);
		}

    
    }
	/**
	 * In case the subformula is false, we remove it from the assertion's formula
	 * list (as it can never be fulfilled).
	 */
	public void caseAFalseTerm(AFalseTerm node){
		removeFormulaAndSetOut(node);
	}
	
	public void caseATrueTerm(ATrueTerm node){
		Assertion current = (Assertion) this.getIn(node);
		current.setTrue();
		
		this.setOut(node, null);
	}
	
	/**
	 * Treat negated state formulae similar to atomic propositions, i.e.
	 * evaluate directly. This is possible as formula is required to be in PNF.
	 */
	public void caseANegStateform(ANegStateform node){
		Assertion current = (Assertion) this.getIn(node);
		
		String negExpectedAP = node.getAtomicprop().toString().trim();
		if(!current.getProgramState().satisfiesAP(negExpectedAP)){
			current.setTrue();
			this.setOut(node, null);
		} else {
			removeFormulaAndSetOut(node);
		}
	}
	
	public void caseAAndStateform(AAndStateform node){
		Assertion firstAssertion = removeFormula(node);
		Assertion secondAssertion = removeFormula(node);
		
		// Add both subformulae to different new assertions here
		firstAssertion.addFormula(node.getLeftform());
		secondAssertion.addFormula(node.getRightform());
		
		HashSet<Assertion> successors = new HashSet<Assertion>();
		successors.add(firstAssertion); successors.add(secondAssertion);
		this.setOut(node, successors);
	}
	
	public void caseAOrStateform(AOrStateform node){
		Assertion newAssertion = removeFormula(node);
		
		// Add both subformulae to one new assertion here
		newAssertion.addFormula(node.getLeftform());
		newAssertion.addFormula(node.getRightform());
		
		HashSet<Assertion> successors = new HashSet<Assertion>();
		successors.add(newAssertion); 
		this.setOut(node, successors);
	}
	
	public void caseAUntilLtlform(AUntilLtlform node){
		
		// Determine the corresponding next node
		ANextLtlform nextNode;
        
        // Check if the unrolled until formula is already present
        if(additionalNextFormulae.containsKey(node)){
        	nextNode = additionalNextFormulae.get(node);
        } else {
        	// Generate new AST node for X node
        	TNext nextToken = new TNext();
        	
        	// CARE: As violation of AST tree property is not possible, set dummy until formula
        	// Make sure that the original until formula (the hash map key) is used _at all times_
        	AUntilLtlform helperNode = new AUntilLtlform();
        	nextNode = new ANextLtlform(nextToken ,helperNode);
        	
        	// Add to hashmap
        	additionalNextFormulae.put(node, nextNode);
        
        }
        
        // Generate two new assertions with original formula removed
        Assertion newAssertion1 = removeFormula(node);
        Assertion newAssertion2 = removeFormula(node);
        		
        newAssertion1.addFormula(node.getLeftform()); newAssertion1.addFormula(node.getRightform());
        
        newAssertion2.addFormula(node.getRightform()); newAssertion2.addFormula(nextNode);
        
        HashSet<Assertion> successors = new HashSet<Assertion>();
		successors.add(newAssertion1); successors.add(newAssertion2); 
		this.setOut(node, successors);
	}
	
	public void caseAReleaseLtlform(AReleaseLtlform node){
		
		// Determine the corresponding next node
		ANextLtlform nextNode;
        
        // Check if the unrolled until formula is already present
        if(additionalNextFormulae.containsKey(node)){
        	nextNode = additionalNextFormulae.get(node);
        } else {
        	// Generate new AST node for X node
        	TNext nextToken = new TNext();
        	
        	// CARE: As violation of AST tree property is not possible, set dummy until formula
        	// Make sure that the original until formula (the hash map key) is used _at all times_
        	AUntilLtlform helperNode = new AUntilLtlform();
        	nextNode = new ANextLtlform(nextToken ,helperNode);
        	
        	// Add to hashmap
        	additionalNextFormulae.put(node, nextNode);
        }
        
        // Generate two new assertions with original formula removed
        Assertion newAssertion1 = removeFormula(node);
        Assertion newAssertion2 = removeFormula(node);
        		
        newAssertion1.addFormula(node.getRightform());
        
        newAssertion2.addFormula(node.getLeftform()); newAssertion2.addFormula(nextNode);
        
        HashSet<Assertion> successors = new HashSet<Assertion>();
		successors.add(newAssertion1); successors.add(newAssertion2); 
		this.setOut(node, successors);
        
        }
	
	/**
	 * This procedure determines the AST successor of the input node and returns it via
	 * the out-information of the switch. Note that, as next formulae are treated different 
	 * than other operaters by the tableau method, this case differs in its outputted value
	 * from the other cases (output here: AST node, other operators: set  of assertions).
	 */
	public void caseANextLtlform(ANextLtlform node){
    	// Check first, if the next formula is an original one
    	if(additionalNextFormulae.containsValue(node)){
    		// If not, proceed with the associated until formula.
    		// Note that this is a workaround, because a helper until formula was used upon generating
    		// the new next formula (to preserve the AST tree property)
    		this.setOut(node, additionalNextFormulae.inverse().get(node));
    	} else {
    		
    		this.setOut(node, node.getLtlform());
    	}
	}
	
	/**
	 * This method generates a copy of the assertion associated to node (via "in"), where node is
	 * removed and sets the "out" map for node to a newly generated set containing this copy.
	 * @param node, the node which should be removed from the assertion
	 */
	private void removeFormulaAndSetOut(Node node){
		Assertion currentCopy = removeFormula(node);
		
		HashSet<Assertion> successors = new HashSet<Assertion>();
		successors.add(currentCopy);
		this.setOut(node, successors);
	}
	
	/**
	 * This method generates a copy of the assertion associated to node (via "in") and removes the first formula from 
	 * this asserion's formulae set.
	 * @param node, the node that should be removed
	 * @return
	 */
	private Assertion removeFormula(Node node){
		Assertion current = (Assertion) this.getIn(node);
		
		Assertion currentCopy = new Assertion(current);
		currentCopy.removeFirstFormula(node);
		
		return currentCopy;
		
	}
	
}
