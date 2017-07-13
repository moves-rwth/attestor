package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.HashSet;

/**
 * A state label provides the atomic propositions, that hold in an associated program state.
 * @author christina
 *
 */

public class StateLabel {
	HashSet<String> propositionSet;
	
	public StateLabel(){
		propositionSet = new HashSet<String>();
	}
	
	public boolean contains(String ap){
		return propositionSet.contains(ap);
		
	}
	
	/**
	 * Adds an element to the set of propositions. If the element is already present, no action
	 * is performed.
	 * 
	 * @param ap, the proposition to add
	 */
	public void addAP(String ap){
		propositionSet.add(ap);
	}
}
