package de.rwth.i2.attestor.indexedGrammars;

import java.util.*;

import de.rwth.i2.attestor.indexedGrammars.stack.Stack;
import de.rwth.i2.attestor.tasks.GeneralNonterminal;

import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.indexedGrammars.stack.*;

public class IndexedNonterminalImpl implements IndexedNonterminal {

	private Stack stack;
	private final GeneralNonterminal generalNonterminal;

	public IndexedNonterminalImpl(String label,
                                  int rank,
                                  boolean[] isReductionTentacle,
                                  List<StackSymbol> stack){

		this.generalNonterminal = GeneralNonterminal.getNonterminal( label, rank, isReductionTentacle );
		this.stack = new Stack(stack);
	}


	public IndexedNonterminalImpl(String label, List<StackSymbol> index ){
		this.generalNonterminal = GeneralNonterminal.getNonterminal(label);
		this.stack = new Stack(index);
	}

	private IndexedNonterminalImpl(GeneralNonterminal generalNonterminal, List<StackSymbol> index ){
		this.generalNonterminal = generalNonterminal;
        this.stack = new Stack(index);
	}

	private IndexedNonterminalImpl(GeneralNonterminal generalNonterminal, Stack stack) {
	    this.generalNonterminal = generalNonterminal;
	    this.stack = stack;
    }

    @Override
    public Stack getStack() {
	    return stack;
    }

    @Override
    public IndexedNonterminal getWithShortenedStack(){
	    return new IndexedNonterminalImpl(generalNonterminal, stack.getWithShortenedStack());
	}

	@Override
    public IndexedNonterminal getWithProlongedStack(StackSymbol s){
		return new IndexedNonterminalImpl(generalNonterminal, stack.getWithProlongedStack(s));
	}

	@Override
    public IndexedNonterminal getWithInstantiation(){
        return new IndexedNonterminalImpl(generalNonterminal, stack.getWithInstantiation());
	}

	@Override
    public IndexedNonterminal getWithProlongedStack(List<StackSymbol> postfix){
        return new IndexedNonterminalImpl(generalNonterminal, stack.getWithProlongedStack(postfix));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((generalNonterminal == null) ? 0 : generalNonterminal.hashCode());
		for(int i=0; i < stack.size(); i++) {
		    StackSymbol symb = stack.get(i);
			result = prime * symb.hashCode();
		}
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexedNonterminal other = (IndexedNonterminal) obj;
	    return getLabel().equals(other.getLabel()) && getStack().equals(other.getStack());
	}

	@Override
	public boolean matches( NodeLabel obj ){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexedNonterminal other = (IndexedNonterminal) obj;
        return getLabel().equals(other.getLabel());
	}

	@Override
	public int getRank() {
		return generalNonterminal.getRank();
	}

	@Override
	public boolean isReductionTentacle(int tentacle) {
		return generalNonterminal.isReductionTentacle(tentacle);
	}

	@Override
	public void setReductionTentacle( int tentacle ){
		generalNonterminal.setReductionTentacle(tentacle);
	}

	@Override
	public void unsetReductionTentacle( int tentacle ){
		generalNonterminal.unsetReductionTentacle(tentacle);
	}

	@Override
	public String toString(){
		return generalNonterminal.toString() + this.stack.toString();
	}

	@Override
	public String getLabel() {
		return generalNonterminal.getLabel();
	}
}
