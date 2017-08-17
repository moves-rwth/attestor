package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.Stack;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.IndexSymbol;
import de.rwth.i2.attestor.graph.GeneralNonterminal;

import java.util.List;

public class IndexedNonterminalImpl implements IndexedNonterminal {

	protected Stack stack;
	protected  final GeneralNonterminal generalNonterminal;

	public IndexedNonterminalImpl(String label,
                                  int rank,
                                  boolean[] isReductionTentacle,
                                  List<IndexSymbol> stack){

		this.generalNonterminal = GeneralNonterminal.getNonterminal( label, rank, isReductionTentacle );
		this.stack = new Stack(stack);
	}


	public IndexedNonterminalImpl(String label, List<IndexSymbol> index ){
		this.generalNonterminal = GeneralNonterminal.getNonterminal(label);
		this.stack = new Stack(index);
	}

	private IndexedNonterminalImpl(GeneralNonterminal generalNonterminal, List<IndexSymbol> index ){
		this.generalNonterminal = generalNonterminal;
        this.stack = new Stack(index);
	}

	protected IndexedNonterminalImpl(GeneralNonterminal generalNonterminal, Stack stack) {
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
    public IndexedNonterminal getWithProlongedStack(IndexSymbol s){
		return new IndexedNonterminalImpl(generalNonterminal, stack.getWithProlongedStack(s));
	}

	@Override
    public IndexedNonterminal getWithInstantiation(){
        return new IndexedNonterminalImpl(generalNonterminal, stack.getWithInstantiation());
	}

	@Override
    public IndexedNonterminal getWithProlongedStack(List<IndexSymbol> postfix){
        return new IndexedNonterminalImpl(generalNonterminal, stack.getWithProlongedStack(postfix));
	}

	@Override
    public IndexedNonterminal getWithStack(List<IndexSymbol> stack){
        return new IndexedNonterminalImpl(generalNonterminal, stack);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((generalNonterminal == null) ? 0 : generalNonterminal.hashCode());
		for(int i=0; i < stack.size(); i++) {
		    IndexSymbol symb = stack.get(i);
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
