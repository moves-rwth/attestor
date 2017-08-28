package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import de.rwth.i2.attestor.graph.GeneralNonterminal;
import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.Index;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;

import java.util.List;

public class IndexedNonterminalImpl implements IndexedNonterminal {


	protected Index index;
	protected  final GeneralNonterminal generalNonterminal;

	public IndexedNonterminalImpl(String label,
                                  int rank,
                                  boolean[] isReductionTentacle,
                                  List<IndexSymbol> index){

		this.generalNonterminal = GeneralNonterminal.getNonterminal( label, rank, isReductionTentacle );

		this.index = new Index(index);
	}


	public IndexedNonterminalImpl(String label, List<IndexSymbol> index ){
		this.generalNonterminal = GeneralNonterminal.getNonterminal(label);
		this.index = new Index(index);
	}

	private IndexedNonterminalImpl(GeneralNonterminal generalNonterminal, List<IndexSymbol> index ){
		this.generalNonterminal = generalNonterminal;

        this.index = new Index(index);
	}

	protected IndexedNonterminalImpl(GeneralNonterminal generalNonterminal, Index index) {

	    this.generalNonterminal = generalNonterminal;
	    this.index = index;
    }

    @Override
    public Index getIndex() {
	    return index;
    }

    @Override
    public IndexedNonterminal getWithShortenedIndex(){
	    return new IndexedNonterminalImpl(generalNonterminal, index.getWithShortenedIndex());
	}

	@Override
    public IndexedNonterminal getWithProlongedIndex(IndexSymbol s){

		return new IndexedNonterminalImpl(generalNonterminal, index.getWithProlongedIndex(s));
	}

	@Override
    public IndexedNonterminal getWithInstantiation(){
        return new IndexedNonterminalImpl(generalNonterminal, index.getWithInstantiation());
	}

	@Override
    public IndexedNonterminal getWithProlongedIndex(List<IndexSymbol> postfix){

        return new IndexedNonterminalImpl(generalNonterminal, index.getWithProlongedIndex(postfix));
	}

	@Override
    public IndexedNonterminal getWithIndex(List<IndexSymbol> index){
        return new IndexedNonterminalImpl(generalNonterminal, index);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((generalNonterminal == null) ? 0 : generalNonterminal.hashCode());

		for(int i = 0; i < index.size(); i++) {
		    IndexSymbol symb = index.get(i);
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
	    return getLabel().equals(other.getLabel()) && getIndex().equals(other.getIndex());
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
		return generalNonterminal.toString() + this.index.toString();
	}

	@Override
	public String getLabel() {
		return generalNonterminal.getLabel();
	}

}
