package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;

public class MockupNonterminal implements Nonterminal {

	private final String label;
	private final int rank;
	
	public MockupNonterminal(String label, int rank) {
		
		this.label = label;
		this.rank = rank;
	}
	
	@Override
	public int compareTo(Nonterminal o) {
		
		return label.compareTo(o.toString()); 
		
	}

	@Override
	public int getRank() {
		
		return rank;
	}

	@Override
	public boolean isReductionTentacle(int tentacle) {
		
		return false;
	}
	
	public String toString() {
		return label;
	}

	@Override
	public void setReductionTentacle(int tentacle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unsetReductionTentacle(int tentacle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean labelMatches(Nonterminal nonterminal) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
