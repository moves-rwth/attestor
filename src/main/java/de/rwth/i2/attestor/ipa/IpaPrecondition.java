package de.rwth.i2.attestor.ipa;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.PreconditionChecker;
import gnu.trove.list.array.TIntArrayList;

public class IpaPrecondition {

	HeapConfiguration config;

	PreconditionChecker lastUsedChecker; //to avoid double computations
	
	public IpaPrecondition(HeapConfiguration precondition) {
		super();
		this.config = precondition;
	}
	
	public HeapConfiguration getHeap(){
		return config;
	}

	/**
	 * This is effectively the hashCode of HeapConfigurations.
	 * The equality function we use here, differs only in the comparison of external nodes,
	 * where we ignore the ordering of external nodes.
	 * As the hashCode only takes the *number* of external nodes into account, this is still
	 * a valid hashCode.
	 */
	@Override
	public int hashCode() {
		return ((config == null) ? 0 : config.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IpaPrecondition other = (IpaPrecondition) obj;
		if (config == null) {
			if (other.config != null)
				return false;
		} else{
			PreconditionChecker checker = new PreconditionChecker( config, other.getHeap() );
			if( checker.hasMatching() ){
				lastUsedChecker = checker;
				return true;
			}
			
		}
		return false;
	}
	
	/**
	 * Computes the reordering of external nodes necessary to match the two configs.
	 * For an example, consider the following two graphs:
	 * (1) -> (2) -> (3)  <br>
	 * (2) -> (1) -> (3) <br>
	 * The resulting reordering would be [2,1,3]
	 * @param matchingConfig The configuration that matches this precondition up do external reordering
	 * @return an array specifying the necessary reordering
	 * @throws IllegalArgumentException if the matchingConfig does *not* already match the
	 * precondition up do external reordering.
	 */
	public int[] getReordering( HeapConfiguration matchingConfig ) throws IllegalArgumentException{
		PreconditionChecker checker = lastUsedChecker;
		if( checker.getTarget() != matchingConfig ){ //checker is not valid for this input
			checker = new PreconditionChecker(config, matchingConfig);
			if( ! checker.hasMatching() ){
				throw new IllegalArgumentException(); //input has to be matching
			}
		}
		
		Matching matching = checker.getMatching();
		TIntArrayList externalNodes = config.externalNodes();
		int[] result = new int[externalNodes.size()];
		for( int i = 0; i < externalNodes.size(); i++ ){
			
			int matchingNode = matching.match( externalNodes.get(i) );
			result[i] = matchingConfig.externalIndexOf( matchingNode );
		}
		
		return result;
	}
	
	public String toString() {
		return "Precondition:" + config.toString();
	}
}
