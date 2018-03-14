package de.rwth.i2.attestor.programState.indexedState;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.programState.indexedState.index.Index;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;

public class IndexedNonterminalImpl implements IndexedNonterminal {


    protected final Index index;
    protected final Nonterminal basicNonterminal;

    public IndexedNonterminalImpl(Nonterminal basicNonterminal, List<IndexSymbol> index) {

        this.basicNonterminal = basicNonterminal;
        this.index = new Index(index);
    }

    protected IndexedNonterminalImpl(Nonterminal basicNonterminal, Index index) {

        this.basicNonterminal = basicNonterminal;
        this.index = index;
    }

    @Override
    public Index getIndex() {

        return index;
    }

    @Override
    public IndexedNonterminal getWithShortenedIndex() {

        return new IndexedNonterminalImpl(basicNonterminal, index.getWithShortenedIndex());
    }

    @Override
    public IndexedNonterminal getWithProlongedIndex(IndexSymbol s) {

        return new IndexedNonterminalImpl(basicNonterminal, index.getWithProlongedIndex(s));
    }


    @Override
    public IndexedNonterminal getWithProlongedIndex(List<IndexSymbol> postfix) {

        return new IndexedNonterminalImpl(basicNonterminal, index.getWithProlongedIndex(postfix));
    }

    @Override
    public IndexedNonterminal getWithIndex(List<IndexSymbol> index) {

        return new IndexedNonterminalImpl(basicNonterminal, index);
    }

    @Override
    public int hashCode() {
        //final int prime = 31;
        int result = 1;
        result = result ^ ((basicNonterminal == null) ? 0 : basicNonterminal.hashCode());

        for (int i = 0; i < index.size(); i++) {
            IndexSymbol symb = index.get(i);
            result = (result << 1) ^ symb.hashCode();
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
    public boolean matches(NodeLabel obj) {

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

        return basicNonterminal.getRank();
    }

    @Override
    public boolean isReductionTentacle(int tentacle) {

        return basicNonterminal.isReductionTentacle(tentacle);
    }

    @Override
    public void setReductionTentacle(int tentacle) {

        basicNonterminal.setReductionTentacle(tentacle);
    }

    @Override
    public void unsetReductionTentacle(int tentacle) {

        basicNonterminal.unsetReductionTentacle(tentacle);
    }

    @Override
    public String toString() {

        return basicNonterminal.toString() + this.index.toString();
    }

    @Override
    public String getLabel() {

        return basicNonterminal.getLabel();
    }

	@Override
	public Collection<Integer> reachableTentaclesFrom(int tentacle) {
		return basicNonterminal.reachableTentaclesFrom(tentacle);
	}

	@Override
	public void setReachableTentacles(Map< Integer, Collection<Integer>> reachabilityMap) {
		basicNonterminal.setReachableTentacles(reachabilityMap);
	}

}
