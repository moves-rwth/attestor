package de.rwth.i2.attestor.programState.indexedState.index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Index {

    private final List<IndexSymbol> indexSymbols;

    public Index(List<IndexSymbol> indexSymbols) {

        this.indexSymbols = indexSymbols;
    }

    public Index(Index index) {

        this.indexSymbols = index.indexSymbols;
    }

    public boolean startsWith(Iterable<IndexSymbol> prefix) {

        Iterator<IndexSymbol> indexIterator = indexSymbols.iterator();
        Iterator<IndexSymbol> prefixIterator = prefix.iterator();

        while (indexIterator.hasNext() && prefixIterator.hasNext()) {
            if (!indexIterator.next().equals(prefixIterator.next())) {
                return false;
            }
        }

        return !prefixIterator.hasNext();
    }


    public boolean endsWith(IndexSymbol symbol) {

        return (!indexSymbols.isEmpty()) && indexSymbols.get(indexSymbols.size() - 1).equals(symbol);
    }


    public IndexSymbol getLastIndexSymbol() {

        assert (indexSymbols.size() > 0);
        return indexSymbols.get(indexSymbols.size() - 1);
    }

    public Index getWithShortenedIndex() {

        assert (indexSymbols.size() > 0);
        List<IndexSymbol> indexCopy = new ArrayList<>(indexSymbols);
        indexCopy.remove(indexCopy.size() - 1);
        return new Index(indexCopy);
    }

    public Index getWithProlongedIndex(IndexSymbol s) {

        List<IndexSymbol> indexCopy = new ArrayList<>(indexSymbols);
        indexCopy.add(s);
        return new Index(indexCopy);
    }

//    public Index getWithInstantiation(){
//        List<IndexSymbol> indexCopy = new ArrayList<>(indexSymbols);
//        if( indexSymbols.size() > 0 && this.getLastIndexSymbol() instanceof IndexVariable){
//            IndexVariable lastSymbol = (IndexVariable)indexCopy.get(indexCopy.size() - 1);
//            indexCopy.remove( indexCopy.size() - 1 );
//            lastSymbol.getInstantiation().forEach(indexCopy::add);
//        }
//        return new Index(indexCopy);
//    }

    public Index getWithProlongedIndex(List<IndexSymbol> postfix) {

        assert (this.size() > 0);
        IndexSymbol lastSymbol = this.getLastIndexSymbol();
        assert (!(lastSymbol instanceof ConcreteIndexSymbol));

        List<IndexSymbol> indexCopy = new ArrayList<>(indexSymbols);
        indexCopy.remove(indexCopy.size() - 1);
        indexCopy.addAll(postfix);

        return new Index(indexCopy);
    }

    public boolean hasConcreteIndex() {

        return (!indexSymbols.isEmpty()) && this.indexSymbols.get(indexSymbols.size() - 1).isBottom();
    }

    public int size() {

        return indexSymbols.size();
    }

    public boolean isEmpty() {

        return indexSymbols.isEmpty();
    }

    public boolean matchIndex(Index other) {

        List<IndexSymbol> otherIndex = other.indexSymbols;
        for (int i = 0; i < this.size() && i < otherIndex.size(); i++) {
            IndexSymbol s1 = this.get(i);
            IndexSymbol s2 = otherIndex.get(i);

            if (!s1.equals(s2)) {
                return false;
            }
        }

        return otherIndex.size() == this.size();
    }

    public IndexSymbol get(int pos) {

        return indexSymbols.get(pos);
    }

    public boolean equals(Object other) {

        if (other instanceof Index) {
            Index index = (Index) other;
            return indexSymbols.equals(index.indexSymbols);
        }
        return false;
    }

    public int hashCode() {

        return indexSymbols.hashCode();
    }

    public String toString() {

        return indexSymbols.toString();
    }

}
